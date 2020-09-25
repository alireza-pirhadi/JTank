package jtanks.GameComponents;

import jtanks.AllImages;
import jtanks.GameRender.AnimationManager;
import jtanks.SoundPlayer;

import java.awt.*;
import java.util.List;

/**
 * An Enemy that doesn't Shoot
 * Moves Fast
 * Commits Kamikaze !
 */
public class BugEnemy extends ObjectInMap implements Enemy {
    private Tank target;
    private boolean alive;


    public BugEnemy(int x, int y) {
        super(new Point(x, y));
        alive = true;
        theta = 0D;
        isBlocking = false;
        isBulletBlocking = false;
        init();
    }

    @Override
    public void decide() {
        if (isBulletBlocking)
            move();
        else if (target.center.distance(this.center) < 4 * Map.ROOM_WIDTH) {
            AnimationManager.addDustAnimation(center);
            isBlocking = true;
            isBulletBlocking = true;
            init();
            SoundPlayer.play(SoundPlayer.bug);
            move();
        }
        attack();
    }

    @Override
    public void setTarget(List<ObjectInMap> targets) {
        targets.forEach(t -> {
            if (t instanceof Tank && (target == null || !target.isBlocking || !targets.contains(target))) {//If Target is dead or previous target is lost
                target = ((Tank) t);
            }
            if (t instanceof Tank && (t.center.distance(this.center) < target.center.distance(this.center))) {
                target = ((Tank) t);
            }
        });
    }

    @Override
    public void move() {
        if (target.center.x < center.x) {
            super.move(-12, 0);
            if (intersects())
                super.move(12, 0);
        }
        if (target.center.x > center.x) {
            super.move(12, 0);
            if (intersects())
                super.move(-12, 0);
        }
        if (target.center.y > center.y) {
            super.move(0, 12);
            if (intersects())
                super.move(0, -12);
        }
        if (target.center.y < center.y) {
            super.move(0, -12);
            if (intersects())
                super.move(0, 12);
        }
    }

    /**
     * @return whether hit anything else than target or not (if nothing is hit , false is returned)
     */
    private boolean intersects() {
        return Map.map.getHitObjects(this).stream().anyMatch(o -> !o.equals(target));
    }

    @Override
    public void attack() {
        if (Map.map.getHitObjects(this).contains(target)) {
            target.hit(2 * Tank.MAX_HEALTH / 5);
            alive = false;
        }
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    /**
     * Used When Anything with damage has hit this
     *
     * @param amount amount of damage
     */
    @Override
    void hit(int amount) {
        alive = false;
    }


    @Override
    public void die() {
        AnimationManager.addDestroyedAnimation(center);
    }

    @Override
    protected void init() {
        bodyImage = AllImages.enemyBug;
        setPoints();
        loadImages();
    }

    @Override
    public void loadImages() {
        if (isBulletBlocking)
            bodyImage = AllImages.enemyBug;
        else
            bodyImage = AllImages.transparent;
    }

}
