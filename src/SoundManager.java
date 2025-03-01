import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;

public class SoundManager {
    private static final Map<String, Clip> soundClips = new HashMap<>();
    private static Clip currentBackgroundMusic;

    public static void initialize() {
        loadSound("intro", "sound/water-bubbles.wav");
        loadSound("water-drop", "sound/water-drop.wav");
        loadSound("winner-bell", "sound/winner-bell.wav");
        loadSound("winner-ceremony", "sound/winner-ceremony.wav");
        loadSound("game-over-horn", "sound/game-over-horn.wav");
        loadSound("game-music", "sound/game-music.wav");
    }

    public static void loadSound(String soundName, String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            soundClips.put(soundName, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logError("Error loading sound: " + soundName, e);
        }
    }

    public static void playSound(String soundName) {
        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        } else {
            logError("Sound not found: " + soundName, null);
        }
    }

    public static void playBackgroundMusic(String musicName) {
        stopBackgroundMusic();
        Clip clip = soundClips.get(musicName);
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            currentBackgroundMusic = clip;
        }
    }

    public static void stopBackgroundMusic() {
        if (currentBackgroundMusic != null && currentBackgroundMusic.isRunning()) {
            currentBackgroundMusic.stop();
        }
    }

    public static void stopAllSounds() {
        for (Clip clip : soundClips.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }

    public static void setVolume(String soundName, float volume) {
        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        } else {
            logError("Sound not found for volume adjustment: " + soundName, null);
        }
    }

    private static void logError(String message, Exception e) {
        if (e != null) {
            System.err.println(message + ": " + e.getMessage());
        } else {
            System.err.println(message);
        }
    }
}
