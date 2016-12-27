package net.strocamp.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final static Logger logger = LoggerFactory.getLogger(AudioPlayerImpl.class);

    private File audioFile;

    @Value("${audioplayer.wav.filename:${classpath:roffel.wav}}")
    public void setAudioFile(String audioFileLocation) throws FileNotFoundException {
        audioFile = ResourceUtils.getFile(audioFileLocation);
        if (!audioFile.canRead()) {
            throw new FileNotFoundException("Unable to access file " + audioFile.getAbsolutePath());
        }

        logger.info("AudioPlayer configured to play : {}", audioFile.getAbsolutePath());
    }

    @Override
    @Async
    public void play() throws Exception {
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);

        logger.debug("Playing {} with length {}ms", audioFile.getAbsoluteFile(), clip.getMicrosecondLength()/1000);
        clip.start();

        Thread.sleep(clip.getMicrosecondLength()/1000); // Sleep for the clip length
        while(clip.isRunning()) {
            Thread.sleep(100);
        }

        clip.flush();
        clip.close();
    }
}
