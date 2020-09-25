package jtanks.GameComponents;

import jtanks.AllImages;
import jtanks.GameRender.AnimationManager;
import jtanks.SoundPlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ShieldItem extends ObjectInMap implements Item {
    private boolean caught = false;

    public ShieldItem(@NotNull Point center) {
        super(center);
        init();
        isBulletBlocking = false;
    }

    @Override
    public void caught(ObjectInMap object) {
        if (object instanceof Tank) {
            ((Tank)object).setShield();
            AnimationManager.addShieldAnimation(object.center);
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
        bodyImage = AllImages.shieldItem;
        setPoints();
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.shieldItem;
    }
}
