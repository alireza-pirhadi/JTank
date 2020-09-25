package jtanks.GameComponents;

import jtanks.AllImages;

import java.awt.*;

public class Plasma extends Bullet {
    public Plasma(ObjectInMap owner, Point center, Point aimedPoint, double theta) {
        super(owner, center, aimedPoint, theta);
        init();

        speed = 40;
        damage = Tank.MAX_HEALTH / 25;

    }

    @Override
    protected void init() {
        bodyImage = AllImages.plasma;
        setPoints();
        rotate(theta);
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.plasma;
    }

}
