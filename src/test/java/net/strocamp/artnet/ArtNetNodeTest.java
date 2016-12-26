package net.strocamp.artnet;

import net.strocamp.artnet.packets.ArtDmx;
import net.strocamp.artnet.packets.ArtNetPacket;
import org.junit.Test;

import java.util.Collections;

import static net.strocamp.artnet.Fixtures.getDmxPacket;
import static org.junit.Assert.*;

public class ArtNetNodeTest {
    @Test
    public void testDmxHandler() throws Exception {
        ArtNetNode artNetNode = new ArtNetNode(Collections.singletonList(new DummyHandler("Test", 0, 1,3) {
            @Override
            public void onDmx(byte[] data) {
                assertArrayEquals(new byte[] { 0x01, 0x02, 0x03}, data);
            }
        }));

        byte[] data = getDmxPacket();
        ArtNetPacket packet = ArtNetPacketParser.parse(data);
        assertTrue(packet instanceof ArtDmx);

        artNetNode.handleDmxData((ArtDmx) packet);

    }
}