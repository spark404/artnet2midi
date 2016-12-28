package net.strocamp.artnet;

import net.strocamp.artnet.packets.ArtDmx;
import net.strocamp.artnet.packets.ArtNetPacket;
import net.strocamp.artnet.packets.ArtPoll;
import net.strocamp.artnet.packets.ArtPollReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ArtNetPacketParser {
    private final static Logger logger = LoggerFactory.getLogger(ArtNetPacketParser.class);

    private ArtNetPacketParser() {}

    public static ArtNetPacket generatePacketByOpCode(ArtNetOpCodes opCode) throws ArtNetException {
        ArtNetPacket artNetPacket;
        try {
            artNetPacket = getTypeForOpCode(opCode).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ArtNetException(e);
        }

        return artNetPacket;
    }

    public static ArtNetPacket parse(byte[] data) throws ArtNetException{
        final byte[] packetData = data.clone();

        byte[] id = Arrays.copyOfRange(packetData, 0, 8);
        if (!Arrays.equals(ArtNetPacket.ARTNET_ID, id)) {
            throw new ArtNetException("Invalid magic string, expected " + new String(ArtNetPacket.ARTNET_ID));
        }

        int opCodeValue = (packetData[9] << 8) + packetData[8];
        ArtNetOpCodes opCode = ArtNetOpCodes.fromInt(opCodeValue);
        if (opCode == null) {
            final String errorMsg = String.format("Unknown opCode %x", opCodeValue);
            logger.error(errorMsg);
            throw new ArtNetException(errorMsg);
        }

        ArtNetPacket artNetPacket;
        try {
            artNetPacket = getTypeForOpCode(opCode).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ArtNetException(e);
        }

        artNetPacket.setLength(data.length);
        return artNetPacket.parse(data);
    }

    public static Class<? extends ArtNetPacket> getTypeForOpCode(ArtNetOpCodes opCode) {
        Class clazz;
        switch (opCode) {
            case OpPoll:
                clazz = ArtPoll.class;
                break;
            case OpPollReply:
                clazz = ArtPollReply.class;
                break;
            case OpDmx:
                clazz = ArtDmx.class;
                break;
            default:
                logger.warn("Unknown OpCode");
                clazz = null;
        }
        return clazz;
    }
}
