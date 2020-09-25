package jtanks.GameComponents;

import jtanks.AllImages;
import jtanks.CoOpManager;
import jtanks.SoundPlayer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.awt.Frame.getFrames;
import static jtanks.GameRender.GameFrame.GAME_HEIGHT;
import static jtanks.GameRender.GameFrame.GAME_WIDTH;

/**
 * Original Play Ground
 * <p>
 * Bullets are not here
 * This class keeps objects in currentMap
 * This class can check intersection between objects
 */
public class Map implements Serializable {

    public static Map map;//Instance of current playing map , used to make this serializable while having global access to it

    public transient BufferedImage backGround;
    public final ArrayList<ObjectInMap> objectsInMap;//Store all things in map
    public final ConcurrentHashMap<String,ArrayList<Point>> animations = new ConcurrentHashMap<>(7);//For transferring animations in multi player mode
    public int mapLives;//Player Lives (Only for saving)
    public int levelNumber;//Number of playing Level (Only for saving)
    boolean isCompleted = false;

    public static Point viewPoint = new Point(0, 0);//A point always on top left of the screen used to show visible part of currentMap
    public static double zoom = 1.0D;
    public int backgroundNum;

    //To make putting things in currentMap easier , it is divided to ROOMS (like cell in grid)
    public static final int ROOM_HEIGHT = 250;//Height of a single Room
    public static final int ROOM_WIDTH = 250;//Width of a single Room
    public final int WIDTH;//Total Width of the currentMap
    public final int HEIGHT;//Total Height of the currentMap


    //This shouldn't Change at all
    private final Polygon Bounds;


    /**
     * Creating a map
     *
     * @param row_count    Number of Rows in Map
     * @param column_count Number of Columns in Map
     */
    public Map(int row_count, int column_count) {
        WIDTH = column_count * ROOM_WIDTH;
        HEIGHT = row_count * ROOM_HEIGHT;
        Bounds = new Polygon(new int[]{0, WIDTH, WIDTH, 0}, new int[]{0, 0, HEIGHT, HEIGHT}, 4);

        backGround = AllImages.mapBackground;//BackGround of the currentMap
        viewPoint = new Point(0, 0);//Initial ViewPoint
        objectsInMap = new ArrayList<>(10);
        mapLives = 3;
        backgroundNum = 0;
        map = this;
    }

    /**
     * Updates the viewPoint based on given point
     * Essential for camera moving
     *
     * @param point the p1 to make viewpoint relevant to it
     */
    static void setViewPointRelevantTo(Point point) {
        point.translate(-1 * viewPoint.x, -1 * viewPoint.y);
        point.x /= Map.zoom;
        point.y /= Map.zoom;
        point.translate(viewPoint.x, viewPoint.y);
        viewPoint.setLocation(point.x - GAME_WIDTH / 2, point.y - GAME_HEIGHT / 2);
        verifyViewPoint();
    }

    /**
     * Checks that viewpoint doesn't be invalid
     */
    private static void verifyViewPoint() {
        if (viewPoint.x < 0)
            viewPoint.x = 0;
        if (viewPoint.y < 0)
            viewPoint.y = 0;
        if (viewPoint.x > map.WIDTH - GAME_WIDTH * zoom)
            viewPoint.x = map.WIDTH - ((int) (GAME_WIDTH * zoom));
        if (viewPoint.y > map.HEIGHT - GAME_HEIGHT * zoom)
            viewPoint.y = map.HEIGHT - ((int) (GAME_HEIGHT * zoom));
    }

    /**
     * Returns WHAT {@code input} hits
     *
     * @param input the hitter object
     * @return the hit objects
     */
    ArrayList<ObjectInMap> getHitObjects(@NotNull ObjectInMap input) {
        ArrayList<ObjectInMap> result = new ArrayList<>();

        objectsInMap.forEach(o -> {
            if (input instanceof Bullet) {//If hitter is a bullet
                if (o.isBulletBlocking && ObjectInMap.intersects(input.polygon, o.polygon))
                    result.add(o);
            } else if (o.isBlocking && ObjectInMap.intersects(input.polygon, o.polygon))
                result.add(o);
        });

        result.removeIf(o -> o.equals(input));

        return result;
    }

    /**
     * Checks if input rectangle intersects anything in the currentMap
     *
     * @param p         the input polygon
     * @param exclusion what is excluded from checking
     * @return whether  objects intersects with anything or not
     */
    boolean isInterested(Polygon p, ObjectInMap exclusion) {
        for (ObjectInMap objectInMap : objectsInMap)
            if (!objectInMap.equals(exclusion) && objectInMap.isBlocking && objectInMap.intersects(p))
                if (!(objectInMap instanceof Bullet && ((Bullet) objectInMap).owner.equals(exclusion))) {
                    return true;
                }
        return false;
    }

    private final static Random r = new Random();

    /**
     * Spawns an item based on probability
     * @param point the point to spawn item at
     */
    void spawnItem(@NotNull Point point) {
        int anInt = r.nextInt(100) + 1;
        if (anInt <= 10)
            objectsInMap.add(new AmmoItem(point));
        else if (anInt <= 20)
            objectsInMap.add(new HealthItem(point));
        else if (anInt <= 30)
            objectsInMap.add(new ShieldItem(point));
        else if (anInt <= 40)
            objectsInMap.add(new StarItem(point));
        else if (anInt <= 45)
            objectsInMap.add(new LivesItem(point));
    }

    public void addObject(ObjectInMap o) {
        if (!objectsInMap.contains(o))
            objectsInMap.add(o);
        else
            objectsInMap.set(objectsInMap.indexOf(o), o);
    }

    public static void zoomOut() {
        if (zoom < Math.min((Map.map.HEIGHT + 0.0D) / GAME_HEIGHT, (Map.map.WIDTH + 0.0D) / GAME_WIDTH))
            zoom += 0.1;
        if (zoom >= Math.min((Map.map.HEIGHT + 0.0D) / GAME_HEIGHT, (Map.map.WIDTH + 0.0D) / GAME_WIDTH))
            zoom -= 0.1;
    }

    public static void zoomIn() {
        if (zoom > Math.max((2 * Map.ROOM_HEIGHT + 0.0D) / GAME_HEIGHT, (2 * Map.ROOM_WIDTH + 0.0D) / GAME_WIDTH))
            zoom -= 0.1;
        if (zoom <= Math.max((2 * Map.ROOM_HEIGHT + 0.0D) / GAME_HEIGHT, (2 * Map.ROOM_WIDTH + 0.0D) / GAME_WIDTH))
            zoom += 0.1;
    }

    /**
     * Used to draw things on currentMap that are visible
     *
     * @return the objects that should be drawn
     */
    public ArrayList<ObjectInMap> getVisibleObjects() {
        //To ensure Obstacles will be drawn at last
        objectsInMap.sort((o1, o2) -> {
            if (o1 instanceof Bush)
                return 1;
            else if (o2 instanceof Bush)
                return -1;
            else if (o1 instanceof Obstacle)
                return -1;
            else if (o2 instanceof Obstacle)
                return 1;
            else
                return 0;
        });

        Polygon visible = new Polygon(new int[]{viewPoint.x, ((int) (viewPoint.x + zoom * GAME_WIDTH)), ((int) (viewPoint.x + zoom * GAME_WIDTH)), viewPoint.x}
                , new int[]{viewPoint.y, viewPoint.y, ((int) (viewPoint.y + zoom * GAME_HEIGHT)), ((int) (viewPoint.y + zoom * GAME_HEIGHT))}, 4);

        return objectsInMap.stream().filter(o -> o.intersects(visible)).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Like {@code getVisibleObjects()} but only for Tanks
     *
     * @return the Tanks that should be drawn
     */
    public ArrayList<Tank> getVisibleTanks() {
        return getVisibleObjects().stream()
                .filter(Tank.class::isInstance)
                .map(Tank.class::cast)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Bullet> getBullets() {
        return objectsInMap.stream()
                .filter(Bullet.class::isInstance)
                .map(Bullet.class::cast)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    ArrayList<Enemy> getEnemies() {
        return objectsInMap.stream()
                .filter(Enemy.class::isInstance)
                .map(Enemy.class::cast)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void updateBullets() {
        getBullets().forEach(bullet -> {
            if(bullet instanceof Rocket && !bullet.moving){
                SoundPlayer.play(SoundPlayer.rocketExplosion);
            }
        });
        objectsInMap.removeIf(o -> o instanceof Bullet && !(((Bullet) o).moving));
    }

    /**
     * Removing dead enemies and changing target of them
     */
    void updateEnemies() {
        getEnemies().forEach(e -> {
            if (!e.isAlive()) {
                e.die();
                SoundPlayer.play(SoundPlayer.enemyDestroyed);
            }
        });
        objectsInMap.removeIf(o -> o instanceof Enemy && !(((Enemy) o).isAlive()));//Removing Dead Enemies
        getEnemies().forEach(e -> e.setTarget(objectsInMap.stream().filter(p -> p.identifier.contains("PlayerTank0.") &&
                p.isBlocking && ((Tank) p).isVisible()).collect(Collectors.toList())));//Giving Possible target to enemies to decide
    }

    void updateItems() {
        objectsInMap.removeIf(o -> o instanceof Item && ((Item) o).isCaught());
    }

    static boolean isOutOfMap(Polygon objectBound) {
        Area mapArea = new Area(map.Bounds);
        Area objectArea = new Area(objectBound);
        objectArea.subtract(mapArea);
        return !objectArea.isEmpty();
    }

    /**
     * @param map the saved map
     * @return if map was saved successfully or not
     */
    public static boolean saveMap(Map map) {

        //Removing Co-Player if exists
        if (CoOpManager.isClient() || CoOpManager.isServer())
            map.objectsInMap.removeIf(o -> o.identifier.contains("PlayerTank0.") && !o.identifier.equals(CoOpManager.getMyID()));

        JFileChooser fileChooser = new JFileChooser("./", FileSystemView.getFileSystemView());
        fileChooser.setDialogTitle("Choose Place to save");
        fileChooser.setSelectedFile(null);
        if (fileChooser.showSaveDialog(getFrames()[0]) == JFileChooser.APPROVE_OPTION) {
            File mapFile = new File(fileChooser.getSelectedFile().getPath().replaceAll("\\.jtank", "") + ".jtank");
            if (mapFile.exists()) {
                if (JOptionPane.showOptionDialog(getFrames()[0], "File already exists !\nWhat to do ?", "Error",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"Replace", "Cancel"},
                        "Replace") != JOptionPane.YES_OPTION)
                    return false;
            }
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(mapFile, false))) {
                objectOutputStream.writeObject(map);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else
            return false;
    }

    void setBackGround() {
        backGround = AllImages.mapBackground;//BackGround of the currentMap
    }

}
