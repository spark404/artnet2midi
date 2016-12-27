package net.strocamp.game;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class AudioPlayerTest {
    @Test
    public void testPlay() throws Exception {
        AudioPlayerImpl audioPlayer = new AudioPlayerImpl();
        audioPlayer.setAudioFile("classpath:roffel.wav");

        audioPlayer.play();

    }
}