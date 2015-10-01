package net.strocamp.artnet.packets;

import net.strocamp.artnet.ArtNetOpCodes;

import java.util.Arrays;

public class ArtDmx extends ArtNetPacket {

    public static final int DMX_LENGTH_OFFSET = 16;
    public static final int DMX_DATA_OFFSET = 18;
    private byte[] dmxData;
    private int dmxLength;

    public ArtDmx() {
        super(ArtNetOpCodes.OpDmx);
    }

    @Override
    public ArtNetPacket parse(byte[] data) {
        setData(data);
        return this;
    }

    public byte[] getDmxData() {
        byte[] data = getData();
        int dmxLength = readIntMsb(data, DMX_LENGTH_OFFSET);
        return Arrays.copyOfRange(data, DMX_DATA_OFFSET, DMX_DATA_OFFSET + dmxLength);
    }

    public int getDmxLength() {
        byte[] data = getData();
        return readIntMsb(data, DMX_LENGTH_OFFSET);
    }
}
