package jtanks.GameComponents;

import jtanks.AllImages;
import jtanks.SoundPlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class LivesItem extends ObjectInMap implements Item {
    private boolean caught = false;

    public LivesItem(@NotNull Point center){
        super(center);
        init();
        isBulletBlocking = false;
    }
    @Override
    public void caught(ObjectInMap object) {
        if (object instanceof Tank) {
            GameState.lives++;
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
        bodyImage = AllImages.livesItem;
        setPoints();
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.livesItem;
    }
}
