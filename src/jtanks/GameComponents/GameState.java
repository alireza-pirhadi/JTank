package jtanks.GameComponents;

import jtanks.AllImages;
import jtanks.Cheater;
import jtanks.CoOpManager;
import jtanks.GameRender.AnimationManager;
import jtanks.GameRender.GameFrame;
import jtanks.SoundPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

/**
 * This class holds the state of game and all of its elements.
 * This class also handles user inputs, which affect the game state.
 * <p>
 * Simply : Manages Game Logic & User inputs
 */
public class GameState {

    //Overall Game properties
    public boolean gameOver;
    public boolean gameCompleted;
    private String playerID;
    public static int lives;//Lives of player
    private LinkedList<Map> levels;
    private int numberOfLevels;//Equals number of file's in Level Folder
    public static int difficultyLevel;

    //Objects in Game
    public Map currentMap;//Map of game
    public Tank playerTank;//Player Tank

    private boolean keyUP, keyDOWN, keyRIGHT, keyLEFT;
    private boolean keyReleased;
    private boolean mousePressed, mouseRightPressed;
    private boolean mouseMoved;

    private Point mousePoint;
    private final KeyHandler keyHandler;
    private final MouseHandler mouseHandler;


    private double lastShotTime = 0;//time of last shoot by player (in ms)
    private static final double rapidFireDelay = 180;//Used to limit number of rifles shot by player (in ms)
    private static final double cannonFireDelay = 1000;//Used to limit number of cannon shot by player (in ms)

    /**
     * The overall state of game , it's unique for each player in each game
     */
    public GameState() {
        //Initializing
        gameOver = false;
        gameCompleted = false;
        keyUP = false;
        keyDOWN = false;
        keyRIGHT = false;
        keyLEFT = false;
        mousePressed = false;
        mouseRightPressed = false;
        keyReleased = false;
        mouseMoved = false;
        keyHandler = new KeyHandler();
        mouseHandler = new MouseHandler();
        levels = null;//DO NOT INITIALIZE HERE !
        lives = -1;
        difficultyLevel = 1;//Normal level

        //Initializing Cheater
        Cheater.setCheater(this);

    }

    /**
     * Loads the map from Map.map or creates one
     */
    public void loadMap() {

        //Check if Map is loaded by player or not (by file or using server)
        if (Map.map == null) {
            //New Game and Server will come here here
            loadLevels();
            Map.map = levels.poll();
            if (Map.map == null) {
                System.err.println("No Map to play");
                return;
            }
            Map.map.levelNumber = getLevelNumber();
            switch (difficultyLevel) {
                case 0:
                    lives = 4;
                    break;
                case 1:
                    lives = 2;
                    break;
                case 2:
                    lives = 1;
                    break;
            }
        } else {
            numberOfLevels = Objects.requireNonNull(new File("./Levels").listFiles()).length;
        }

        //Finding Player Tank in Map
        if (!CoOpManager.isClient()) {
            Map.map.objectsInMap.forEach(ObjectInMap::init);
            Map.map.objectsInMap.forEach(o -> {
                if (o instanceof Tank && o.identifier.contains("PlayerTank0.")) {
                    playerTank = ((Tank) o);
                    playerID = playerTank.identifier;
                }
            });
            //Server will send map to client so that client can add it's tank to map
            if (CoOpManager.isServer()) {
                CoOpManager.send(Map.map);
                Map.map = CoOpManager.get();
            }
        }

        //For Client in Multi player Mode :
        if (CoOpManager.isClient()) {
            Map.map.objectsInMap.forEach(o -> {
                if (o instanceof Tank && o.identifier.contains("PlayerTank0.")) {
                    Tank serverTank = ((Tank) o);
                    playerTank = new Tank(serverTank.center.x, serverTank.center.y, "PlayerTank" + Math.random());
                    playerTank.init();
                    playerID = playerTank.identifier;
                    Random random = new Random();//For placing the player's tank in a good place
                    while (!Map.map.getHitObjects(playerTank).isEmpty() || Map.isOutOfMap(playerTank.polygon)) {
                        playerTank.move((random.nextInt(3) - 1) * Map.ROOM_WIDTH / 2, (random.nextInt(3) - 1) * Map.ROOM_HEIGHT / 2);
                        if (Map.isOutOfMap(playerTank.polygon))
                            playerTank.move(Map.map.WIDTH / 2 - playerTank.center.x, Map.map.HEIGHT / 2 - playerTank.center.y);
                    }
                }
            });
            Map.map.addObject(playerTank);
            CoOpManager.send(Map.map);
        }

        currentMap = Map.map;
        if (lives == -1)
            lives = Map.map.mapLives;

        if (CoOpManager.isClient() || CoOpManager.isServer()) {
            currentMap.objectsInMap.forEach(ObjectInMap::loadImages);
            CoOpManager.setMyID(playerID);
        }

        AllImages.loadMapBackgrounds();
        currentMap.setBackGround();

    }

    /**
     * The method which updates the game state.
     * It's called on each step of game
     */
    public void update() {

        if (CoOpManager.isClient() || CoOpManager.isServer()) {
            currentMap = Map.map;
            AllImages.loadMapBackgrounds();
            currentMap.setBackGround();
            Map.map.objectsInMap.forEach(o -> {
                o.loadImages();
                if (o.identifier.equals(playerID))
                    playerTank = (Tank) o;
            });
        } else
            currentMap.objectsInMap.removeIf(o -> o.identifier.contains("PlayerTank0.") && !o.equals(playerTank));

        //Changing view point
        if (mouseMoved) {
            if (gameOver || gameCompleted)
                Map.setViewPointRelevantTo(new Point(mousePoint.x, mousePoint.y));
            else
                Map.setViewPointRelevantTo(new Point((mousePoint.x + playerTank.center.x) / 2, (mousePoint.y + playerTank.center.y) / 2));
        }

        //Updating Tank Cannon (Enemies know what to do , they will set their cannon to target)
        if (!CoOpManager.isClient()) {
            if (!CoOpManager.isServer())
                currentMap.getVisibleTanks().forEach(t -> t.setCanonThetaRelevantTo(mousePoint));
            else
                currentMap.getVisibleTanks().forEach(t -> {
                    if (t.equals(playerTank) || !t.identifier.contains("PlayerTank0."))
                        t.setCanonThetaRelevantTo(mousePoint);
                });
        } else
            playerTank.setCanonThetaRelevantTo(mousePoint);

        if (!CoOpManager.isClient()) {
            //Removing Caught Items
            currentMap.updateItems();

            //Removing Bullets that are already hit to something OR out of screen
            currentMap.updateBullets();

            //Removing Dead Enemies and changing targets if needed
            currentMap.updateEnemies();

            //Enemies Will Decide
            currentMap.getEnemies().forEach(Enemy::decide);

            //All bullets that are moving (haven't hit anything)
            ArrayList<Bullet> bullets = currentMap.getBullets();
            //Moving Bullets
            bullets.forEach(Bullet::move);
            //Check if bullets have hit something
            bullets.forEach(bullet -> currentMap.getHitObjects(bullet).forEach(object -> {
                //if what bullet has hit is NOT OWNER  :
                if (!object.equals(bullet.owner)) {
                    //object here can be an enemy , obstacle
                    object.hit(bullet.damage);//Object is hit by bullet
                    bullet.hit();//Bullet have hit something
                }
            }));

        }

        if (!playerTank.alive) {
            if (!gameOver) {
                lives--;
                gameOver = isGameOver();
                if (!gameOver) {
                    playerTank = new Tank(playerTank.center.x, playerTank.center.y, playerTank.identifier);
                    playerTank.init();
                    //For placing the player's tank in a good place (avoid collision , cause player won't be able to get out of it)
                    Random random = new Random();
                    while (!Map.map.getHitObjects(playerTank).isEmpty() || Map.isOutOfMap(playerTank.polygon)) {
                        playerTank.move((random.nextInt(3) - 1) * Map.ROOM_WIDTH / 8, (random.nextInt(3) - 1) * Map.ROOM_HEIGHT / 8);
                    }

                    playerTank.setShield();
                    currentMap.addObject(playerTank);
                } else {
                    currentMap.objectsInMap.remove(playerTank);
                }
            }
        }

        //if Player shot somewhere
        if (mousePressed) {
            if (playerTank.canShoot()) {//Check if player can shoot or not
                Bullet shotBullet = null;
                updateMousePointLocation();
                playerTank.setCanonThetaRelevantTo(mousePoint);
                switch (playerTank.shotType) {
                    case Tank.BULLET:
                        if (System.currentTimeMillis() - lastShotTime >= cannonFireDelay) {
                            lastShotTime = System.currentTimeMillis();
                            playerTank.shotBullet();
                            shotBullet = new Bullet(playerTank, ((Point) playerTank.center.clone()), mousePoint, playerTank.canonTheta);
                            if (playerTank.bulletLevel == 1) {
                                //Speed up current shot bullet
                                shotBullet.speed += 10;
                                //Two more shots :
                                Point2D tmp;
                                AffineTransform transform = new AffineTransform();
                                transform.rotate(Math.PI / 12, playerTank.center.x, playerTank.center.y);
                                tmp = transform.transform(mousePoint, null);
                                Bullet shotBullet1 = new Bullet(playerTank, ((Point) playerTank.center.clone()),
                                        new Point(((int) tmp.getX()), ((int) tmp.getY())), playerTank.canonTheta + Math.PI / 12);
                                shotBullet1.speed += 10;
                                shotBullet1.initMove();
                                currentMap.addObject(shotBullet1);
                                try {
                                    transform.invert();
                                } catch (NoninvertibleTransformException e) {
                                    System.err.println("Error shooting");
                                }
                                tmp = transform.transform(mousePoint, null);
                                Bullet shotBullet2 = new Bullet(playerTank, ((Point) playerTank.center.clone()),
                                        new Point(((int) tmp.getX()), ((int) tmp.getY())), playerTank.canonTheta - Math.PI / 12);
                                shotBullet2.initMove();
                                shotBullet2.speed += 10;
                                currentMap.addObject(shotBullet2);
                            }
                            SoundPlayer.play(SoundPlayer.BULLET);
                        }
                        break;
                    case Tank.RIFLE:
                        if (System.currentTimeMillis() - lastShotTime >= rapidFireDelay) {
                            lastShotTime = System.currentTimeMillis();
                            playerTank.shotBullet();
                            shotBullet = (new RifleBullet(playerTank, ((Point) playerTank.center.clone()), mousePoint, playerTank.canonTheta));
                            if (playerTank.rifleLevel == 1) {
                                //Speed up current shot bullet
                                shotBullet.speed += 10;
                                //Two more shots :
                                Point2D tmp;
                                AffineTransform transform = new AffineTransform();
                                transform.rotate(Math.PI / 12, playerTank.center.x, playerTank.center.y);
                                tmp = transform.transform(mousePoint, null);
                                Bullet shotBullet1 = new RifleBullet(playerTank, ((Point) playerTank.center.clone()),
                                        new Point(((int) tmp.getX()), ((int) tmp.getY())), playerTank.canonTheta + Math.PI / 12);
                                shotBullet1.speed += 10;
                                shotBullet1.initMove();
                                currentMap.addObject(shotBullet1);
                                try {
                                    transform.invert();
                                } catch (NoninvertibleTransformException e) {
                                    System.err.println("Error shooting");
                                }
                                tmp = transform.transform(mousePoint, null);
                                Bullet shotBullet2 = new RifleBullet(playerTank, ((Point) playerTank.center.clone()),
                                        new Point(((int) tmp.getX()), ((int) tmp.getY())), playerTank.canonTheta - Math.PI / 12);
                                shotBullet2.initMove();
                                shotBullet2.speed += 10;
                                currentMap.addObject(shotBullet2);
                            }
                            SoundPlayer.play(SoundPlayer.RIFLE);
                        }
                        break;
                    default:
                        System.err.println("Invalid Shot Type");
                }
                if (shotBullet != null) {
                    shotBullet.initMove();
                    currentMap.addObject(shotBullet);
                }
            }
            else
                SoundPlayer.play(SoundPlayer.emptyGun);
        }

        if (mouseRightPressed) {
            playerTank.changeShotType();
            mouseRightPressed = false;
        }

        //Moving Player Tank
        if (keyReleased) {
            playerTank.resetHead();
            keyReleased = false;
        }
        if (keyUP) {
            playerTank.moveUp();
            updateMousePointLocation();
        }
        if (keyDOWN) {
            playerTank.moveDown();
            updateMousePointLocation();
        }
        if (keyLEFT) {
            playerTank.moveLeft();
            updateMousePointLocation();
        }
        if (keyRIGHT) {
            playerTank.moveRight();
            updateMousePointLocation();
        }

        if (!CoOpManager.isClient()) {
            //Check Level Completeness
            if (!gameOver) {
                if (Map.map.getEnemies().isEmpty() && !gameCompleted) {
                    if (goNextLevel()) //going to next level
                        System.out.println("You Won ! You'll go to the Next Level ");
                    else {
                        System.out.println("Congrats ! You did it");
                        Map.map.isCompleted = gameCompleted = true;
                    }
                }
            }
        } else {
            gameCompleted = Map.map.isCompleted;
            AnimationManager.loadAnimations();
        }

    }

    /**
     * @return the keyboard listener , used for {@code keyPressed}
     */
    public KeyListener getKeyListener() {
        return keyHandler;
    }

    /**
     * @return the mouse listener , used for {@code mousePressed}
     */
    public MouseListener getMouseListener() {
        return mouseHandler;
    }

    /**
     * @return the mouse motion listener , used for {@code mouseMoved}
     */
    public MouseMotionListener getMouseMotionListener() {
        return mouseHandler;
    }

    /**
     * updates the value of {@code mousePoint} based on current location of mouse
     */
    private void updateMousePointLocation() {
        mousePoint = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mousePoint, GameFrame.getFrames()[0]);
        mousePoint.x *= Map.zoom;
        mousePoint.y *= Map.zoom;
        mousePoint.translate(Map.viewPoint.x, Map.viewPoint.y);
    }

    /**
     * updates the value of {@code mousePoint}
     *
     * @param p mouse point is set to this
     */
    private void updateMousePointLocation(Point p) {
        mousePoint = p;
        mousePoint.x *= Map.zoom;
        mousePoint.y *= Map.zoom;
        mousePoint.translate(Map.viewPoint.x, Map.viewPoint.y);
    }

    /**
     * The keyboard handler.
     */
    class KeyHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    keyUP = true;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    keyDOWN = true;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    keyLEFT = true;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    keyRIGHT = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    keyUP = false;
                    keyReleased = true;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    keyDOWN = false;
                    keyReleased = true;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    keyLEFT = false;
                    keyReleased = true;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    keyRIGHT = false;
                    keyReleased = true;
                    break;
            }
        }


    }

    /**
     * The mouse handler.
     */
    class MouseHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            updateMousePointLocation(e.getPoint());
            mouseMoved = true;
            switch (e.getButton()) {
                case MouseEvent.BUTTON1://Left Click
                    mousePressed = true;
                    break;
                case MouseEvent.BUTTON3://Right Click
                    mouseRightPressed = true;
                    break;
            }

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            updateMousePointLocation(e.getPoint());
            mouseMoved = true;
            mousePressed = false;
            mouseRightPressed = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            updateMousePointLocation(e.getPoint());
            mouseMoved = true;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            updateMousePointLocation(e.getPoint());
            mouseMoved = true;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            updateMousePointLocation(e.getPoint());
            mouseMoved = true;
        }

        @Override
        public void mouseExited(MouseEvent e) {
            updateMousePointLocation(e.getPoint());
            mouseMoved = false;//to avoid Updating mouse location
            mousePressed = false;
        }

    }

    /**
     * When window looses focus , none of these should keep happening
     */
    public void resetKeys() {
        keyUP = keyDOWN = keyRIGHT = keyLEFT = mousePressed = mouseMoved = mouseRightPressed = false;
    }

    /**
     * Game is over when player has no lives and it's dead
     *
     * @return whether game is over or not
     */
    private boolean isGameOver() {
        return !playerTank.alive && lives < 0;
    }

    /**
     * Used to go to the next level
     *
     * @return whether game continues or not (another level exists or not)
     */
    private boolean goNextLevel() {
        if (levels == null) {
            //This Will happen when client loses connection with server or when saved progress is loaded
            int tmpLevelNumber = getLevelNumber();
            loadLevels();
            for (int i = 0; i < tmpLevelNumber; i++) {
                levels.poll();//Ignoring Finished Maps
            }
        }
        if (levels.isEmpty()) {
            System.out.println("All Levels are finished");
            return false;
        } else {
            //Before Loading New Map
            Tank coPlayerTank = null;
            if (CoOpManager.isServer()) {
                for (ObjectInMap o : Map.map.objectsInMap) {
                    if (o.identifier.contains("PlayerTank0.") && !o.equals(playerTank)) {
                        coPlayerTank = ((Tank) o);
                    }
                }
            }

            Map.zoom = 1D;
            Map.map = levels.poll();
            Map.map.levelNumber = getLevelNumber();

            //Setting Player tank location based on loaded map
            Tank initialPlayerTank = null;
            for (ObjectInMap o : Map.map.objectsInMap) {
                if (o.identifier.contains("PlayerTank0."))
                    initialPlayerTank = ((Tank) o);
            }
            if (initialPlayerTank == null) {
                System.err.println("Bad Map Data.Game will end");
                return false;
            }
            playerTank.setLocation(initialPlayerTank);
            Map.map.objectsInMap.set(Map.map.objectsInMap.indexOf(initialPlayerTank), playerTank);
            playerTank.loadImages();
            updateMousePointLocation();

            if (CoOpManager.isServer()) {
                //Setting Co Player tank location
                if (coPlayerTank == null) {
                    System.err.println("Bad Map from client.Game Will end");
                    return false;
                }
                coPlayerTank.setLocation(playerTank);
                Map.map.addObject(coPlayerTank);
                Random random = new Random();//For placing the Co player's tank in a good place
                while (!Map.map.getHitObjects(coPlayerTank).isEmpty() || Map.isOutOfMap(coPlayerTank.polygon)) {
                    coPlayerTank.move((random.nextInt(3) - 1) * Map.ROOM_WIDTH / 2, (random.nextInt(3) - 1) * Map.ROOM_HEIGHT / 2);
                    if (Map.isOutOfMap(coPlayerTank.polygon)) {
                        coPlayerTank.move(Map.map.WIDTH / 2 - coPlayerTank.center.x, Map.map.HEIGHT / 2 - coPlayerTank.center.y);
                    }
                }
            }

            AllImages.loadMapBackgrounds();
            Map.map.setBackGround();
            Map.map.objectsInMap.forEach(ObjectInMap::init);
            currentMap = Map.map;
            return true;
        }
    }

    public int getLevelNumber() {
        if (levels != null)
            return numberOfLevels - levels.size();
        else
            return currentMap.levelNumber;
    }

    /**
     * Loads all maps in "Levels" Folder to levels Linked List
     */
    private void loadLevels() {
        levels = new LinkedList<>();
        System.out.println("Loading Map from Level File's");
        File[] levelFiles = new File("./Levels").listFiles();

        if (levelFiles == null) {
            System.err.println("No Map in Level folder ! Game Will Quit ");
            System.exit(0);
        }

        for (File levelFile : levelFiles) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(levelFile))) {
                levels.add((Map) objectInputStream.readObject());
            } catch (IOException | ClassNotFoundException | NullPointerException | ArrayIndexOutOfBoundsException e) {
                System.err.println("Couldn't Load Map ! Game Will Quit " + e.getMessage());
                System.exit(0);
            }
        }
        System.out.println("Number of levels = " + (numberOfLevels = levels.size()));
    }

}

