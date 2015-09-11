package net.strocamp.midi;

import javax.sound.midi.*;

public class MidiSender {
    Receiver receiver;

    public MidiSender(MidiDevice.Info midiDeviceInfo) throws Exception {
        MidiDevice midiDevice = MidiSystem.getMidiDevice(midiDeviceInfo);
        receiver = midiDevice.getReceiver();
        if (receiver == null) {
            System.err.println("WTF");
        }

        midiDevice.open();
    }

    public void sendTrigger(int trigger) throws Exception {
        System.out.println("Sending trigger to channel " + trigger);
        MidiMessage midiMessage = new ShortMessage(ShortMessage.CONTROL_CHANGE, 0, trigger - 1,0);
        receiver.send(midiMessage, -1);

    }

}
