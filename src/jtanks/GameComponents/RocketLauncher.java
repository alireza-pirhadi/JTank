package jtanks.GameComponents;

import jtanks.AllImages;
import jtanks.GameRender.AnimationManager;
import jtanks.SoundPlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class RocketLauncher extends ObjectInMap implements Enemy {

    public ObjectInMap target;
    public transient BufferedImage launcherImage;
    public double canonTheta;

    private int health;

    public RocketLauncher(int x, int y) {
        super(new Point(x, y));

        theta = 0D;
        canonTheta = 0D;
        isBlocking = true;
        isBulletBlocking = false;
        health = 4 * Tank.MAX_HEALTH / 5;

        init();
    }


    @Override
    public void decide() {
        if (isBulletBlocking) {
            setCanonThetaRelevantTo(target.center);
            attack();
        } else {
            if (target.center.distance(this.center) < 5 * Map.ROOM_WIDTH ) {
                isBulletBlocking = true;
                bodyImage = AllImages.openWicket;
                attack();
            }
        }
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
    private static final int attackDelay = 100;

    @Override
    public void attack() {
        if (target instanceof Tank && ((Tank) target).isVisible()) {
            if ((attackRateLimit = ((++attackRateLimit) % attackDelay)) == 0) {
                setCanonThetaRelevantTo(target.center);
                Rocket rocket = new Rocket(this, ((Point) center.clone()), target.center, canonTheta);
                AnimationManager.addSmokeAnimation(new Point((int) (1.5*(((double) launcherImage.getWidth()) / 2D) * Math.cos(canonTheta) + center.x),
                        (int) (2.5*(((double) launcherImage.getHeight()) / 2D) * Math.sin(canonTheta) + center.y)));
                Map.map.addObject(rocket);
                SoundPlayer.play(SoundPlayer.rocketLaunching);
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
        bodyImage = AllImages.wicket;
        launcherImage = AllImages.rocketLauncher;
        setPoints();
    }

    @Override
    public void loadImages() {
        if (isBulletBlocking)
            bodyImage = AllImages.openWicket;
        else
            bodyImage = AllImages.wicket;
        launcherImage = AllImages.rocketLauncher;
    }

}
