package jtanks.GUI;

import jtanks.AllImages;
import jtanks.GameRender.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static jtanks.GameRender.GameFrame.GAME_HEIGHT;
import static jtanks.GameRender.GameFrame.GAME_WIDTH;

/**
 * Shows help Picture
 * Exits if any key or mouse pressed
 */
class Help extends JPanel {

    void refresh(){
        revalidate();
        repaint();
    }

    Help() {
        setOpaque(false);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println(":/");
                GameFrame.frame.setPanel(MainMenu.panel);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GameFrame.frame.setPanel(MainMenu.panel);
            }
        });
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(AllImages.help, 0, 0, GAME_WIDTH, GAME_HEIGHT-35, null);
    }

}
