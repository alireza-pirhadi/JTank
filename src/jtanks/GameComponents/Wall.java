package jtanks.GameComponents;

import jtanks.AllImages;
import jtanks.GameRender.AnimationManager;

import java.awt.*;

/**
 * An Obstacle
 * It will be destroyed
 * Things will NOT move through it unless destroyed
 */
public class Wall extends ObjectInMap implements Obstacle {

    private int health = 100;//Can be immutable (no health check in hit() method)

    public Wall(int x, int y) {
        super(new Point(x, y));
        init();
    }


    @Override
    protected void hit(int amount) {
        //If it is already destroyed
        if (health <= 0)
            return;

        //Health Decrement
        health -= amount;

        if(health <= 50)
            loadImages();

        if (health <= 0) {
            //Wall is destroyed
            AnimationManager.addDustDestroyedAnimation(center);
            isBlocking = false;
            isBulletBlocking = false;
            loadImages();
            Map.map.spawnItem(center);
        }

    }

    @Override
    protected void init() {
        loadImages();
        setPoints();
    }

    @Override
    public void loadImages() {
        if (health > 0)
            if (health > 50)
                bodyImage = AllImages.wall;
            else
                bodyImage = AllImages.brokenWall;
        else
            bodyImage = AllImages.brokenWall2;
    }


}
