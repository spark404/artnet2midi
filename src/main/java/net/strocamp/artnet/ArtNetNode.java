package net.strocamp.artnet;

import net.strocamp.midi.MidiSender;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArtNetNode {

    private static final byte[] ARTNET_ID = { 'A', 'r', 't', '-', 'N', 'e', 't', 0x0};
    byte lastValue = 0x0;

    private Map<DmxHandlerInfo, DmxHandler> handlers;

    NetworkInterface networkInterface;
    InterfaceAddress interfaceAddress;


    public ArtNetNode(NetworkInterface networkInterface, InterfaceAddress interfaceAddress) {
        handlers = new ConcurrentHashMap<DmxHandlerInfo, DmxHandler>();
        this.networkInterface = networkInterface;
        this.interfaceAddress = interfaceAddress;
    }

    public void handler() throws Exception {
        DatagramSocket artNetSocket = new DatagramSocket(6454, interfaceAddress.getBroadcast());
        byte[] receiveData = new byte[1024];

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            artNetSocket.receive(receivePacket);
            ArtNetPacket artNetPacket = ArtNetPacket.parseRawPacket(receiveData);

            if (!artNetPacket.isValid() && artNetPacket.getOpCode() != 0x5000) {
                continue;
            }

            if (artNetPacket.getUniverse() != 7 && artNetPacket instanceof ArtNetDmxPacket) {
                continue;
            }

            ArtNetDmxPacket dmxPacket = (ArtNetDmxPacket)artNetPacket;

            for (Map.Entry<DmxHandlerInfo, DmxHandler> handlerEntry: handlers.entrySet()) {
                DmxHandlerInfo info = handlerEntry.getKey();
                if (dmxPacket.getUniverse() != info.getUniverse()) {
                    // Not my universe
                    continue;
                }

                int startPosition = info.getAddress();
                int length = info.getWidth();

                // TODO length checking
                byte[] dataPart = Arrays.copyOfRange(dmxPacket.getDmxData(), startPosition, startPosition + length);
                System.out.println("Handler found, sending data for address " + info.getAddress() +
                        " to handler " + info.getName());
                handlerEntry.getValue().handle(dataPart);
            }
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
