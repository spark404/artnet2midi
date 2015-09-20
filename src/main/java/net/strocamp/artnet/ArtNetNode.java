package net.strocamp.artnet;

import net.strocamp.artnet.packets.ArtDmx;
import net.strocamp.artnet.packets.ArtNetPacket;
import net.strocamp.artnet.packets.ArtPollReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArtNetNode {
    private final static Logger logger = LoggerFactory.getLogger(ArtNetNode.class);

    private Map<DmxHandlerInfo, DmxHandler> handlers;

    NetworkInterface networkInterface;
    InterfaceAddress interfaceAddress;


    public ArtNetNode(NetworkInterface networkInterface, InterfaceAddress interfaceAddress) {
        handlers = new ConcurrentHashMap<>();
        this.networkInterface = networkInterface;
        this.interfaceAddress = interfaceAddress;
    }

    public void handler() throws Exception {
        DatagramSocket artNetSocket = new DatagramSocket(6454);//, interfaceAddress.getAddress());
        artNetSocket.setBroadcast(true);
        byte[] receiveData = new byte[1024];

        // According to the spec, start off with ArtPollReply broadcast
        DatagramPacket datagramPacket = generateArtPollReply(artNetSocket);
        artNetSocket.send(datagramPacket);

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            artNetSocket.receive(receivePacket);
            ArtNetPacket artNetPacket = ArtNetPacketParser.parse(receiveData);

            if (artNetPacket == null) {
                continue;
            }

            if (artNetPacket.getOpCode() == ArtNetOpCodes.OpPoll) {
                logger.info("Poll received from {}", receivePacket.getAddress().toString());
                datagramPacket = generateArtPollReply(artNetSocket);
                artNetSocket.send(datagramPacket);
            }

            if (artNetPacket.getOpCode() == ArtNetOpCodes.OpPollReply) {
                logger.info("Poll reply seen from {}", ((ArtPollReply)artNetPacket).getShortName());
            }

            if (artNetPacket.getOpCode() == ArtNetOpCodes.OpDmx) {
                ArtDmx dmxPacket = (ArtDmx) artNetPacket;
                handleDmxData(dmxPacket);
            }
        }
    }

    private DatagramPacket generateArtPollReply(DatagramSocket artNetSocket) throws ArtNetException, SocketException {
        ArtPollReply artPollReply = (ArtPollReply) ArtNetPacketParser.generatePacketByOpCode(ArtNetOpCodes.OpPollReply);
        artPollReply
                .setNetSwitch(10, 5)
                .setIpAddress(interfaceAddress.getAddress().getAddress())
                .setMacAddress(networkInterface.getHardwareAddress())
                .setUniverseForInputPort(1, 7);
        return new DatagramPacket(artPollReply.getData(), artPollReply.getLength(), interfaceAddress.getBroadcast(), 0x1936);
    }

    private void handleDmxData(ArtDmx dmxPacket) {
        for (Map.Entry<DmxHandlerInfo, DmxHandler> handlerEntry : handlers.entrySet()) {
            DmxHandlerInfo info = handlerEntry.getKey();
            //if (dmxPacket.getUniverse() != info.getUniverse()) {
            //    // Not my universe
            //    continue;
            //}

            int startPosition = info.getAddress();
            int length = info.getWidth();

            // TODO length checking
            byte[] dataPart = Arrays.copyOfRange(dmxPacket.getDmxData(), startPosition, startPosition + length);
            handlerEntry.getValue().handle(dataPart);
        }
    }

    public void addHandler(DmxHandlerInfo dmxHandlerInfo, DmxHandler dmxHandler) {
        handlers.put(dmxHandlerInfo, dmxHandler);
    }

    public void removeHandler(DmxHandlerInfo dmxHandlerInfo) {
        if (handlers.containsKey(dmxHandlerInfo)) {
            handlers.remove(dmxHandlerInfo);
        }
    }
}
