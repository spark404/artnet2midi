package net.strocamp.artnet;

import net.strocamp.artnet.packets.ArtDmx;
import net.strocamp.artnet.packets.ArtNetPacket;
import net.strocamp.artnet.packets.ArtPollReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ArtNetNode implements ArtNetNodeMBean {
    private final static Logger logger = LoggerFactory.getLogger(ArtNetNode.class);
    public static final int DMX_PORT = 6454;

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

    private boolean autoStart;
    private Environment env;

    @Autowired
    public ArtNetNode(List<DmxHandler> dmxHandlers) {
        handlers = new ArrayList<>();
        handlers.addAll(dmxHandlers);
    }

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Value("${artnetnode.autostart:#{false}}")
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
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

    @PostConstruct
    public void initialize() {
        if (autoStart) {
            logger.debug("Autostart enabled, attempting to start node");

            String artnetInterface = env.getProperty("artnetnode.dmx.interface", (String)null);
            try {
                configureNetworkFromInterfaceName(artnetInterface);
            } catch (ArtNetException e) {
                logger.error("Failed to start ArtNetNode", e);
            }

            this.setUniverse(env.getProperty("artnetnode.dmx.universe", Integer.class, 0));
            this.setNetwork(env.getProperty("artnetnode.dmx.network", Integer.class, 0));
            this.setSubnetwork(env.getProperty("artnetnode.dmx.sunbet", Integer.class, 0));

            try {
                start();
            } catch (ArtNetException e) {
                logger.error("Failed to start ArtNetNode on {}", artnetInterface, e);
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        logger.debug("Shutdown requested");
        stop();
    }

    public void start() throws ArtNetException {
        if (handlerThread != null && handlerThread.isAlive())  {
            throw new ArtNetException("Node already started");
        }
        logger.info("Configuring ArtNetNode with interface:{}, address:{}, network:{}, subnet:{}",
                networkInterface.getDisplayName(), interfaceAddress.getAddress().getHostAddress(), network, subnetwork);
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
        handlerThread.setDaemon(true);
        handlerThread.start();
        logger.info("ArtNetNode on " + interfaceAddress.toString() + " started");
    }

    public void stop() {
        if (handlerThread != null && handlerThread.isAlive()) {
            logger.info("Stopping ArtNetNode on " + interfaceAddress.toString());
            terminate = true;

            try {
                handlerThread.join(5000l);
            } catch (InterruptedException e) {
                logger.error("Thread was interrupted while waiting for it to stop", e);
            }
            handlerThread = null;
            terminate  = false;
            logger.info("ArtNetNode on " + interfaceAddress.toString() + " stopped");
        }
    }

    private void handler() throws Exception {
        DatagramChannel server = null;
        server = DatagramChannel.open();
        InetSocketAddress sAddr = new InetSocketAddress("0.0.0.0", DMX_PORT);
        server.bind(sAddr);

        // According to the spec, start off with ArtPollReply broadcast
        sendArtPollReply(server);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (!terminate) {

            SocketAddress source = server.receive(buffer);
            ArtNetPacket artNetPacket = null;
            try {
                artNetPacket = ArtNetPacketParser.parse(buffer.array());
            } catch (ArtNetException e) {
                logger.error("Ignoring ArtNetException", e);
            }

            if (artNetPacket == null) {
                logger.warn("Received something, but i don't recognize it");
                continue;
            }

            if (artNetPacket.getOpCode() == ArtNetOpCodes.OpPoll) {
                logger.info("Poll received from {}", source.toString());
                sendArtPollReply(server);
            }

            if (artNetPacket.getOpCode() == ArtNetOpCodes.OpPollReply) {
                ArtPollReply artPollReply = (ArtPollReply) artNetPacket;
                handleArtPollReply(artPollReply);
            }

            if (artNetPacket.getOpCode() == ArtNetOpCodes.OpDmx) {
                ArtDmx dmxPacket = (ArtDmx) artNetPacket;
                logger.trace("DMX data received for {}:{}:{}, {} bytes",
                        dmxPacket.getNetwork(), dmxPacket.getSubnet(), dmxPacket.getUniverse(),
                        dmxPacket.getDmxLength());
                if (dmxPacket.getNetwork() == network && dmxPacket.getSubnet() == subnetwork) {
                    handleDmxData(dmxPacket);
                }
            }

            buffer.clear();
        }

        if (server.isOpen()) {
            server.close();
        }
    }

    private void sendArtPollReply(DatagramChannel server) throws ArtNetException, IOException {
        DatagramChannel replyChannel = DatagramChannel.open();
        replyChannel.socket().setBroadcast(true);
        ArtPollReply artPollReply = generateArtPollReply();
        ByteBuffer reply = ByteBuffer.wrap(artPollReply.getData());
        replyChannel.send(reply, new InetSocketAddress(interfaceAddress.getBroadcast(), DMX_PORT));
        replyChannel.close();
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

    private ArtPollReply generateArtPollReply() throws ArtNetException, SocketException {
        ArtPollReply artPollReply = (ArtPollReply) ArtNetPacketParser.generatePacketByOpCode(ArtNetOpCodes.OpPollReply);
        artPollReply
                .setNetSwitch(network, subnetwork)
                .setIpAddress(interfaceAddress.getAddress().getAddress())
                .setMacAddress(networkInterface.getHardwareAddress())
                .setUniverseForInputPort(1, universe);
        return artPollReply;
    }

    void handleDmxData(ArtDmx dmxPacket) {
        for (DmxHandler handlerEntry : handlers) {
            if (handlerEntry.getUniverse() != dmxPacket.getUniverse()) {
                return;
            }

            int startPosition = handlerEntry.getAddress();
            int length = handlerEntry.getWidth();

            // DMX addresses are from 1 to 512, offset by -1 for array indices
            int from = startPosition - 1;
            byte[] dataPart = Arrays.copyOfRange(dmxPacket.getDmxData(), from, from + length);
            handlerEntry.onDmx(dataPart);
        }
    }

    public Collection<ArtNetNodeInfo> getDiscoveredNodes() {
        return discoveredNodes.values();
    }

    public Collection<DmxHandler> getHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    public void configureNetworkFromInterfaceName(String network) throws ArtNetException {
        try {
            NetworkInterface artNetInterface = NetworkInterface.getByName(network);
            InterfaceAddress artNetInterfaceAddress = null;
            for (InterfaceAddress interfaceAddress : artNetInterface.getInterfaceAddresses()) {
                if (interfaceAddress.getAddress() instanceof Inet4Address) {
                    artNetInterfaceAddress = interfaceAddress;
                    break;
                }
            }

            setNetworkInterface(artNetInterface);
            setInterfaceAddress(artNetInterfaceAddress);
        } catch (SocketException e) {
            throw new ArtNetException("Unable to determine the interface");
        }

    }
}
