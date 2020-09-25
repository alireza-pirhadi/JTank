package jtanks.GameRender;

import jtanks.AllImages;
import jtanks.CoOpManager;
import jtanks.GameComponents.Map;
import jtanks.ThreadPool;

import java.awt.*;
import java.util.ArrayList;

/**
 * Manages animations of game
 * can add animation and transfer animations in co-op mode
 */
public class AnimationManager {

    static final ArrayList<Animation> animations = new ArrayList<>(5);

    public static void addDestroyedAnimation(Point point) {
        addAnimation(new Animation(AllImages.destroyedAnimation, point, 3 * GameLoop.MS_PER_UPDATE));
        if (CoOpManager.isServer())
            Map.map.animations.computeIfAbsent("Destroyed", k -> new ArrayList<>()).add(point);
    }

    public static void addDustDestroyedAnimation(Point point) {
        addAnimation(new Animation(AllImages.dustDestroyedAnimation, point, 3 * GameLoop.MS_PER_UPDATE));
        if (CoOpManager.isServer())
            Map.map.animations.computeIfAbsent("DustDestroyed", k -> new ArrayList<>()).add(point);
    }

    public static void addDustAnimation(Point point) {
        addAnimation(new Animation(AllImages.dustAnimation, point, 3 * GameLoop.MS_PER_UPDATE));
        if (CoOpManager.isServer())
            Map.map.animations.computeIfAbsent("Dust", k -> new ArrayList<>()).add(point);
    }

    public static void addGunShotAnimation(Point point) {
        addAnimation(new Animation(AllImages.gunShotAnimation, point, GameLoop.MS_PER_UPDATE / 2));
        if (CoOpManager.isServer())
            Map.map.animations.computeIfAbsent("GunShot", k -> new ArrayList<>()).add(point);
    }

    public static void addCannonShotAnimation(Point point) {
        addAnimation(new Animation(AllImages.gunShotAnimation, point, 3 * GameLoop.MS_PER_UPDATE));
        if (CoOpManager.isServer())
            Map.map.animations.computeIfAbsent("CannonShot", k -> new ArrayList<>()).add(point);
    }

    public static void addShieldAnimation(Point point) {
        addAnimation(new Animation(AllImages.shieldAnimation, point, 4 * GameLoop.MS_PER_UPDATE));
        if (CoOpManager.isServer())
            Map.map.animations.computeIfAbsent("Shield", k -> new ArrayList<>()).add(point);
    }

    public static void addSmokeAnimation(Point point) {
        addAnimation(new Animation(AllImages.smokeAnimation, point, 2 * GameLoop.MS_PER_UPDATE));
        if (CoOpManager.isServer())
            Map.map.animations.computeIfAbsent("Smoke", k -> new ArrayList<>()).add(point);
    }

    private static void addAnimation(Animation animation) {
        synchronized (animations) {
            animations.add(animation);
        }
        ThreadPool.execute(animation);
    }

    public static void loadAnimations() {
        for (String key : Map.map.animations.keySet()) {
            switch (key) {
                case "Destroyed":
                    Map.map.animations.get(key).forEach(AnimationManager::addDestroyedAnimation);
                    break;
                case "DustDestroyed":
                    Map.map.animations.get(key).forEach(AnimationManager::addDustDestroyedAnimation);
                    break;
                case "Dust":
                    Map.map.animations.get(key).forEach(AnimationManager::addDustAnimation);
                    break;
                case "GunShot":
                    Map.map.animations.get(key).forEach(AnimationManager::addGunShotAnimation);
                    break;
                case "CannonShot":
                    Map.map.animations.get(key).forEach(AnimationManager::addCannonShotAnimation);
                    break;
                case "Smoke":
                    Map.map.animations.get(key).forEach(AnimationManager::addSmokeAnimation);
                    break;
                case "Shield":
                    Map.map.animations.get(key).forEach(AnimationManager::addShieldAnimation);
                    break;
            }
            Map.map.animations.get(key).clear();
        }
    }

}
