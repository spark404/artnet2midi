package net.strocamp.artnet;

import net.strocamp.artnet.packets.ArtDmx;
import net.strocamp.artnet.packets.ArtNetPacket;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static net.strocamp.artnet.Fixtures.getDmxPacket;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ArtNetPacketParserTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetTypeForOpCode() throws Exception {
        Class clazz = ArtNetPacketParser.getTypeForOpCode(ArtNetOpCodes.OpDmx);
        assertEquals(clazz, ArtDmx.class);
     }

     @Test
    public void testGetTypeForUnknownOpCode() throws Exception {
        Class clazz = ArtNetPacketParser.getTypeForOpCode(ArtNetOpCodes.OpNzs); // Nzs is not implemented yet
        assertNull(clazz);
    }

    @Test
    public void testPacketParser() throws Exception {
        byte[] data = getDmxPacket();

        ArtNetPacket packet = ArtNetPacketParser.parse(data);
        assertTrue(packet instanceof ArtDmx);

        assertEquals(530, packet.getLength());
        assertEquals(530, packet.getData().length);
    }

    @Test
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

    @Test
    public void testUnknownPacketParser() throws Exception {
        thrown.expect(ArtNetException.class);
        thrown.expectMessage("Unknown opCode 6000");

        byte[] data = getDmxPacket();
        data[8] = 0x00;
        data[9] = 0x60;

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