package jtanks.GameComponents;

import jtanks.AllImages;
import jtanks.SoundPlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class HealthItem extends ObjectInMap implements Item {
    private boolean caught = false;

    public HealthItem(@NotNull Point center) {
        super(center);
        init();
        isBulletBlocking = false;
    }

    @Override
    public void caught(ObjectInMap object) {
        if (object instanceof Tank) {
            ((Tank) object).heal(Tank.MAX_HEALTH / 5);
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
        bodyImage = AllImages.healthItem;
        setPoints();
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.healthItem;
    }
}
