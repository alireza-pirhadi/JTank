package jtanks.GameComponents;

import jtanks.AllImages;

import java.awt.*;

/**
 * A bullet with different picture AND faster Speed AND less damage AND rapid fire ability
 * Rapid fire ability is controlled by GameState
 */

class RifleBullet extends Bullet {

    public RifleBullet(ObjectInMap owner, Point center, Point aimedPoint, double theta) {
        super(owner, center, aimedPoint, theta);
        init();

        speed = 25;
        damage = Tank.MAX_HEALTH / 10;

    }

    @Override
    protected void init() {
        bodyImage = AllImages.rifle;
        setPoints();
        rotate(theta);
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.rifle;
    }

}
