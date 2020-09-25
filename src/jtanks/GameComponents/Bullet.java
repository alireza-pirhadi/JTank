package jtanks.GameComponents;

import jtanks.AllImages;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * A bullet kept at GameState
 */
public class Bullet extends ObjectInMap {

    public Point aimedPoint;

    //Logic Stuff
    boolean moving;
    int damage;
    public final ObjectInMap owner;//Who has shot this bullet
    int speed;//Speed of moving bullet

    Bullet(ObjectInMap owner, Point center, @NotNull Point aimedPoint, double theta) {
        super(center);
        this.theta = theta;
        this.aimedPoint = aimedPoint;
        this.owner = owner;
        init();

        moving = true;//When Bullet is newed , its shot , so moving

        speed = 15;
        damage = Tank.MAX_HEALTH / 5;

        aim();

    }

    void aim(){
        dx = aimedPoint.x - center.x;
        dy = aimedPoint.y - center.y;

        if (dy < 0) {
            yi = -1;
            dy *= -1;
        } else {
            yi = 1;
        }
        if (dx < 0) {
            xi = -1;
            dx *= -1;
        } else {
            xi = 1;
        }
        m = (0.0 + dy) / dx;
        if (m >= 1) {
            int tmp = dx;
            dx = dy;
            dy = tmp;
        }
        error = 2 * dy - dx;
    }

    private int dx;
    private int dy;
    private int yi;
    private int xi;
    private int error;
    private double m;

    /**
     * Moves the bullet using Bresenham's line algorithm
     * To control speed , use {@code speed} only
     */
    void move() {
        //Check If out of Map
        if (Map.isOutOfMap(this.polygon)) {
            moving = false;
            return;
        }

        //Moving
        for (int i = 0; i < speed; i++) {
            if (m <= 1)
                super.move(xi, 0);
            else {
                super.move(0, yi);
            }
            if (error > 0) {
                if (m <= 1)
                    super.move(0, yi);
                else
                    super.move(xi, 0);
                error -= 2 * dx;
            }
            error += 2 * dy;
        }

    }

    void initMove() {
        for (int i = 0; i < 5; i++) {
            move();
        }
    }

    /**
     * When Bullet Hit anything
     */
    @Override
    void hit() {
        moving = false;
    }

    /**
     * When bullet hit target
     *
     * @param object what This has Hit
     */
    @Override
    protected void hit(ObjectInMap object) {
        if (object instanceof Bullet && !((Bullet) object).owner.equals(this.owner)) {//If another bullet hit this
            moving = false;//this will stop
            object.hit();//The Other bullet will also be stopped
        }
    }

    @Override
    protected void init() {
        bodyImage = AllImages.bullet;
        setPoints();
        rotate(theta);
    }

    @Override
    public void loadImages() {
        bodyImage = AllImages.bullet;
    }
}
