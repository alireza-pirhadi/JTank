package jtanks.GUI;

import jtanks.AllImages;
import jtanks.GameComponents.*;
import jtanks.GameRender.GameFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static jtanks.GameComponents.Map.ROOM_HEIGHT;
import static jtanks.GameComponents.Map.ROOM_WIDTH;

/**
 * A tool for creating a map
 * Objects are put on a grid
 * and background is chosen
 * and by creating , you can save your map
 */
class MapEditor extends JPanel {
    private final String[][] objectNames;

    MapEditor(int row, int column) {
        super(new BorderLayout(0, 0));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        add(userPanel, BorderLayout.SOUTH);
        setBackground(Color.WHITE);

        objectNames = new String[row][column];
        String[] tmp = new String[column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                objectNames[i][j] = "Empty";
                tmp[j] = "";
            }
        }

        JLabel label = new JLabel("Welcome To Map Editor");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(217, 255, 241));
        label.setForeground(new Color(225, 177, 130));
        label.setFont(new Font("Times New Roman", Font.BOLD, 32));
        add(label, BorderLayout.NORTH);
        JTable table = new JTable(objectNames, tmp);
        table.getColumnModel().getColumns().asIterator().forEachRemaining(c -> c.setWidth(GameFrame.GAME_WIDTH / 15));
        table.setRowHeight(GameFrame.GAME_WIDTH / 15);
        table.setPreferredScrollableViewportSize(new Dimension(GameFrame.GAME_WIDTH, GameFrame.GAME_HEIGHT));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JComboBox<String> backGrounds = new JComboBox<>(new String[]{"Road","Ice","Lava"});
        backGrounds.setSelectedItem("Road");
        backGrounds.setToolTipText("Choose Map's Background");
        backGrounds.setFont(new Font("Georgia", Font.BOLD, 20));
        userPanel.add(backGrounds);

        JComboBox<String> choices = new JComboBox<>(new String[]{"PlayerTank", "EnemyTank", "EnemyBug"
                , "RocketLauncher", "PlasmaGun", "Wall", "Plant", "Bush", "Teazel", "StoneWall", "Empty"});
        choices.setSelectedItem("PlayerTank");
        choices.setToolTipText("Choose the object to add to Map");
        choices.setFont(new Font("Georgia", Font.BOLD, 20));
        userPanel.add(choices);



        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                int selectedColumn = table.getSelectedColumn();
                if (selectedColumn != -1 && selectedRow != -1)
                    objectNames[selectedRow][selectedColumn] = ((String) choices.getSelectedItem());
                SwingUtilities.invokeLater(table::repaint);
            }
        });

        for (int i = 0; i < column; i++) {
            table.setDefaultRenderer(table.getColumnClass(i), new TableRenderer());
        }

        table.setDefaultEditor(Object.class, null);
        table.setTableHeader(null);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.setFont(new Font("Georgia", Font.BOLD, 20));
        close.addActionListener(e -> {
            GameFrame.frame.setPanel(MainMenu.panel);
            GameFrame.frame.pack();
        });

        JButton create = new JButton("Create Map");
        create.setFont(new Font("Georgia", Font.BOLD, 20));
        userPanel.add(create);
        userPanel.add(close);
        userPanel.setBackground(new Color(230, 244, 239));
        create.addActionListener(e -> {
            Map resultMap = new Map(row, column);
            resultMap.backgroundNum = backGrounds.getSelectedIndex();
            Map.map = null;
            Tank playerTank = null;
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    if (objectNames[i][j].contains("PlayerTank")) {
                        if (playerTank == null)
                            playerTank = new Tank(j * ROOM_WIDTH + ROOM_WIDTH / 2, i * ROOM_HEIGHT + ROOM_HEIGHT / 2, "PlayerTank" + Math.random());
                        else {
                            System.err.println("More Than One Player !");
                            return;
                        }
                    }
                }
            }
            if (playerTank == null) {
                System.err.println("No Player !");
                return;
            }
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    switch (objectNames[i][j]) {
                        case "PlayerTank":
                            resultMap.addObject(playerTank);
                            break;
                        case "Wall":
                            resultMap.addObject(new Wall(j * ROOM_WIDTH + ROOM_WIDTH / 2, i * ROOM_HEIGHT + ROOM_HEIGHT / 2));
                            break;
                        case "StoneWall":
                            resultMap.addObject(new StoneWall(j * ROOM_WIDTH + ROOM_WIDTH / 2, i * ROOM_HEIGHT + ROOM_HEIGHT / 2));
                            break;
                        case "Plant":
                            resultMap.addObject(new Plant(j * ROOM_WIDTH + ROOM_WIDTH / 2, i * ROOM_HEIGHT + ROOM_HEIGHT / 2));
                            break;
                        case "Bush":
                            resultMap.addObject(new Bush(j * ROOM_WIDTH + ROOM_WIDTH / 2, i * ROOM_HEIGHT + ROOM_HEIGHT / 2));
                            break;
                        case "Teazel":
                            resultMap.addObject(new Teazel(j * ROOM_WIDTH + ROOM_WIDTH / 2, i * ROOM_HEIGHT + ROOM_HEIGHT / 2));
                            break;
                        case "EnemyBug":
                            resultMap.addObject(new BugEnemy(j * ROOM_WIDTH + ROOM_WIDTH / 2, i * ROOM_HEIGHT + ROOM_HEIGHT / 2));
                            break;
                        case "EnemyTank":
                            resultMap.addObject(new TankEnemy(j * ROOM_WIDTH + ROOM_WIDTH / 2, i * ROOM_HEIGHT + ROOM_HEIGHT / 2, playerTank));
                            break;
                        case "PlasmaGun":
                            resultMap.addObject(new PlasmaGun(j * ROOM_WIDTH + ROOM_WIDTH / 2, i * ROOM_HEIGHT + ROOM_HEIGHT / 2));
                            break;
                        case "RocketLauncher":
                            resultMap.addObject(new RocketLauncher(j * ROOM_WIDTH + ROOM_WIDTH / 2, i * ROOM_HEIGHT + ROOM_HEIGHT / 2));

                    }
                }
            }
            resultMap.levelNumber = 0;
            if (Map.saveMap(resultMap)) {
                close.doClick();
            }
        });

    }

    /**
     * For showing Objects icons in table
     */
    private class TableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            ImageIcon icon = new ImageIcon();
            switch (objectNames[row][column]) {
                case "Plant":
                    icon.setImage(AllImages.plant);
                    break;
                case "Bush":
                    icon.setImage(AllImages.bushIcon);
                    break;
                case "PlayerTank":
                    icon.setImage(AllImages.playerTankIcon);
                    break;
                case "Wall":
                    icon.setImage(AllImages.wall);
                    break;
                case "StoneWall":
                    icon.setImage(AllImages.stoneWall);
                    break;
                case "EnemyTank":
                    icon.setImage(AllImages.enemyTankIcon);
                    break;
                case "Teazel":
                    icon.setImage(AllImages.teazelIcon);
                    break;
                case "EnemyBug":
                    icon.setImage(AllImages.bugIcon);
                    break;
                case "RocketLauncher":
                    icon.setImage(AllImages.rocketLauncherIcon);
                    break;
                case "PlasmaGun":
                    icon.setImage(AllImages.plasmaGunIcon);
                    break;
                case "Empty":
                    break;
                default:
                    System.err.println("Unexpected Data in objects");
            }
            return new JLabel("", icon, JLabel.LEADING);
        }
    }

}
