package net.strocamp.artnet.packets;

import net.strocamp.artnet.ArtNetException;
import net.strocamp.artnet.ArtNetOpCodes;

import java.util.Arrays;

public class ArtDmx extends ArtNetPacket {

    private byte[] dmxData;
    private int dmxLength;

    protected ArtDmx(byte[] rawData) throws ArtNetException {
        super(rawData);
        this.opCode = ArtNetOpCodes.OpDmx;
        dmxLength = rawData[16] + (rawData[17] << 8);
        dmxData = Arrays.copyOfRange(rawData, 18, 18 + dmxLength);
    }

    public byte[] getDmxData() {
        return dmxData;
    }

    public int getDmxLength() {
        return dmxLength;
    }
}
