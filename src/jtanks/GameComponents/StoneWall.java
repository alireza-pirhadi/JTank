package jtanks.GameComponents;

import jtanks.AllImages;

import java.awt.*;

public class StoneWall extends ObjectInMap implements Obstacle {

    public StoneWall(int x, int y) {
        super(new Point(x, y));
        init();
    }

    @Override
    protected void init() {
        bodyImage = AllImages.stoneWall;
        setPoints();
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.stoneWall;
    }
}
