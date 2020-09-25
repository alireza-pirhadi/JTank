package jtanks.GameComponents;


import jtanks.AllImages;
import jtanks.GameRender.AnimationManager;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A Tank can move,shoot,change shooting type
 * A Tank has cannon image,health,bullets count
 * <p>
 * Note : Tank's Cannon is not considered an object in Map
 */
public class Tank extends ObjectInMap {

    public static final int BULLET = 0;
    public static final int RIFLE = 1;
    public static final int MAX_BULLET_NUM = 150;
    public static final int MAX_HEALTH = 250;

    private final Point head;//Used for rotating body of tank

    public double canonTheta;//Rotation Angle for cannon
    public transient BufferedImage canonImage;

    public int bullets;
    public int shotType;
    public int health;
    public int bulletLevel;
    public int rifleLevel;
    boolean alive;

    private double shieldTime;
    private static final double shieldDuration = 10000;//Duration of a shield (in ms)
    int movementSpeed;

    public Tank(int x, int y) {
        super(new Point(x, y));
        head = new Point(0, 0);

        theta = 0D;
        alive = true;
        health = MAX_HEALTH;
        bullets = MAX_BULLET_NUM;
        shotType = BULLET;
        shieldTime = 0;
        movementSpeed = 10;
        bulletLevel = rifleLevel = 0;

        init();
    }

    public Tank(int x, int y, String id) {
        this(x, y);
        setIdentifier(id);
    }

    public void changeShotType() {
        shotType = (++shotType) % 2;
        loadImages();
    }

    public void moveLeft() {
        move(-1 * movementSpeed, 0);
        head.x = -1;
        if (isOutOfBound()) {
            move(movementSpeed, 0);
        }
        rotate();
        if (isOutOfBound())
            reverseRotate();
    }

    public void moveRight() {
        move(movementSpeed, 0);
        head.x = 1;
        if (isOutOfBound()) {
            move(-1 * movementSpeed, 0);
        }
        rotate();
        if (isOutOfBound())
            reverseRotate();
    }

    /**
     * Important : Up : -y
     */
    public void moveUp() {
        move(0, -1 * movementSpeed);
        head.y = -1;
        if (isOutOfBound()) {
            move(0, movementSpeed);
        }
        rotate();
        if (isOutOfBound())
            reverseRotate();
    }

    /**
     * Important : Down : +y
     */
    public void moveDown() {
        move(0, +movementSpeed);
        head.y = 1;
        if (isOutOfBound()) {
            move(0, -1 * movementSpeed);
        }
        rotate();
        if (isOutOfBound())
            reverseRotate();
    }

    public void setCanonThetaRelevantTo(Point aimPoint) {
        canonTheta = Math.atan2((aimPoint.getY() - center.getY()), (aimPoint.getX() - center.getX()));
    }

    /**
     * Called when tank shot
     */
    public void shotBullet() {
        if (bullets != 0) {
            bullets--;
            if (shotType == RIFLE)
                AnimationManager.addGunShotAnimation(new Point((int) ((((double) bodyImage.getWidth()) / 2D) * Math.cos(canonTheta) + center.x),
                        (int) ((((double) bodyImage.getHeight()) / 2D) * Math.sin(canonTheta) + center.y)));
            else
                AnimationManager.addCannonShotAnimation(new Point((int) ((((double) bodyImage.getWidth()) / 2D) * Math.cos(canonTheta) + center.x),
                        (int) ((((double) bodyImage.getHeight()) / 2D) * Math.sin(canonTheta) + center.y)));
        }
    }

    public boolean canShoot() {
        return bullets > 0;
    }

    /**
     * Check if tank has gone some where that shouldn't , like in a wall or out of screen
     * Also if it has hit anything , hit() method will be awaken
     *
     * @return whether tank can be where it is or not
     */
    private boolean isOutOfBound() {

        boolean outOfMap = Map.isOutOfMap(this.polygon);

        boolean intersect = Map.map.isInterested(this.polygon, this);

        //If tank has hit something (item , wall , mine or ... )
        if (intersect) {
            Map.map.getHitObjects(this).forEach(this::hit);
            if (this instanceof Enemy)
                intersect = Map.map.getHitObjects(this).stream().anyMatch(o -> !(o instanceof Item));
        }

        return outOfMap || intersect;
    }

    boolean isVisible() {
        for (ObjectInMap objectInMap : Map.map.objectsInMap) {
            if (objectInMap instanceof Bush && this.intersects(objectInMap.polygon)) {
                if (objectInMap.polygon.contains(polygon.getBounds2D()))//If tank is completely in bush
                    return false;
            }
        }
        return true;
    }

    //A bullet,enemy,... will call this method
    @Override
    void hit(int amount) {
        if (hasShield())
            return;
        health -= amount;
        if (health <= 0) {
            alive = false;
            AnimationManager.addDestroyedAnimation(center);
            isBulletBlocking = false;
            isBlocking = false;
        }
    }

    /**
     * Attacker without damage will call this like item or mine
     *
     * @param object what This has Hit
     */
    @Override
    protected void hit(ObjectInMap object) {
        if (object instanceof Item && !(this instanceof Enemy)) {
            ((Item) object).caught(this);
        }
    }

    @Override
    protected void init() {
        if(identifier.contains("PlayerTank0.")){
            if(GameState.difficultyLevel == 0)
                bulletLevel = rifleLevel = 1;
        }
        loadImages();
        setPoints();
    }

    private double headTheta = 0;

    /**
     * Rotates the tank based on head
     * Some times it makes extra rotations
     * can be optimized
     */
    private void rotate() {
        headTheta = Math.atan2(head.y, head.x);

        if (Math.abs(theta - headTheta) == Math.PI)
            return;


        if (Math.abs(headTheta - theta) > Math.PI / 2) {
            headTheta = Math.atan2(-1 * head.y, -1 * head.x);
            if ((theta < 0 && headTheta > 0) || (theta > 0 && headTheta < 0))
                headTheta *= -1;
        }

        if (headTheta - theta < 0) {
            theta += Math.max(-0.07, headTheta - theta);
        } else {
            theta += Math.min(0.07, headTheta - theta);
        }

        if (Math.abs(theta) > Math.PI) {
            theta = -1 * Math.signum(theta % Math.PI) * Math.PI + theta % Math.PI;
        }

        super.rotate(theta);
    }

    /**
     * Rotates In reverse direction of latest reverse
     */
    private void reverseRotate() {
        if (headTheta - theta < 0) {
            theta -= Math.max(-0.07, headTheta - theta);
        } else {
            theta -= Math.min(0.07, headTheta - theta);
        }
        super.rotate(theta);
    }

    public void resetHead() {
        head.setLocation(0, 0);
    }

    public void heal() {
        if (health < 0)
            health = 0;
        heal(MAX_HEALTH - health);
    }

    public void setShield() {
        shieldTime = System.currentTimeMillis();
    }

    public void switchInfiniteShield() throws NullPointerException {
        if (shieldTime >= 0)
            shieldTime = -1;
        else
            shieldTime = 0;
    }

    public boolean hasShield() {
        return (System.currentTimeMillis() - shieldTime <= shieldDuration) || shieldTime < 0;
    }

    public void heal(int amount) {
        health += amount;
        if (health > Tank.MAX_HEALTH)
            health = Tank.MAX_HEALTH;
    }

    public void loadImages() {
        bodyImage = AllImages.tank;
        if (shotType == BULLET)
            if (bulletLevel == 0)
                canonImage = AllImages.tankCannon;
            else
                canonImage = AllImages.upgradedTankCannon;
        else
            canonImage = AllImages.rifleTankCannon;
    }

    public void switchBoost() {
        if (movementSpeed == 10)
            movementSpeed = 30;
        else
            movementSpeed = 10;
    }

}
