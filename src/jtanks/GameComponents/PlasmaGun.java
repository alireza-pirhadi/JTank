package jtanks.GameComponents;

import jtanks.AllImages;
import jtanks.GameRender.AnimationManager;
import jtanks.SoundPlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class PlasmaGun extends ObjectInMap implements Enemy {
    private ObjectInMap target;
    public transient BufferedImage gunImage;
    public double canonTheta;

    private int health;

    public PlasmaGun(int x, int y) {
        super(new Point(x, y));

        theta = 0D;
        canonTheta = 0D;
        isBlocking = true;
        isBulletBlocking = true;
        health = 4 * Tank.MAX_HEALTH / 5;

        init();
    }


    @Override
    public void decide() {
        setCanonThetaRelevantTo(target.center);
        attack();
    }

    @Override
    public void setTarget(List<ObjectInMap> targets) {
        targets.forEach(t -> {
            if (target == null || !target.isBlocking || !targets.contains(target)) {//If Target is dead or previous target is lost
                target = t;
            }
            if (t.center.distance(this.center) < target.center.distance(this.center)) {
                target = t;
            }
        });
    }

    @Override
    public void move() {
        //Won't Move
    }

    private int attackRateLimit = -1;
    private static final int attackDelay = 10;

    @Override
    public void attack() {
        if (target instanceof Tank && ((Tank) target).isVisible()) {
            //Check if bullet will hit anything than target
            Line2D.Double line = new Line2D.Double(center, target.center);
            if (Map.map.objectsInMap.stream().anyMatch(o -> !o.equals(target) && !o.equals(this) && !(o instanceof Bullet) && o.isBulletBlocking && line.intersects(o.getBounds())))
                return;
            if ((attackRateLimit = ((++attackRateLimit) % attackDelay)) == 0) {
                Map.map.addObject(new Plasma(this, ((Point) center.clone()), target.center, canonTheta));
                SoundPlayer.play(SoundPlayer.machineGun);
            }
        } else
            attackRateLimit = -1;
    }

    private void setCanonThetaRelevantTo(@NotNull Point aimPoint) {
        canonTheta = Math.atan2((aimPoint.getY() - center.getY()), (aimPoint.getX() - center.getX()));
    }

    /**
     * Used When Anything with damage has hit this
     *
     * @param amount amount of damage
     */
    @Override
    void hit(int amount) {
        health -= amount;
        if (health <= 0) {
            AnimationManager.addDestroyedAnimation(center);
            isBulletBlocking = false;
            isBlocking = false;
        }
    }

    @Override
    public boolean isAlive() {
        return isBlocking;
    }

    @Override
    public void die() {
        AnimationManager.addDestroyedAnimation(center);
        Map.map.spawnItem(center);
    }

    @Override
    protected void init() {
        if(GameState.difficultyLevel == 2)
            health = 6 * Tank.MAX_HEALTH / 5;
        bodyImage = AllImages.plasmaWicket;
        gunImage = AllImages.plasmaGun;
        setPoints();
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.plasmaWicket;
        gunImage = AllImages.plasmaGun;
    }

}
