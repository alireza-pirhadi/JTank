package jtanks.GameRender;

import jtanks.Cheater;
import jtanks.CoOpManager;
import jtanks.GUI.MainMenu;
import jtanks.GameComponents.GameState;
import jtanks.GameComponents.Map;

import javax.swing.*;
import java.awt.event.*;

/**
 * Game Loop .
 * <p>
 * Ghaffarian :
 * Note that to make this work, none of the 2 methods
 * in the while loop (update() and render()) should be
 * long running! Both must execute very quickly, without
 * any waiting and blocking!
 * <p>
 * Simply : This keeps the game running until something stops it
 */
public class GameLoop implements Runnable {
    private static final int MAX_FPS_TIME = 1000 / 60;//Maximum render time (Will make the game run smoother NOT faster)
    public static int MS_PER_UPDATE = 25;//Control Speed of Game (Simulation Speed).The more , the SLOWER the game

    private final GameFrame gameFrame;
    private GameState state;

    private boolean paused = false;
    private boolean keyPressed = false;
    private boolean gameFinished;

    public GameLoop(GameFrame frame) {
        gameFrame = frame;
    }

    /**
     * This must be called before the game loop starts.
     */
    public void init() {
        state = new GameState();
        gameFrame.addKeyListener(state.getKeyListener());
        gameFrame.addMouseListener(state.getMouseListener());
        gameFrame.addMouseMotionListener(state.getMouseMotionListener());
        gameFrame.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                state.resetKeys();
            }
        });
        gameFrame.addMouseWheelListener(e -> {
            if (Map.map != null) {
                if (e.getWheelRotation() < 0)
                    Map.zoomIn();
                else
                    Map.zoomOut();
            }
        });
        //Pause Key Setting
        gameFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyPressed = true;
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !gameFinished)
                    pause();
                if (e.getKeyCode() == KeyEvent.VK_F1)
                    GameFrame.testMode = !GameFrame.testMode;
            }
        });
        gameFrame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                keyPressed = true;
            }
        });

        gameFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() >= 'a' && e.getKeyChar() <= 'z')
                    Cheater.keyPressed(e.getKeyChar());
            }
        });
    }

    /**
     * Keeps the game alive until Over
     * The loops is modified to be able to change speed of game
     */
    @Override
    public void run() {
        gameFinished = false;
        state.loadMap();

        //Timing Stuff
        double previous = System.currentTimeMillis();
        double current;
        double elapsed;
        double lag = 0.0D;

        if (Map.map != null)
            GAME_LOOP:while (!gameFinished || !keyPressed) {

                current = System.currentTimeMillis();
                elapsed = current - previous;
                previous = current;
                lag += elapsed;

                //Updating Client
                if (CoOpManager.isClient())
                    Map.map = CoOpManager.get();

                while (lag >= MS_PER_UPDATE) {
                    state.update();
                    lag -= MS_PER_UPDATE;
                }

                //Syncing Client and server
                if (CoOpManager.isClient())
                    CoOpManager.send(state.currentMap);
                if (CoOpManager.isServer()) {
                    CoOpManager.send(state.currentMap);
                    Map.map = CoOpManager.get();
                }

                gameFrame.render(state);

                try {
                    Thread.sleep((long) (Math.max(0, MAX_FPS_TIME - (System.currentTimeMillis() - previous))));
                } catch (InterruptedException e) {
                    System.err.println("Unexpected Occasion.Game will Terminate");
                    break;
                }

                if (paused) {
                    gameFrame.render(state);
                    //Show Paused Dialogue
                    switch (JOptionPane.showOptionDialog(gameFrame
                            , "Game Paused !", "Paused"
                            , JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE
                            , null, new Object[]{"Continue", "Save And Quit", "Quit"}, "Continue")) {
                        case JOptionPane.CLOSED_OPTION:
                        case JOptionPane.YES_OPTION:
                            System.out.println("Game continues");
                            paused = false;
                            previous = System.currentTimeMillis();
                            continue;
                        case JOptionPane.NO_OPTION:
                            System.out.println("Save and Quits");
                            Map.map.mapLives = GameState.lives;//Saving lives of player
                            Map.saveMap(state.currentMap);
                            break GAME_LOOP;
                        case JOptionPane.CANCEL_OPTION:

                            System.out.println("Game Quits");
                            break GAME_LOOP;
                        default:
                            System.out.println("Unexpected");
                            break;
                    }
                }

                gameFinished = state.gameOver || state.gameCompleted;

                if (!gameFinished)
                    keyPressed = false;

            }

        if (gameFinished) {
            gameFrame.render(state);
            gameFrame.setPanel(MainMenu.panel);
        } else {
            //when player quits by pausing
            paused = false;
            gameFrame.setPanel(MainMenu.panel);
        }


        //Game is Finished Here
        if (CoOpManager.isServer() || CoOpManager.isClient())
            CoOpManager.disconnect();
        Map.map = null;//Delete current Game Progress
        state.gameOver = false;//Game is On ;)
        state.gameCompleted = false;
        keyPressed = false;

    }

    private void pause() {
        paused = true;
    }

}
