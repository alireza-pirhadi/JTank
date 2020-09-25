package jtanks;

import jtanks.GameComponents.Map;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static jtanks.GameRender.GameFrame.GAME_WIDTH;

/**
 * Keeping All Images to avoid reading from files each time
 * <p>
 * Note0: Images MUST be resized to wanted size based on MAP'S SIZE
 * Note1: Images should be horizontal , for example a bullet picture should point exactly to Right
 * Note2: No need to change size of the image in files , just resize here
 * Note3: Map BackGround is loaded separately
 */
public class AllImages {
    public static BufferedImage transparent;
    public static BufferedImage help;

    public static BufferedImage tank;
    public static BufferedImage enemyTank;
    public static BufferedImage enemyBug;
    public static BufferedImage tankCannon;
    public static BufferedImage upgradedTankCannon;
    public static BufferedImage rifleTankCannon;
    public static BufferedImage enemyTankCannon;
    public static BufferedImage rocketLauncher;
    public static BufferedImage plasmaGun;
    public static BufferedImage plant;
    public static BufferedImage teazel;
    public static BufferedImage bush;
    public static BufferedImage mapBackground;
    public static BufferedImage bullet;
    public static BufferedImage rifle;
    public static BufferedImage plasma;
    public static BufferedImage rocket;
    public static BufferedImage wall;
    public static BufferedImage stoneWall;
    public static BufferedImage healthItem;
    public static BufferedImage ammoItem;
    public static BufferedImage starItem;
    public static BufferedImage livesItem;
    public static BufferedImage brokenWall;
    public static BufferedImage brokenWall2;
    public static BufferedImage mainMenuBackGround;
    public static BufferedImage settingBackGround;
    public static BufferedImage bulletsStatus;
    public static BufferedImage healthStatus;
    public static BufferedImage shieldItem;
    public static BufferedImage shieldCircle;
    public static BufferedImage aliveTank;
    public static BufferedImage wicket;
    public static BufferedImage plasmaWicket;
    public static BufferedImage openWicket;

    public static BufferedImage playerTankIcon;
    public static BufferedImage enemyTankIcon;
    public static BufferedImage rocketLauncherIcon;
    public static BufferedImage plasmaGunIcon;
    public static BufferedImage bushIcon;
    public static BufferedImage teazelIcon;
    public static BufferedImage bugIcon;

    public static BufferedImage cannonAimCursor;
    public static BufferedImage rifleAimCursor;

    public static BufferedImage[] destroyedAnimation;
    public static BufferedImage[] dustAnimation;
    public static BufferedImage[] dustDestroyedAnimation;
    public static BufferedImage[] gunShotAnimation;
    public static BufferedImage[] shieldAnimation;
    public static BufferedImage[] smokeAnimation;

    /**
     * Again I say : Images MUST be resized to preferred size BASED on MAP'S SIZE
     * Try Not to use arbitrary numbers , use portions of Map Size
     */
    public AllImages() {
        try {
            transparent = resizeTo(ImageIO.read(new File("./resources/T.png")), 32, 32);

            mainMenuBackGround = ImageIO.read(new File("./resources/mainM.png"));
            settingBackGround = ImageIO.read(new File("./resources/setting.jpg"));
            help = ImageIO.read(new File("./resources/help.png"));

            //Map Moving Objects
            tank = resizeTo(ImageIO.read(new File("./resources/Tank/tank.png")), Map.ROOM_WIDTH / 2, Map.ROOM_HEIGHT / 2);
            enemyTank = resizeTo(ImageIO.read(new File("./resources/Tank/EnemyTank.png")), Map.ROOM_WIDTH / 2, Map.ROOM_HEIGHT / 2);
            enemyBug = resizeTo(ImageIO.read(new File("./resources/Tank/bug.png")), Map.ROOM_WIDTH / 3, Map.ROOM_HEIGHT / 3);
            tankCannon = resizeTo(ImageIO.read(new File("./resources/Tank/tank-cannon.png")), Map.ROOM_WIDTH / 2, Map.ROOM_HEIGHT / 4);
            wicket = resizeTo(ImageIO.read(new File("./resources/Tank/wicket.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT);
            plasmaWicket = resizeTo(ImageIO.read(new File("./resources/Tank/plasmaWicket.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT);
            openWicket = resizeTo(ImageIO.read(new File("./resources/Tank/openWicket.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT);
            rocketLauncher = resizeTo(ImageIO.read(new File("./resources/Tank/RocketLauncher.png")), 4 * Map.ROOM_WIDTH / 3, 2 * Map.ROOM_HEIGHT / 3);
            plasmaGun = resizeTo(ImageIO.read(new File("./resources/Tank/plasmaHead.png")), Map.ROOM_WIDTH, 2 * Map.ROOM_HEIGHT / 3);
            upgradedTankCannon = resizeTo(ImageIO.read(new File("./resources/Tank/tank-cannon2.png")), Map.ROOM_WIDTH / 2, Map.ROOM_HEIGHT / 4);
            rifleTankCannon = resizeTo(ImageIO.read(new File("./resources/Tank/tankGun02.png")), Map.ROOM_WIDTH / 2, Map.ROOM_HEIGHT / 4);
            enemyTankCannon = resizeTo(ImageIO.read(new File("./resources/Tank/EnemyGun.png")), Map.ROOM_WIDTH / 2, Map.ROOM_HEIGHT / 4);
            bullet = resizeTo(ImageIO.read(new File("./resources/Bullets/bullet1.png")), Map.ROOM_WIDTH / 5, Map.ROOM_HEIGHT / 12);
            rifle = resizeTo(ImageIO.read(new File("./resources/Bullets/bullet3.png")), Map.ROOM_WIDTH / 5, Map.ROOM_HEIGHT / 15);
            plasma = resizeTo(ImageIO.read(new File("./resources/Bullets/plasma.png")), Map.ROOM_WIDTH / 4, Map.ROOM_HEIGHT / 10);
            rocket = resizeTo(ImageIO.read(new File("./resources/Bullets/rocket.png")), 3 * Map.ROOM_WIDTH / 5, Map.ROOM_HEIGHT / 5);

            //Obstacles : < MUST BE Same Size >
            plant = resizeTo(ImageIO.read(new File("./resources/Map/plant.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT);
            teazel = resizeTo(ImageIO.read(new File("./resources/Map/teazel.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT);
            bush = resizeTo(ImageIO.read(new File("./resources/Map/bush.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT);
            wall = resizeTo(ImageIO.read(new File("./resources/Map/brick.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT);
            stoneWall = resizeTo(ImageIO.read(new File("./resources/Map/stoneWall.JPG")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT);
            brokenWall = resizeTo(ImageIO.read(new File("./resources/Map/brokenBrick0.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT);
            brokenWall2 = resizeTo(ImageIO.read(new File("./resources/Map/brokenBrick.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT);

            //Items
            ammoItem = resizeTo(ImageIO.read(new File("./resources/Map/ammoItem.png")), Map.ROOM_WIDTH / 2, Map.ROOM_HEIGHT / 2);
            livesItem = resizeTo(ImageIO.read(new File("./resources/aliveTank.png")), Map.ROOM_WIDTH / 2, Map.ROOM_HEIGHT / 2);
            healthItem = resizeTo(ImageIO.read(new File("./resources/Map/healerItem.png")), Map.ROOM_WIDTH / 2, Map.ROOM_HEIGHT / 2);
            starItem = resizeTo(ImageIO.read(new File("./resources/Map/redStar.png")), Map.ROOM_WIDTH / 2, Map.ROOM_HEIGHT / 2);
            shieldItem = resizeTo(ImageIO.read(new File("./resources/Map/shieldItem.png")), Map.ROOM_WIDTH / 2, Map.ROOM_HEIGHT / 2);
            shieldCircle = resizeTo(ImageIO.read(new File("./resources/Animations/shield.png")), Map.ROOM_WIDTH / 2 + 80, Map.ROOM_HEIGHT / 2 + 80);

            //Map Editor Icons
            playerTankIcon = resizeTo(ImageIO.read(new File("./resources/MapEditorIcons/tank.png")), GAME_WIDTH / 15 - 5, GAME_WIDTH / 15 - 5);
            enemyTankIcon = resizeTo(ImageIO.read(new File("./resources/MapEditorIcons/enemy.png")), GAME_WIDTH / 15 - 5, GAME_WIDTH / 15 - 5);
            rocketLauncherIcon = resizeTo(ImageIO.read(new File("./resources/MapEditorIcons/rocketLauncher.png")), GAME_WIDTH / 15 - 5, GAME_WIDTH / 15 - 5);
            plasmaGunIcon = resizeTo(ImageIO.read(new File("./resources/MapEditorIcons/plasmaGun.png")), GAME_WIDTH / 15 - 5, GAME_WIDTH / 15 - 5);
            bushIcon = resizeTo(bush, GAME_WIDTH / 15 - 5, GAME_WIDTH / 15 - 5);
            teazelIcon = resizeTo(teazel, GAME_WIDTH / 15 - 5, GAME_WIDTH / 15 - 5);
            bugIcon = resizeTo(enemyBug, GAME_WIDTH / 15 - 5, GAME_WIDTH / 15 - 5);

            //HUD:
            bulletsStatus = resizeTo(ImageIO.read(new File("./resources/Bullets/bullet4.png")), 40, 40);
            healthStatus = resizeTo(ImageIO.read(new File("./resources/healthStatus.png")), 40, 40);
            aliveTank = resizeTo(ImageIO.read(new File("./resources/aliveTank.png")), 40, 40);

            //Cursors
            cannonAimCursor = resizeTo(ImageIO.read(new File("./resources/AimCursor.png")), 48, 48);
            rifleAimCursor = resizeTo(ImageIO.read(new File("./resources/RifleAimCursor.png")), 48, 48);

            //Animations
            destroyedAnimation = new BufferedImage[]{
                    resizeTo(ImageIO.read(new File("./resources/Animations/destroyed.png")), Map.ROOM_WIDTH / 8, Map.ROOM_HEIGHT / 8),
                    resizeTo(ImageIO.read(new File("./resources/Animations/destroyed.png")), Map.ROOM_WIDTH / 6, Map.ROOM_HEIGHT / 6),
                    resizeTo(ImageIO.read(new File("./resources/Animations/destroyed.png")), Map.ROOM_WIDTH / 4, Map.ROOM_HEIGHT / 4),
                    resizeTo(ImageIO.read(new File("./resources/Animations/destroyed.png")), Map.ROOM_WIDTH / 2, Map.ROOM_HEIGHT / 2),
                    resizeTo(ImageIO.read(new File("./resources/Animations/destroyed.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT)
            };
            dustAnimation = new BufferedImage[]{
                    resizeTo(ImageIO.read(new File("./resources/Animations/sand2.png")), Map.ROOM_WIDTH * 3 / 5, Map.ROOM_HEIGHT * 3 / 5),
                    resizeTo(ImageIO.read(new File("./resources/Animations/sand2.png")), Map.ROOM_WIDTH * 4 / 5, Map.ROOM_HEIGHT * 4 / 5),
                    resizeTo(ImageIO.read(new File("./resources/Animations/sand2.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT),
            };
            dustDestroyedAnimation = new BufferedImage[]{
                    resizeTo(ImageIO.read(new File("./resources/Animations/sand.png")), Map.ROOM_WIDTH * 3 / 5, Map.ROOM_HEIGHT * 3 / 5),
                    resizeTo(ImageIO.read(new File("./resources/Animations/sand.png")), Map.ROOM_WIDTH * 4 / 5, Map.ROOM_HEIGHT * 4 / 5),
                    resizeTo(ImageIO.read(new File("./resources/Animations/sand.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT),
            };
            gunShotAnimation = new BufferedImage[]{
                    resizeTo(ImageIO.read(new File("./resources/Animations/gunShot.png")), Map.ROOM_WIDTH / 10, Map.ROOM_HEIGHT / 10),
                    resizeTo(ImageIO.read(new File("./resources/Animations/gunShot.png")), Map.ROOM_WIDTH / 6, Map.ROOM_HEIGHT / 6),
            };
            shieldAnimation = new BufferedImage[]{
                    resizeTo(ImageIO.read(new File("./resources/Animations/shield.png")), 2 * Map.ROOM_WIDTH, 2 * Map.ROOM_HEIGHT),
                    resizeTo(ImageIO.read(new File("./resources/Animations/shield2.png")), 3 * Map.ROOM_WIDTH / 2, 3 * Map.ROOM_HEIGHT / 2),
                    resizeTo(ImageIO.read(new File("./resources/Animations/shield3.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT),
                    resizeTo(ImageIO.read(new File("./resources/Animations/shield4.png")), Map.ROOM_WIDTH, Map.ROOM_HEIGHT),
            };
            smokeAnimation = new BufferedImage[]{
                    resizeTo(ImageIO.read(new File("./resources/Animations/smoke.png")), Map.ROOM_WIDTH / 6, Map.ROOM_HEIGHT / 6),
                    resizeTo(ImageIO.read(new File("./resources/Animations/smoke.png")), Map.ROOM_WIDTH / 5, Map.ROOM_HEIGHT / 5),
                    resizeTo(ImageIO.read(new File("./resources/Animations/smoke.png")), Map.ROOM_WIDTH / 4, Map.ROOM_HEIGHT / 4),
                    resizeTo(ImageIO.read(new File("./resources/Animations/smoke.png")), Map.ROOM_WIDTH / 2, Map.ROOM_HEIGHT / 2),
            };

        } catch (IOException e) {
            System.err.println("Error Loading Pictures.");
            System.err.println(e.getMessage() + ' ' + e.getStackTrace()[1]);
            System.exit(0);
        }

    }

    /**
     * Resizing Images to fit to given sizes
     *
     * @param input  input image
     * @param width  resize to this width
     * @param height resize to this height
     * @return the result image
     */
    private static BufferedImage resizeTo(@NotNull BufferedImage input, int width, int height) {
        BufferedImage resize = new BufferedImage(width, height, input.getType());
        resize.getGraphics().drawImage(input, 0, 0, width, height, null);
        return resize;
    }

    /**
     * Used for changing map backgrounds
     */
    public static void loadMapBackgrounds() {
        try {
            if (mapBackground == null || mapBackground.getWidth() != Map.map.WIDTH) {
                if (mapBackground != null)
                    Map.zoom = 1D;
                switch (Map.map.backgroundNum) {
                    case 0:
                        mapBackground = resizeTo(ImageIO.read(new File("./resources/Map/background0.png")), Map.map.WIDTH, Map.map.HEIGHT);
                        break;
                    case 1:
                        mapBackground = resizeTo(ImageIO.read(new File("./resources/Map/background1.png")), Map.map.WIDTH, Map.map.HEIGHT);
                        break;
                    case 2:
                        mapBackground = resizeTo(ImageIO.read(new File("./resources/Map/background2.png")), Map.map.WIDTH, Map.map.HEIGHT);
                        break;
                    default:
                        System.err.println("Unexpected Map Background Number");
                        mapBackground = resizeTo(ImageIO.read(new File("./resources/Map/background0.png")), Map.map.WIDTH, Map.map.HEIGHT);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
