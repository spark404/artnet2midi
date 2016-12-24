package net.strocamp.artnet;

import net.strocamp.artnet.packets.ArtDmx;
import net.strocamp.artnet.packets.ArtNetPacket;
import net.strocamp.artnet.packets.ArtPollReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ArtNetNode implements ArtNetNodeMBean {
    private final static Logger logger = LoggerFactory.getLogger(ArtNetNode.class);

    private List<DmxHandler> handlers;
    private Thread handlerThread;
    private DatagramSocket artNetSocket;
    private Map<String, ArtNetNodeInfo> discoveredNodes = new ConcurrentHashMap<>();

    private volatile boolean terminate = false;

    private NetworkInterface networkInterface;
    private InterfaceAddress interfaceAddress;
    private int network;
    private int subnetwork;
    private int universe;

    @Autowired
    public ArtNetNode(List<DmxHandler> dmxHandlers) {
        handlers = new ArrayList<>();
        handlers.addAll(dmxHandlers);
    }

    public void setNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
    }

    public void setInterfaceAddress(InterfaceAddress interfaceAddress) {
        this.interfaceAddress = interfaceAddress;
    }

    public void setNetwork(int network) {
        this.network = network;
    }

    public void setSubnetwork(int subnetwork) {
        this.subnetwork = subnetwork;
    }

    public void setUniverse(int universe) {
        this.universe = universe;
    }

    public void start() throws ArtNetException {
        if (handlerThread != null)  {
            throw new ArtNetException("Node already started");
        }
        logger.info("Starting ArtNetNode on " + interfaceAddress.toString());
        Runnable artNetRunner = () -> {
            try {
                handler();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        handlerThread = new Thread(artNetRunner);
        handlerThread.setName("ArtNetHandler-" + interfaceAddress.toString());
        handlerThread.start();
        logger.info("ArtNetNode on " + interfaceAddress.toString() + " started");
    }

    public void stop() {
        if (handlerThread != null) {
            logger.info("Stopping ArtNetNode on " + interfaceAddress.toString());
            terminate = true;
            artNetSocket.close();
            try {
                handlerThread.join();
            } catch (InterruptedException e) {
                logger.error("Thread was interrupted while waiting for it to stop", e);
            }
            handlerThread = null;
            terminate  = false;
            logger.info("ArtNetNode on " + interfaceAddress.toString() + " stopped");
        }
    }

    private void handler() throws Exception {
        artNetSocket = new DatagramSocket(6454);//, interfaceAddress.getAddress());
        artNetSocket.setBroadcast(true);
        byte[] receiveData = new byte[1024];

        // According to the spec, start off with ArtPollReply broadcast
        DatagramPacket datagramPacket = generateArtPollReply(artNetSocket);
        artNetSocket.send(datagramPacket);

        while (!terminate) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            artNetSocket.receive(receivePacket);
            ArtNetPacket artNetPacket = ArtNetPacketParser.parse(receiveData);

            if (artNetPacket == null) {
                continue;
            }
            //logger.info("hex dump: {}", Util.bytesToHex(artNetPacket.getData()));

            if (artNetPacket.getOpCode() == ArtNetOpCodes.OpPoll) {
                logger.info("Poll received from {}", receivePacket.getAddress().toString());
                datagramPacket = generateArtPollReply(artNetSocket);
                artNetSocket.send(datagramPacket);
            }

            if (artNetPacket.getOpCode() == ArtNetOpCodes.OpPollReply) {
                ArtPollReply artPollReply = (ArtPollReply) artNetPacket;
                handleArtPollReply(artPollReply);
            }

            if (artNetPacket.getOpCode() == ArtNetOpCodes.OpDmx) {
                ArtDmx dmxPacket = (ArtDmx) artNetPacket;
                logger.info("DMX data received for {}:{}:{}, {} bytes",
                        dmxPacket.getNetwork(), dmxPacket.getSubnet(), dmxPacket.getUniverse(),
                        dmxPacket.getDmxLength());
                if (dmxPacket.getNetwork() == network && dmxPacket.getSubnet() == subnetwork) {
                    handleDmxData(dmxPacket);
                }
            }
        }

        if (!artNetSocket.isClosed()) {
            artNetSocket.close();
        }
    }

    private void handleArtPollReply(ArtPollReply artPollReply) {
        logger.info("Poll reply seen from {}", artPollReply.getShortName());

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss,SSS", Locale.getDefault());
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        String lastSeen = sdf.format(calendar.getTime());

        if (discoveredNodes.containsKey(artPollReply.getShortName())) {
            discoveredNodes.get(artPollReply.getShortName()).setLastSeen(lastSeen);
        } else {
            discoveredNodes.put(artPollReply.getShortName(), new ArtNetNodeInfo(artPollReply.getShortName(), lastSeen));
        }
        new ArtNetNodeInfo(artPollReply.getShortName(), lastSeen);
    }

    private DatagramPacket generateArtPollReply(DatagramSocket artNetSocket) throws ArtNetException, SocketException {
        ArtPollReply artPollReply = (ArtPollReply) ArtNetPacketParser.generatePacketByOpCode(ArtNetOpCodes.OpPollReply);
        artPollReply
                .setNetSwitch(network, subnetwork)
                .setIpAddress(interfaceAddress.getAddress().getAddress())
                .setMacAddress(networkInterface.getHardwareAddress())
                .setUniverseForInputPort(1, universe);
        return new DatagramPacket(artPollReply.getData(), artPollReply.getLength(), interfaceAddress.getBroadcast(), 0x1936);
    }

    private void handleDmxData(ArtDmx dmxPacket) {
        for (DmxHandler handlerEntry : handlers) {
            if (handlerEntry.getUniverse() != dmxPacket.getUniverse()) {
                return;
            }

            int startPosition = handlerEntry.getAddress();
            int length = handlerEntry.getWidth();

            // TODO length checking
            byte[] dataPart = Arrays.copyOfRange(dmxPacket.getDmxData(), startPosition, startPosition + length);
            handlerEntry.onDmx(dataPart);
        }
    }

    public Collection<ArtNetNodeInfo> getDiscoveredNodes() {
        return discoveredNodes.values();
    }

    public Collection<DmxHandler> getHandlers() {
        return Collections.unmodifiableList(handlers);
    }
}
