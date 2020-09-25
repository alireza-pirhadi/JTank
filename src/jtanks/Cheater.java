package jtanks;

import jtanks.GameComponents.GameState;
import jtanks.GameComponents.Tank;

import java.awt.*;

/**
 * Used for cheating
 * Cheat codes are entered in console
 *
 * <p>Limitations for cheat codes :
 * 1.Should'nt be more than 10 characters
 * 2.Shouldn't end in any of [w,a,s,d] characters
 * </p>
 */
@SuppressWarnings("SynchronizeOnNonFinalField")
public class Cheater implements Runnable {

    private static char[] inputs;
    private static volatile boolean doReset;
    private static Cheater cheater;
    private static int i;
    private static GameState state = null;

    /**
     * Initializes and runs the cheater
     */
    private Cheater() {
        i = 0;
        inputs = new char[10];
        ThreadPool.execute(this);
    }

    /**
     * Called when a key is pressed
     *
     * @param input the input of user
     */
    public static void keyPressed(char input) {


        if (i >= inputs.length) {
            i = 0;
        }
        inputs[i] = input;
        i++;

        if (input != 'w' && input != 'a' && input != 's' && input != 'd') {
            synchronized (cheater) {
                cheater.notify();
            }
        }

    }

    /**
     * @param gameState the state of current game , used to apply cheats
     */
    public static void setCheater(GameState gameState) {
        state = gameState;
        if (cheater == null) {
            doReset = false;
            cheater = new Cheater();
        } else {
            doReset = true;
            synchronized (cheater) {
                cheater.notify();
            }
            EventQueue.invokeLater(() -> cheater = new Cheater());
        }
    }

    /**
     * Cheating part , which will check inputs and apply cheat when code is entered
     */
    @Override
    public void run() {

        while (doReset) {
            Thread.onSpinWait();
        }

        while (!doReset) {

            synchronized (cheater) {
                try {
                    cheater.wait();
                } catch (InterruptedException e) {
                    System.err.println("Cheater Interrupted");
                    break;
                }
            }

            //Input of user , a copy is placed to make sure code is got from user
            String string = String.valueOf(inputs) + String.valueOf(inputs);

            try {
                if (string.contains("hesoyam")) {
                    state.playerTank.heal();
                    state.playerTank.bullets = Tank.MAX_BULLET_NUM;
                    state.playerTank.setShield();
                    System.out.println("Cheat Activated");
                    resetInputs();
                    continue;
                }
                if (string.contains("run")) {
                    state.playerTank.switchBoost();
                    System.out.println("Cheat Activated");
                    resetInputs();
                    continue;
                }
                if (string.contains("safe")) {
                    state.playerTank.switchInfiniteShield();
                    System.out.println("Cheat Activated");
                    resetInputs();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        doReset = false;//to allow other cheaters to get activated

    }

    /**
     * Resets the inputs to avoid applying a code twice
     */
    private void resetInputs() {
        for (int j = 0; j < inputs.length; j++) {
            inputs[j] = ' ';
        }
        i = 0;
    }

}
