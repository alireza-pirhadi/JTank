package jtanks.GameComponents;

import jtanks.AllImages;

import java.awt.*;

public class Teazel extends ObjectInMap {

    public Teazel(int x, int y) {
        super(new Point(x, y));
        init();

        isBulletBlocking = false;

    }

    @Override
    protected void init() {
        bodyImage = AllImages.teazel;
        setPoints();
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.teazel;
    }

}
