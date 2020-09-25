package jtanks;

import jtanks.GUI.MainMenu;
import jtanks.GameRender.GameFrame;
import jtanks.GameRender.GameLoop;

import javax.swing.*;
import java.awt.*;

/**
 * <p>In The Name of Allah</p>
 * JTanks Game
 * And that't it
 *
 * @author Alireza.PirHadi
 * @author Amir.MA
 * @version 1.1
 */

class Main {

    public static void main(String[] args) {

        //Initialize the global thread-pool
        ThreadPool.init();

        //Loading Images
        new AllImages();

        //Loading Sound
        new SoundPlayer();

        //Initializing game frame and main menu and show it , But NOT running game
        EventQueue.invokeLater(() -> {
            GameFrame frame = new GameFrame("Java Tanks");
            GameFrame.frame = frame;
            frame.setLocationRelativeTo(null); // put frame at center of screen
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //Don't Touch here :|
            frame.setVisible(true);
            frame.initBufferStrategy();

            GameLoop game = new GameLoop(frame);
            game.init();
            MainMenu.init(game, frame);//Initializing Main Menu
            frame.setPanel(MainMenu.panel);//Main Menu will show First
        });

    }

}
