package net.strocamp.core;

import net.strocamp.midi.MidiSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

@Component
public class MidiSenderFactory {
    private final static Logger logger = LoggerFactory.getLogger(MidiSenderFactory.class);

    public MidiSender getMidiSender(String midiDeviceName) throws MidiUnavailableException {
        MidiSender midiSender = null;
        MidiDevice.Info midiDeviceInfo[] = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : midiDeviceInfo) {
            MidiDevice midiDevice = MidiSystem.getMidiDevice(info);
            int transmitters = midiDevice.getMaxTransmitters();
            int receivers = midiDevice.getMaxReceivers();
            logger.debug("Found MIDI device {} with {} receivers and {} transmitters", info.getName(), receivers, transmitters);
            if (info.getName().equals(midiDeviceName) && midiDevice.getMaxReceivers() == -1) {
                midiSender = new MidiSender(info);
                break;
            }
        }
        if (midiSender == null) {
            throw new MidiUnavailableException("Midi device " + midiDeviceName + " not found");
        }
        return midiSender;
    }
}
