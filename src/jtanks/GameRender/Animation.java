package jtanks.GameRender;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Simple Animation
 * It acts like an overlay
 * Change's image based on game speed
 * Stores images of animation in an array
 */
class Animation implements Runnable {

    private final BufferedImage[] images;//All images that are going to be shown
    private BufferedImage image;//The current image

    private final Point center;//Center at which all images are centered
    private final long delay;
    public final Point point;//Point where drawing is done

    Animation(BufferedImage[] images, Point center, long delay) {
        this.images = images;
        this.center = center;
        this.point = ((Point) center.clone());
        image = images[0];
        this.delay = delay;
    }

    BufferedImage getImage() {
        return image;
    }

    /**
     * This part changes image at constant rate
     */
    @Override
    public void run() {
        for (BufferedImage bufferedImage : images) {
            image = bufferedImage;
            point.setLocation(center.x - image.getWidth() / 2, center.y - image.getHeight() / 2);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                break;
            }
        }
        synchronized (AnimationManager.animations) {//to avoid Concurrent Modification Exception when drawing
            AnimationManager.animations.remove(this);
        }
    }

}
