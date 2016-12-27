package net.strocamp.game;

import org.junit.Ignore;
import org.junit.Test;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

public class AudioPlayerTest {
    @Test
    @Ignore
    public void testPlay() throws Exception {
        AudioPlayerImpl audioPlayer = new AudioPlayerImpl();
        audioPlayer.setAudioFile("classpath:roffel.wav");

        audioPlayer.play();
    }

    @Test
    public void testMixer() throws Exception {
        Mixer.Info[] info = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : info) {
            System.out.println(mixerInfo.getName() + " " + mixerInfo.getVendor() + " " + mixerInfo.getDescription());
        }
    }
}