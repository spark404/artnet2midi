package net.strocamp.game;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.FileNotFoundException;

@Component
public class AudioPlayerImpl implements AudioPlayer {

    private File audioFile;

    @Value("${audioplayer.wav.filename:${classpath:roffel.wav}}")
    public void setAudioFile(String audioFileLocation) throws FileNotFoundException {
        audioFile = ResourceUtils.getFile(audioFileLocation);
    }

    @Override
    @Async
    public void play() throws Exception {
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
        while(clip.isRunning()) {
            Thread.sleep(100);
        }
        clip.flush();
        clip.close();
    }
}
