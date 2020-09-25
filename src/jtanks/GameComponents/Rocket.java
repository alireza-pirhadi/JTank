package jtanks.GameComponents;

import jtanks.AllImages;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Rocket extends Bullet {

    private RocketLauncher rocketLauncher;//Owner can be a tank , what matter is that it has a target

    Rocket(ObjectInMap owner, Point center, @NotNull Point aimedPoint, double theta) {
        super(owner, center, aimedPoint, theta);

        if(owner instanceof RocketLauncher)
            rocketLauncher = (RocketLauncher) owner;

        speed = 15;
        damage = 3 * Tank.MAX_HEALTH / 5;

    }

    /**
     * Moves the bullet using Bresenham's line algorithm
     * To control speed , use {@code speed} only
     */
    @Override
    void move() {
        aimedPoint = rocketLauncher.target.center;
        aim();
        theta = Math.atan2((aimedPoint.getY() - center.getY()), (aimedPoint.getX() - center.getX()));
        super.move();
    }

    @Override
    protected void init() {
        bodyImage = AllImages.rocket;
        setPoints();
        rotate(theta);
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.rocket;
    }

}
