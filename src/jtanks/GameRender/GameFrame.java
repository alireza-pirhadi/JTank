package jtanks.GameRender;

import jtanks.AllImages;
import jtanks.GameComponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

/**
 * The window on which the rendering is performed.
 * This Game uses the modern BufferStrategy approach for double-buffering,
 * <p>
 * Simply : Shows what's going on in Map . Uses {@code Map.viewPoint} to know where to show
 */
public class GameFrame extends JFrame {

    public static GameFrame frame;

    public static final int GAME_HEIGHT = 720;                  // game resolution
    public static final int GAME_WIDTH = 16 * GAME_HEIGHT / 9;  // aspect ratio

    private BufferStrategy bufferStrategy;
    private final Container mainGameContainer;//Used to manage GUI

    @SuppressWarnings("FieldCanBeLocal")
    private AffineTransform transformBackup;//Used to keep back up from drawing transform

    public GameFrame(String title) {
        super(title);
        setResizable(false);
        setSize(GAME_WIDTH, GAME_HEIGHT);
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

        //Changing Cursor
        try {
            Image image = AllImages.transparent;
            setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                    image,
                    new Point(image.getWidth(null) / 2, image.getHeight(null) / 2), "aim cursor"));
        } catch (IndexOutOfBoundsException | HeadlessException e) {
            System.err.println("Cursor Error " + e.getMessage());
        }

        mainGameContainer = getContentPane();
    }

    /**
     * This must be called once after the JFrame is shown:
     * frame.setVisible(true);
     * and before any rendering is started.
     */
    public void initBufferStrategy() {
        // Tetra-buffering
        createBufferStrategy(4);
        bufferStrategy = getBufferStrategy();
    }

    @Override
    public boolean isDoubleBuffered() {
        return true;
    }

    /**
     * Game rendering with tetra-buffering using BufferStrategy.
     */
    public void render(GameState state) {
        // Render single frame
        do {
            // The following loop ensures that the contents of the drawing buffer
            // are consistent in case the underlying surface was recreated
            do {
                // Get a new graphics context every time through the loop
                // to make sure the strategy is validated
                Graphics2D graphics = (Graphics2D) bufferStrategy.getDrawGraphics();
                try {
                    doRendering(graphics, state);
                } finally {
                    // Dispose the graphics
                    graphics.dispose();
                }
                // Repeat the rendering if the drawing buffer contents were restored
            } while (bufferStrategy.contentsRestored());

            // Display the buffer
            bufferStrategy.show();
            // Tell the system to do the drawing NOW;
            // otherwise it can take a few extra ms and will feel jerky!
            Toolkit.getDefaultToolkit().sync();

            // Repeat the rendering if the drawing buffer was lost
        } while (bufferStrategy.contentsLost());
    }

    //Settings
    private static final boolean highQuality = false;
    static boolean testMode = false;

    /**
     * Rendering all game elements based on the game state.
     * <p>
     * Simply  : Draw Everything in Map if visible in right angel
     *
     * @param g2d   the brush from Frame
     * @param state the game's components' location and details
     */
    private void doRendering(Graphics2D g2d, GameState state) {
        AffineTransform untouchedTransform = g2d.getTransform();

        if (highQuality) {
            //For More Soft Edges (Disable if affected performance)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        }

        if (testMode) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            g2d.scale(1D / Map.zoom, 1D / Map.zoom);
            g2d.translate(-1 * Map.viewPoint.x, -1 * Map.viewPoint.y);
            state.currentMap.getVisibleObjects().forEach(o -> {
                g2d.setColor(Color.RED);
                g2d.fillOval(o.center.x - 5, o.center.y - 5, 10, 10);
                g2d.setColor(Color.GREEN);
                g2d.fillOval(o.polygon.xpoints[0] - 5, o.polygon.ypoints[0] - 5, 10, 10);
                g2d.setColor(Color.BLUE);
                g2d.draw(o.polygon);
                g2d.setColor(Color.BLACK);
            });

            g2d.setColor(Color.MAGENTA);
            if (!state.currentMap.getBullets().isEmpty()) {
                state.currentMap.getBullets().forEach(b -> {
                    g2d.drawLine(b.aimedPoint.x, b.aimedPoint.y, b.owner.center.x, b.owner.center.y);
                    g2d.fillOval(b.aimedPoint.x - 5, b.aimedPoint.y - 5, 10, 10);
                });
            }
        } else { //Not Test Mode

            // Draw background
            g2d.drawImage(state.currentMap.backGround, 0, 0, GAME_WIDTH, GAME_HEIGHT, Map.viewPoint.x, Map.viewPoint.y
                    , ((int) (Map.viewPoint.x + Map.zoom * GAME_WIDTH)), ((int) (Map.viewPoint.y + Map.zoom * GAME_HEIGHT)), null);

            g2d.scale(1D / Map.zoom, 1D / Map.zoom);

            //Draw Objects
            g2d.translate(-1 * Map.viewPoint.x, -1 * Map.viewPoint.y);

            state.currentMap.getVisibleObjects().forEach(o -> {
                transformBackup = g2d.getTransform();

                g2d.translate(o.polygon.xpoints[0], o.polygon.ypoints[0]);
                g2d.rotate(o.theta);
                g2d.drawImage(o.bodyImage, 0, 0, null);

                g2d.setTransform(transformBackup);
            });

            //Draw Tank Cannons
            state.currentMap.getVisibleTanks().forEach(t -> {
                if (t.hasShield()) {
                    int r = t.bodyImage.getWidth() / 2 + 40;
                    g2d.drawImage(AllImages.shieldCircle, t.center.x - r, t.center.y - r, null);
                }
                transformBackup = g2d.getTransform();
                g2d.translate(t.center.x, t.center.y);
                g2d.rotate(t.canonTheta);
                if (t.canonImage == null)
                    t.loadImages();
                g2d.drawImage(t.canonImage, -1 * t.canonImage.getWidth() / 2 + 10,
                        -1 * t.canonImage.getHeight() / 2, null);
                g2d.setTransform(this.transformBackup);
            });

            //Draw RocketLauncher and Plasma Guns
            state.currentMap.objectsInMap.forEach(o -> {
                if ((o instanceof RocketLauncher && o.isBulletBlocking) || o instanceof PlasmaGun) {
                    transformBackup = g2d.getTransform();
                    g2d.translate(o.center.x, o.center.y);
                    if (o instanceof RocketLauncher) {
                        RocketLauncher rocketLauncher = (RocketLauncher) o;
                        g2d.rotate(rocketLauncher.canonTheta);
                        g2d.drawImage(rocketLauncher.launcherImage, -1 * rocketLauncher.launcherImage.getWidth() / 2 + 100,
                                -1 * rocketLauncher.launcherImage.getHeight() / 2, null);
                        g2d.setTransform(this.transformBackup);
                    } else {
                        PlasmaGun plasmaGun = (PlasmaGun) o;
                        g2d.rotate(plasmaGun.canonTheta);
                        g2d.drawImage(plasmaGun.gunImage, -1 * plasmaGun.gunImage.getWidth() / 2,
                                -1 * plasmaGun.gunImage.getHeight() / 2, null);
                        g2d.setTransform(this.transformBackup);
                    }
                }
            });

            //Draw Animations
            synchronized (AnimationManager.animations) {
                AnimationManager.animations.forEach(a -> g2d.drawImage(a.getImage(), a.point.x, a.point.y, null));
            }

        }


        //HUD :
        g2d.setTransform(untouchedTransform);//Necessary for avoid HUD be affected by game visual transforms
        int bulletsNumber = state.playerTank.bullets >= Tank.MAX_BULLET_NUM ? 5 : (int) Math.ceil(state.playerTank.bullets * 5D / Tank.MAX_BULLET_NUM);
        for (int i = 0; i < bulletsNumber; i++) {
            g2d.drawImage(AllImages.bulletsStatus, 10 + (i * 50), 60, null);
        }

        int heartsNumber = state.playerTank.health >= Tank.MAX_HEALTH ? 5 : (int) Math.ceil(state.playerTank.health * 5D / Tank.MAX_HEALTH);
        for (int i = 0; i < heartsNumber; i++) {
            g2d.drawImage(AllImages.healthStatus, (GAME_WIDTH / 2) - 20 + (i * 50), 60, null);
        }

        for (int i = 1; i <= GameState.lives; i++) {
            g2d.drawImage(AllImages.aliveTank, GAME_WIDTH - (i * 50), 60, null);
        }

        //Level Number
        g2d.setColor(Color.CYAN);
        g2d.setFont(new Font("Arial", Font.PLAIN, 25));
        g2d.drawString("Level " + state.getLevelNumber(), GAME_WIDTH - 100, 150);

        // Draw GAME OVER
        if (state.gameOver || state.gameCompleted) {
            g2d.setTransform(untouchedTransform);
            String str = state.gameOver ? "GAME OVER" : "Well Done";
            g2d.setColor(Color.WHITE);
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD).deriveFont(64.0f));
            int strWidth = g2d.getFontMetrics().stringWidth(str);
            g2d.drawString(str, (GAME_WIDTH - strWidth) / 2, GAME_HEIGHT / 2);
            str = "Press Any Key to Exit";
            g2d.setColor(Color.WHITE);
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD).deriveFont(24.0f));
            strWidth = g2d.getFontMetrics().stringWidth(str);
            g2d.drawString(str, (GAME_WIDTH - strWidth) / 2, GAME_HEIGHT / 2 + 40);
        }

        //Draw Cursor
        g2d.setTransform(untouchedTransform);
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouse, GameFrame.getFrames()[0]);
        if (state.playerTank.shotType == Tank.BULLET)
            g2d.drawImage(AllImages.cannonAimCursor, mouse.x - 24, mouse.y - 24, null);
        else
            g2d.drawImage(AllImages.rifleAimCursor, mouse.x - 24, mouse.y - 24, null);

    }

    /**
     * Used to change Displaying panel (in GUI)
     *
     * @param panel the showing panel
     */
    public void setPanel(JPanel panel) {
        setContentPane(panel);
        requestFocus();
    }

    /**
     * Used by GUI to show Game
     */
    public void showGame() {
        setContentPane(mainGameContainer);
        requestFocus();
    }

}
