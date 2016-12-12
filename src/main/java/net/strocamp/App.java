package net.strocamp;

import net.strocamp.artnet.ArtNetException;
import net.strocamp.artnet.ArtNetNode;
import net.strocamp.artnet.DmxHandlerInfo;
import net.strocamp.artnet.util.Utils;
import net.strocamp.core.DmxToMidiHandler;
import net.strocamp.core.JettyManager;
import net.strocamp.core.MidiSenderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

public class App
{
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) throws Exception
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("config.xml");

        // Embedded jetty
        new Thread() {
            @Override
            public void run() {
                JettyManager jettyManager = applicationContext.getBean(JettyManager.class);
                jettyManager.startServer(8089);
            }
        }.start();

        DmxToMidiHandler midiTriggerHandler = applicationContext.getBean(DmxToMidiHandler.class);
        MidiSenderFactory midiSenderFactory = applicationContext.getBean(MidiSenderFactory.class);

        midiTriggerHandler.setMidiSender(midiSenderFactory.getMidiSender("ArtNet2Midi"));

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
            logger.debug("Trying address " + printableAddress(artnetAddr) + " on " + networkInterface.getDisplayName());

            if (networkInterface.getInterfaceAddresses() != null) {
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    if (Arrays.equals(artnetAddr, interfaceAddress.getAddress().getAddress())) {
                        artNetInterface = networkInterface;
                        artNetInterfaceAddress = interfaceAddress;
                        break;
                    }
                }
            }
        }

        if (artNetInterface == null) {
            // DEBUG
            artNetInterface = NetworkInterface.getByName("en0");
            if (artNetInterface == null) {
                artNetInterface = NetworkInterface.getByName("wlan0");
            }
            if (artNetInterface == null) {
                artNetInterface = NetworkInterface.getByName("lo0");
            }
            if (artNetInterface.getInterfaceAddresses() != null) {
                for (InterfaceAddress interfaceAddress : artNetInterface.getInterfaceAddresses()) {
                    if (interfaceAddress.getAddress() instanceof Inet4Address) {
                        artNetInterfaceAddress = interfaceAddress;
                        break;
                    }
                }
            }
        }

        logger.debug("Starting an ArtNet node on " + artNetInterface.getDisplayName());
        final ArtNetNode artNetNode = ArtNetNode.getInstance();

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("net.strocamp.artnet2midi:type=ArtNetNode");
        mbs.registerMBean(artNetNode, name);

        artNetNode.addHandler(new DmxHandlerInfo("midiTrigger", 7, 0, 2), midiTriggerHandler);
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
