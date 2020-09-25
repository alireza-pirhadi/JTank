package jtanks.GameComponents;

import jtanks.AllImages;
import jtanks.SoundPlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class StarItem extends ObjectInMap implements Item {
    private boolean caught = false;

    public StarItem(@NotNull Point center){
        super(center);
        init();
        isBulletBlocking = false;
    }
    @Override
    public void caught(ObjectInMap object) {
        if (object instanceof Tank) {
            if(((Tank)object).shotType == 0)
                ((Tank)object).bulletLevel = 1;
            else
                ((Tank)object).rifleLevel = 1;
            object.loadImages();
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
        bodyImage = AllImages.starItem;
        setPoints();
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.starItem;
    }
}
