package net.strocamp.artnet.packets;

import net.strocamp.artnet.ArtNetException;

import java.util.Arrays;

public class ArtDmx extends ArtNetPacket {

    public static final int ARTNET_OPCODE_DMX512 = 0x5000;
    private byte[] dmxData;
    private int dmxLength;

    protected ArtDmx(byte[] rawData) throws ArtNetException {
        super(rawData);
        if (getOpCode() != ARTNET_OPCODE_DMX512) {
            throw new ArtNetException("Not a valid DMX ArtNetPackage");
        }
        dmxLength = rawData[16] + (rawData[17] << 8);
        dmxData = Arrays.copyOfRange(rawData, 18, 18 + dmxLength);
    }

    public int supportedOpCode() {
        return ARTNET_OPCODE_DMX512;
    }

    public byte[] getDmxData() {
        return dmxData;
    }

    public int getDmxLength() {
        return dmxLength;
    }
}
