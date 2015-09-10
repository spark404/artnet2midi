package net.strocamp.artnet;

import net.strocamp.midi.MidiSender;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Arrays;

public class ArtNetNode {

    private static final byte[] ARTNET_ID = { 'A', 'r', 't', '-', 'N', 'e', 't', 0x0};
    byte lastValue = 0x0;

    NetworkInterface networkInterface;
    InterfaceAddress interfaceAddress;


    public ArtNetNode(NetworkInterface networkInterface, InterfaceAddress interfaceAddress) {
        this.networkInterface = networkInterface;
        this.interfaceAddress = interfaceAddress;
    }

    public void handler(MidiSender midiSender) throws Exception {
        DatagramSocket artNetSocket = new DatagramSocket(6454);//, interfaceAddress.getAddress());
        byte[] receiveData = new byte[1024];
        byte[] dmxData = new byte[512];
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            artNetSocket.receive(receivePacket);
            byte[] id = Arrays.copyOfRange(receiveData,0,8);
            if (!Arrays.equals(ARTNET_ID, id)) {
                System.err.println("Not a valid ArtNet packet");
                continue;
            }

            int opCode = (receiveData[9] << 8) + receiveData[8];
            if (opCode != 0x5000) {
                // Not DMX
                continue;
            }

            int universe = receiveData[14] + (receiveData[15] << 8);
            if (universe != 7) {
                // Not my universe
                continue;
            }

            int length = receiveData[16] + (receiveData[17] << 8);
            dmxData = Arrays.copyOfRange(receiveData, 18, 18+length);
            //System.out.println(String.format("Got %d bytes of DMX data", length));
            //System.out.println(String.format("%x %x", dmxData[0], dmxData[1]));

            if (dmxData[0] != lastValue) {
                lastValue = dmxData[0];
                if (dmxData[0] == -1) {
                    midiSender.sendTrigger(1);
                }
            }


        }

    }
}
