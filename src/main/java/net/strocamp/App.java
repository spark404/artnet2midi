package net.strocamp;

import net.strocamp.artnet.ArtNetNode;
import net.strocamp.artnet.DmxHandler;
import net.strocamp.artnet.DmxHandlerInfo;
import net.strocamp.artnet.util.Utils;
import net.strocamp.midi.MidiSender;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

public class App
{
    public static void main( String[] args ) throws Exception
    {
        MidiSender midiSender = null;
        MidiDevice.Info midiDeviceInfo[] = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : midiDeviceInfo) {
            if (info.getName().equals("QLab")) {
                midiSender = new MidiSender(info);
                break;
            }
        }

        if (midiSender == null) {
           throw  new Exception("Failed to find a midisender");
        }

        final MidiSender target = midiSender;

        DmxHandler midiTriggerHandler = new DmxHandler() {
            private byte[] lastValue = { -1, -1};

            @Override
            public void handle(byte[] data) {
                byte channel_1 = data[0];
                if (channel_1 != lastValue[0]) {
                    lastValue[0] = channel_1;
                    if (channel_1 == -1) {
                        try {
                            target.sendTrigger(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                byte channel_2 = data[1];
                if (channel_2 != lastValue[1]) {
                    lastValue[1] = channel_2;
                    if (channel_2 == -1) {
                        try {
                            target.sendTrigger(2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        };

        NetworkInterface artNetInterface = null;
        InterfaceAddress artNetInterfaceAddress = null;
        // Channel 1, universe 8

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface networkInterface : Collections.list(interfaces)) {
            if (networkInterface.getHardwareAddress() == null) {
                continue;
            }
            byte[] macAddress= networkInterface.getHardwareAddress();
            byte[] artnetAddr = Utils.calculateArtNetAddress(macAddress, Utils.OOM_CODE, false);
            System.out.println("Trying address " + printableAddress(artnetAddr) + " on " + networkInterface.getDisplayName());

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                if (Arrays.equals(artnetAddr, interfaceAddress.getAddress().getAddress())) {
                    artNetInterface = networkInterface;
                    artNetInterfaceAddress = interfaceAddress;
                    break;
                }
            }
        }

        if (artNetInterface == null) {
            throw new Exception("No interface found with the correct IP");
        }

        System.out.println("Starting an ArtNet node on " + artNetInterface.getDisplayName());
        final MidiSender destination = midiSender;
        final ArtNetNode artNetNode = new ArtNetNode(artNetInterface, artNetInterfaceAddress);
        Runnable artNetRunner = new Runnable() {
            @Override
            public void run() {

                try {
                    artNetNode.handler();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        Thread artNetThread = new Thread(artNetRunner);
        artNetThread.setDaemon(true);
        artNetThread.start();

        artNetNode.addHandler(new DmxHandlerInfo("midiTrigger", 7, 0, 2), midiTriggerHandler);

        artNetThread.join();
    }

    private static String printableAddress(byte[] hex) {
        StringBuilder sb = new StringBuilder();
        for (byte b: hex) {
            sb.append(String.format("%d", 0xff&b));
            sb.append('.');
        }
        return sb.toString();
    }
}
