package jtanks.GameComponents;

import jtanks.AllImages;

import java.awt.*;

/**
 * An Obstacle
 * Only Bullets will Move through it
 * It will not be destroyed
 */

public class Plant extends ObjectInMap implements Obstacle {

    public Plant(int x, int y) {
        super(new Point(x, y));
        init();

        isBulletBlocking = false;

    }

    @Override
    protected void init() {
        bodyImage = AllImages.plant;
        setPoints();
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.plant;
    }


}
