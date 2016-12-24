package net.strocamp.core;

import net.strocamp.artnet.DmxHandler;
import net.strocamp.midi.MidiSender;

public class DmxToMidiHandler extends DmxHandler {
    private MidiSender midiSender;
    private byte[] lastValue = { -1, -1};

    public DmxToMidiHandler(int universe, int address, int channels) {
        super("DmxToMidiHandler", universe, address, channels);
    }

    public void setMidiSender(MidiSender midiSender) {
        this.midiSender = midiSender;
    }

    @Override
    public void onDmx(byte[] data) {
        byte channel_1 = data[0];
        if (channel_1 != lastValue[0]) {
            lastValue[0] = channel_1;
            if (channel_1 == -1) {
                try {
                    midiSender.sendTrigger(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        byte channel_2 = data[1];
        if (channel_2 != lastValue[1]) {
            lastValue[1] = channel_2;
            if (channel_2 == -1) {
                try {
                    midiSender.sendTrigger(2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
