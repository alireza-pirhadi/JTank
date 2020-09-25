package jtanks.GameComponents;

import jtanks.AllImages;
import jtanks.GameRender.AnimationManager;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.List;

public class TankEnemy extends Tank implements Enemy {
    private ObjectInMap target;
    private Bullet hittingBullet;//The bullet that will hit this

    public TankEnemy(int x, int y, ObjectInMap target) {
        super(x, y, "EnemyTank" + Math.random());

        this.target = target;
        health = 3 * Tank.MAX_HEALTH / 5;
        bullets = 1;//Bullet number is not checked

        init();
    }

    @Override
    public void setCanonThetaRelevantTo(Point aimPoint) {
        if (target != null)
            super.setCanonThetaRelevantTo(target.center);
    }

    @Override
    public void decide() {

        hittingBullet = getHittingBullet();

        //Do Nothing if target is far away or no bullet to escape from

        if (center.distance(target.center) > 5 * Map.ROOM_WIDTH && hittingBullet == null) {
            return;
        }


        if (target != null)
            move();

        if (target != null && ((Tank) target).isVisible())
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
        //Check whether dodge a bullet or not
        if (isHittingBullet()) {
            if (hittingBullet.center.x > center.x) {
                moveLeft();
            } else {
                moveRight();
            }
            resetHead();
            if (hittingBullet.center.y > center.y) {
                moveDown();
            } else {
                moveUp();
            }
            resetHead();
            return;
        }

        if (canHitTarget())
            return;

        //No need to dodge a bullet here , so get closer to target if it can't already hit it

        if (target.bodyImage == null) {
            System.err.println("null body image");
            target.loadImages();
        }

        int neglect = 3 * Map.ROOM_WIDTH + target.bodyImage.getWidth() / 2;//Distance kept to target

        if (Math.abs(target.center.x - center.x) > neglect) {
            if (target.center.x < center.x) {
                moveLeft();
                if (isHittingBullet()) {
                    moveRight();
                    return;
                }
            } else {
                moveRight();
                if (isHittingBullet()) {
                    moveLeft();
                    return;
                }
            }
            resetHead();
        } else if (Math.abs(target.center.y - center.y) > neglect) {
            if (target.center.y < center.y) {
                moveUp();
                if (isHittingBullet()) {
                    moveDown();
                    return;
                }
            } else {
                moveDown();
                if (isHittingBullet()) {
                    moveUp();
                    return;
                }
            }
            resetHead();
        } else
            resetHead();
    }

    /**
     * @return the bullet that will hit this and null if non exists
     */
    private Bullet getHittingBullet() {
        if (GameState.difficultyLevel == 0)//If easy
            return null;

        if ((hittingBullet != null && !hittingBullet.moving))
            hittingBullet = null;

        for (Bullet b : Map.map.getBullets()) {
            if (!b.owner.equals(this)) {
                Line2D line = new Line2D.Double(b.polygon.xpoints[1], b.polygon.ypoints[1], b.polygon.xpoints[1] + Math.signum(Math.cos(b.theta)) * 1e3, b.polygon.ypoints[1] + Math.signum(Math.sin(b.theta)) * 1e3 * Math.abs(Math.tan(b.theta)));
                Line2D.Double line2 = new Line2D.Double(b.polygon.xpoints[2], b.polygon.ypoints[2], b.polygon.xpoints[2] + Math.signum(Math.cos(b.theta)) * 1e3, b.polygon.ypoints[2] + Math.signum(Math.sin(b.theta)) * 1e3 * Math.abs(Math.tan(b.theta)));
                if (line.intersects(polygon.getBounds()) || line2.intersects(polygon.getBounds())) {
                    return b;
                }
            }
        }

        return null;
    }

    /**
     * @return whether a bullet wil hit us or not
     */
    private boolean isHittingBullet() {
        return hittingBullet != null;
    }

    private int attackRateLimit = -1;
    private static int attackDelay = 50;

    @Override
    public void attack() {
        if ((attackRateLimit = ((++attackRateLimit) % attackDelay)) == 0) {
            super.setCanonThetaRelevantTo(target.center);
            Line2D.Double line = new Line2D.Double(center, target.center);
            for (Enemy enemy : Map.map.getEnemies()) {
                if (!enemy.equals(this) && line.intersects(((ObjectInMap) enemy).getBounds()))
                    return;
            }
            Bullet bullet = new Bullet(this, ((Point) center.clone()), target.center, canonTheta);
            AnimationManager.addCannonShotAnimation(new Point((int) ((((double) bodyImage.getWidth()) / 2D) * Math.cos(canonTheta) + center.x),
                    (int) ((((double) bodyImage.getHeight()) / 2D) * Math.sin(canonTheta) + center.y)));
            bullet.initMove();
            Map.map.addObject(bullet);
        }
    }

    private boolean canHitTarget() {
        Line2D.Double line = new Line2D.Double(center, target.center);
        return target.center.distance(center) < 5 * Map.ROOM_WIDTH &&
                Map.map.objectsInMap.stream().noneMatch(o -> !(o.equals(target)) && !(o.equals(this)) && !(o instanceof Bullet) && line.intersects(o.getBounds()));
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void die() {
        AnimationManager.addDestroyedAnimation(center);
        Map.map.spawnItem(center);
    }

    @Override
    protected void init() {
        switch (GameState.difficultyLevel) {
            case 0:
                movementSpeed = 5;
                health = Tank.MAX_HEALTH / 5;
                attackDelay = 80;
                break;
            case 1:
                movementSpeed = 10;
                health = 3 * Tank.MAX_HEALTH / 5;
                attackDelay = 50;
                break;
            case 2:
                movementSpeed = 15;
                health = Tank.MAX_HEALTH;
                attackDelay = 35;
                break;
        }
        bodyImage = AllImages.enemyTank;
        canonImage = AllImages.enemyTankCannon;
        setPoints();
    }

    public void loadImages() {
        bodyImage = AllImages.enemyTank;
        canonImage = AllImages.enemyTankCannon;
    }

}
