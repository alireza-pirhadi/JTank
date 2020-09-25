package jtanks.GUI;

import jtanks.AllImages;
import jtanks.GameComponents.GameState;
import jtanks.GameRender.GameFrame;
import jtanks.GameRender.GameLoop;
import jtanks.SoundPlayer;

import javax.swing.*;
import java.awt.*;

import static jtanks.GameRender.GameFrame.GAME_HEIGHT;
import static jtanks.GameRender.GameFrame.GAME_WIDTH;

/**
 * Settings for program
 * Speed of game , muting sounds and game difficulty can be changed here
 */
class Settings extends JPanel {
    public Settings(GameFrame frame, JPanel panel) {
        setLayout(new BorderLayout());
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        setOpaque(false);

        JPanel checkboxPanel = new JPanel();
        JCheckBox checkBox = new JCheckBox();
        checkBox.setText("Mute");
        checkBox.setVerticalAlignment(SwingConstants.CENTER);
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
        checkBox.setHorizontalTextPosition(SwingConstants.LEFT);
        checkBox.setIconTextGap(80);
        checkBox.setPreferredSize(new Dimension(300,200));
        checkBox.setOpaque(false);
        checkBox.setFont(new Font("Arial", Font.PLAIN, 50));
        checkBox.setForeground(Color.WHITE);
        checkBox.setSelected(SoundPlayer.muted);
        checkboxPanel.add(checkBox);
        checkboxPanel.setOpaque(false);

        //Difficulty Level ComboBox
        JComboBox<String> difficulties = new JComboBox<>(new String[]{"Easy", "Normal", "Hard"});
        difficulties.setSelectedIndex(GameState.difficultyLevel);
        difficulties.setFont(new Font("Georgia", Font.BOLD, 40));
        difficulties.setOpaque(true);
        difficulties.setBackground(Color.WHITE);

        JPanel sliderPanel = new JPanel();
        JLabel difficultyLabel = new JLabel("Difficulty Level");
        difficultyLabel.setFont(new Font("Georgia", Font.PLAIN, 50));
        difficultyLabel.setForeground(Color.white);
        difficultyLabel.setVerticalAlignment(SwingConstants.CENTER);
        difficultyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sliderPanel.setLayout(new GridLayout(2,2,10,50));
        sliderPanel.setOpaque(false);
        JLabel speed = new JLabel("Speed");
        speed.setFont(new Font("Georgia", Font.PLAIN, 50));
        speed.setForeground(Color.white);
        speed.setVerticalAlignment(SwingConstants.CENTER);
        speed.setHorizontalAlignment(SwingConstants.CENTER);
        JSlider slider = new JSlider(0, 40, 25);
        slider.setOpaque(false);
        slider.setMajorTickSpacing(2);
        slider.setMinorTickSpacing(1);
        slider.setFont(new Font("Arial", Font.PLAIN, 20));
        slider.setForeground(Color.white);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        sliderPanel.add(difficultyLabel);
        sliderPanel.add(difficulties);
        sliderPanel.add(speed);
        sliderPanel.add(slider);

        JPanel okPanel = new JPanel();
        okPanel.setOpaque(false);
        okPanel.setLayout(new BorderLayout());
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> {
            SoundPlayer.muted = checkBox.isSelected();
            synchronized (SoundPlayer.class) {
                if (!SoundPlayer.muted) {
                    SoundPlayer.class.notify();
                }
            }
            GameLoop.MS_PER_UPDATE = 50 - slider.getValue();
            GameState.difficultyLevel= difficulties.getSelectedIndex();
            frame.setPanel(panel);
        });
        ok.setFont(new Font("Arial", Font.BOLD, 40));
        ok.setForeground(Color.white);
        okPanel.add(ok, BorderLayout.CENTER);
        ok.setOpaque(false);
        ok.setBackground(Color.WHITE);
        ok.setForeground(Color.WHITE);
        JPanel cancelPanel = new JPanel();
        cancelPanel.setOpaque(false);
        cancelPanel.setLayout(new BorderLayout());
        cancelPanel.setOpaque(false);
        JButton cancel = new JButton("Cancel");
        cancel.setOpaque(false);
        cancel.setBackground(Color.WHITE);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(e -> {
            slider.setValue(30 - GameLoop.MS_PER_UPDATE);
            frame.setPanel(panel);
        });
        cancel.setFont(new Font("Arial", Font.BOLD, 40));
        cancel.setForeground(Color.white);
        cancelPanel.add(cancel, BorderLayout.CENTER);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new GridLayout(1, 2,50,5));
        buttonsPanel.add(okPanel);
        buttonsPanel.add(cancelPanel);


        add(checkboxPanel,BorderLayout.NORTH);
        add(sliderPanel,BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(AllImages.settingBackGround, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
    }
}
