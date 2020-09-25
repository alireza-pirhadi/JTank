package jtanks;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer {
    public static String BULLET;
    public static String RIFLE;
    public static String enemyDestroyed;
    public static String rocketExplosion;
    public static String rocketLaunching;
    public static String machineGun;
    public static String emptyGun;
    public static String repair;
    public static String bug;

    public static boolean muted = false;//Whether muted or not

    /**
     * Plays Sounds
     */
    public SoundPlayer() {
        BULLET = "./resources/Sounds/bullet.wav";
        RIFLE = "./resources/Sounds/machineGun.wav";
        enemyDestroyed = "./resources/Sounds/enemydestroyed.wav";
        rocketExplosion = "./resources/Sounds/rocketlauncher_fire.wav";
        rocketLaunching = "./resources/Sounds/bottlerocket.wav";
        machineGun = "./resources/Sounds/machineGun.wav";
        emptyGun = "./resources/Sounds/emptyGun.wav";
        repair = "./resources/Sounds/repair.wav";
        bug = "./resources/Sounds/bug.wav";

        ThreadPool.execute(() -> {
            while (true) {

                if (muted)
                    synchronized (SoundPlayer.class) {
                        try {
                            SoundPlayer.class.wait();
                        } catch (InterruptedException e) {
                            break;
                        }
                    }

                try {
                    File file = new File("./resources/Sounds/Off Limits.wav");
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip();
                    clip.open(inputStream);
                    clip.start();
                    while (clip.getFramePosition() != clip.getFrameLength()) {
                        if (muted)
                            clip.stop();
                        else
                            clip.start();
                    }
                    inputStream.close();
                } catch (LineUnavailableException | IOException e) {
                    System.err.println("Clip Playing Error " + e.getMessage());
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void play(String input) {
        if (!muted)
            ThreadPool.execute(() -> {
                try {
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(input));
                    Clip clip = AudioSystem.getClip();
                    clip.open(inputStream);
                    clip.start();
                    inputStream.close();
                } catch (LineUnavailableException | IOException e) {
                    System.err.println("Clip Playing Error "+e.getMessage());
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                }
            });
    }

}
