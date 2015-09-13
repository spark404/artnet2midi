package net.strocamp.artnet.packets;

import net.strocamp.artnet.ArtNetOpCodes;

public class ArtPoll extends ArtNetPacket {

    public ArtPoll() {
        super(ArtNetOpCodes.OpPoll);
    }

    @Override
    public ArtNetPacket parse(byte[] data) {
        setData(data);
        return this;
    }
}
