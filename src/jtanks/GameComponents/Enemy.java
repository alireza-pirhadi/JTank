package jtanks.GameComponents;

import java.util.List;

/**
 * The enemies will try to destroy tank
 * each enemy can have different behaviour
 */
interface Enemy {
    void decide();//what to do in each update
    void setTarget(List<ObjectInMap> targets);//chooses target
    void move();//moving if needed
    void attack();//attacks :/
    boolean isAlive();//whether dead or not
    void die();//what to do after death ( go to hell maybe )
}
