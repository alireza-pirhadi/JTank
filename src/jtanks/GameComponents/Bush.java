package jtanks.GameComponents;

import jtanks.AllImages;

import java.awt.*;

public class Bush extends ObjectInMap implements Obstacle {
    public  Bush(int x, int y){
        super(new Point(x, y));
        init();

        isBulletBlocking = false;
        isBlocking = false;
    }

    @Override
    protected void init() {
        bodyImage = AllImages.bush;
        setPoints();
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.bush;
    }
}
