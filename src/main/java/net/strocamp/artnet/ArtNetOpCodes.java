package net.strocamp.artnet;

public enum ArtNetOpCodes {
    OpPoll(0x2000),
    OpPollReply(0x2100),
    OpDmx(0x5000),
    OpNzs(0x5100);

    private final int opCode;

    ArtNetOpCodes(int opCode) {
        this.opCode = opCode;
    }
}
