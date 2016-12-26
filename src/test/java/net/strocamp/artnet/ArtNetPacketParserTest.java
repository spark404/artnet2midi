package net.strocamp.artnet;

import junit.framework.TestCase;
import net.strocamp.artnet.packets.ArtDmx;
import net.strocamp.artnet.packets.ArtNetPacket;

import static net.strocamp.artnet.Fixtures.getDmxPacket;

public class ArtNetPacketParserTest extends TestCase {

    public void testGetTypeForOpCode() throws Exception {
        Class clazz = ArtNetPacketParser.getTypeForOpCode(ArtNetOpCodes.OpDmx);
        assertEquals(clazz, ArtDmx.class);
     }

    public void testGetTypeForUnknownOpCode() throws Exception {
        Class clazz = ArtNetPacketParser.getTypeForOpCode(ArtNetOpCodes.OpNzs); // Nzs is not implemented yet
        assertNull(clazz);
    }

    public void testPacketParser() throws Exception {
        byte[] data = getDmxPacket();

        ArtNetPacket packet = ArtNetPacketParser.parse(data);
        assertTrue(packet instanceof ArtDmx);

        assertEquals(530, packet.getLength());
        assertEquals(530, packet.getData().length);
    }

    public void testDmxPacketParser() throws Exception {
        byte[] data = getDmxPacket();

        ArtNetPacket packet = ArtNetPacketParser.parse(data);
        assertTrue(packet instanceof ArtDmx);

        ArtDmx dmxPacket = (ArtDmx) packet;
        assertEquals(512, dmxPacket.getDmxLength());
        assertEquals(512, dmxPacket.getDmxData().length);

        assertEquals(0x01, dmxPacket.getDmxData()[0]);
        assertEquals(0x02, dmxPacket.getDmxData()[1]);
        assertEquals(0x03, dmxPacket.getDmxData()[2]);

    }

}