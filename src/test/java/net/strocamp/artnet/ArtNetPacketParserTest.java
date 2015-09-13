package net.strocamp.artnet;

import junit.framework.TestCase;
import net.strocamp.artnet.packets.ArtDmx;

public class ArtNetPacketParserTest extends TestCase {

    public void testGetTypeForOpCode() throws Exception {
        Class clazz = ArtNetPacketParser.getTypeForOpCode(ArtNetOpCodes.OpDmx);
        assertEquals(clazz, ArtDmx.class);
     }

    public void testGetTypeForUnknownOpCode() throws Exception {
        Class clazz = ArtNetPacketParser.getTypeForOpCode(ArtNetOpCodes.OpNzs); // Nzs is not implemented yet
        assertNull(clazz);
    }
}