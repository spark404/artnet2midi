package net.strocamp.artnet.packets;

import net.strocamp.artnet.ArtNetException;

import java.util.Arrays;

public abstract class ArtNetPacket {
    private static final byte[] ARTNET_ID = { 'A', 'r', 't', '-', 'N', 'e', 't', 0x0};
    private final byte[] rawData;

    private int opCode;
    private int artNetVersion;
    private int universe;

    public static ArtNetPacket parseRawPacket(byte[] rawData) throws ArtNetException {
        byte[] id = Arrays.copyOfRange(rawData, 0, 8);
        if (!Arrays.equals(ARTNET_ID, id)) {
            throw new ArtNetException("Invalid magic string");
        }
        int opCode = (rawData[9] << 8) + rawData[8];
        if (opCode == ArtDmx.ARTNET_OPCODE_DMX512) {
            return new ArtDmx(rawData.clone());
        } else {
            System.out.println("Unsupported OpCode " + opCode);
            return null;
        }
    }

    protected ArtNetPacket(byte[] rawData) {
        this.rawData = rawData;
        artNetVersion = (rawData[10] << 8) + rawData[11];
        opCode = (rawData[9] << 8) + rawData[8];
        universe = rawData[14] + (rawData[15] << 8);
    }

    public boolean isValid() {
        byte[] id = Arrays.copyOfRange(rawData, 0, 8);
        if (!Arrays.equals(ARTNET_ID, id)) {
            return false;
        }
        return true;
    }

    public int getOpCode() {
        return opCode;
    }

    public int getUniverse() {
        return universe;
    }

    public int getArtNetVersion() {
        return artNetVersion;
    }
}
