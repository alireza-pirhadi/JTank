package jtanks.GameComponents;

/**
 * Reward for player which can be gained after destroying something
 * Items are :
 * 1.health
 * 2.life
 * 3.ammo
 * 4.shield
 * 5.upgrade
 */
interface Item {
    /**
     * What to do when some one caught this item
     * @param object the catcher
     */
    void caught(ObjectInMap object);

    /**
     * To know has anyone caught this or not
     * @return whether caught or not
     */
    boolean isCaught();
}
