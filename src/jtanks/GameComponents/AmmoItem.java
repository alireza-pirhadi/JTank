package jtanks.GameComponents;

import jtanks.AllImages;
import jtanks.SoundPlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Bonus Item
 * Adds bullets when caught
 */
public class AmmoItem extends ObjectInMap implements Item {

    private boolean caught = false;

    public AmmoItem(@NotNull Point center) {
        super(center);
        init();

        isBulletBlocking = false;

    }

    /**
     * @param object the catcher
     */
    @Override
    public void caught(ObjectInMap object) {
        if (object instanceof Tank) {
            ((Tank) object).bullets += Tank.MAX_BULLET_NUM / 5;
            caught = true;
            SoundPlayer.play(SoundPlayer.repair);
        }
    }



    @Override
    public boolean isCaught() {
        return caught;
    }

    @Override
    protected void init() {
        bodyImage = AllImages.ammoItem;
        setPoints();
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.ammoItem;
    }

}
