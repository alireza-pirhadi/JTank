package jtanks.GUI;

import jtanks.AllImages;
import jtanks.CoOpManager;
import jtanks.GameComponents.Map;
import jtanks.GameRender.GameFrame;
import jtanks.GameRender.GameLoop;
import jtanks.ThreadPool;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import static jtanks.GameRender.GameFrame.GAME_HEIGHT;
import static jtanks.GameRender.GameFrame.GAME_WIDTH;

/**
 * Main Menu of program
 * <p>
 * Will Contain :
 * 1.Start New Game
 * 2.Load from previously saved game
 * 3.Co-Op
 * 4.Settings
 * 5.Help
 * 6.Exit
 */
public class MainMenu extends JPanel {

    public static JPanel panel;//Instance of JPanel
    private final Settings settings;
    private final JButton start;
    private final TextField statusField;

    private MainMenu(GameLoop gameLoop, GameFrame frame) {
        super();
        Font georgia = new Font("Georgia", Font.BOLD, 30);
        settings = new Settings(frame, this);
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));//Can be Replaced with costume cursor (look at GameFrame line 56)
        setLayout(null);//No Need to use Layout in this (Not recommended actually)

        JButton setting = new JButton("Setting");
        setting.setOpaque(false);
        setting.setLocation((GAME_WIDTH / 7) + 600, 5 * GAME_HEIGHT / 8);
        setting.setSize(240, 80);
        setting.setBackground(new Color(111, 155, 160));
        setting.setForeground(Color.PINK);
        setting.setFont(georgia);
        setting.addActionListener(e -> {
            frame.setPanel(settings);
            frame.pack();
        });
        add(setting);

        JButton help = new JButton("Help");
        help.setOpaque(false);
        help.setLocation(GAME_WIDTH - 200, 2 * GAME_HEIGHT / 8);
        help.setSize(100, 70);
        help.setBackground(new Color(111, 155, 160));
        help.setForeground(new Color(218, 255, 255));
        help.setFont(georgia.deriveFont(22f));
        help.addActionListener(e -> {
            Help panel = new Help();
            frame.setPanel(panel);
            panel.refresh();
        });
        add(help);

        JButton coOp = new JButton("Co-Op");
        coOp.setOpaque(false);
        coOp.setLocation((GAME_WIDTH / 7) + 150, 2 * GAME_HEIGHT / 8);
        coOp.setBackground(new Color(111, 155, 160));
        coOp.setForeground(Color.WHITE);
        coOp.setFont(georgia);
        coOp.setSize(240, 80);
        statusField = new TextField("NotRunning");
        coOp.addActionListener(e -> {
            statusField.setText("NotRunning");
            statusField.setFont(new Font("Arial", Font.BOLD, 15));
            statusField.setEditable(false);

            CoOpWorker worker = new CoOpWorker(JOptionPane.showOptionDialog(JOptionPane.getRootFrame()
                    , "Choose An Option", "Co-Op Mode",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    new Object[]{"Run Server", "Be Client", "Cancel"}, "Cancel"), statusField);

            worker.execute();

            ThreadPool.execute(() -> {

                //Waiting For Client/Server to Start
                synchronized (statusField) {
                    try {
                        statusField.wait();
                    } catch (InterruptedException e1) {
                        System.err.println("Status Field Interrupted");
                        return;
                    }
                }

                if (!statusField.getText().equals("Cancelled")) {
                    int i = JOptionPane.showOptionDialog(JOptionPane.getRootFrame()
                            , statusField, "Co-Op Initializing",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                            new Object[]{"Cancel"}, null);
                    if (i == JOptionPane.OK_OPTION || i == JOptionPane.CLOSED_OPTION) {
                        worker.cancel(true);
                    }
                }

            });

        });
        add(coOp);

        //About Button is not needed
        JButton mapEditor = new JButton("Map Editor");
        mapEditor.setOpaque(false);
        mapEditor.setLocation((GAME_WIDTH / 7) + 450, GAME_HEIGHT / 2);
        mapEditor.setSize(240, 80);
        mapEditor.setBackground(new Color(111, 155, 160));
        mapEditor.setForeground(new Color(71, 199, 139));
        mapEditor.setFont(georgia);
        mapEditor.addActionListener(e -> {
            Object[] options = new Object[4];
            JLabel rowLabel = new JLabel("Number Of Rows :");
            JLabel colLabel = new JLabel("Number Of Columns :");
            TextField rowField = new TextField("10");
            TextField colField = new TextField("20");
            options[0] = rowLabel;
            options[1] = rowField;
            options[2] = colLabel;
            options[3] = colField;
            if (JOptionPane.showOptionDialog(JOptionPane.getRootFrame(),
                    options, "Enter Information Needed",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE
                    , null, null, null) == JOptionPane.OK_OPTION) {
                try {
                    frame.setPanel(new MapEditor(Integer.parseInt(rowField.getText()), Integer.parseInt(colField.getText())));
                } catch (NumberFormatException e1) {
                    System.err.println("Invalid Data !");
                    return;
                }
                frame.pack();
            }
        });
        add(mapEditor);


        //Starts Game
        start = new JButton("New Game");
        start.setOpaque(false);
        start.setLocation(GAME_WIDTH / 7, GAME_HEIGHT / 8);
        start.setSize(240, 80);
        start.setBackground(new Color(111, 155, 160));
        start.setForeground(Color.BLACK);
        start.setFont(georgia);
        start.addActionListener(e -> {
            frame.showGame();
            EventQueue.invokeLater(() -> ThreadPool.execute(gameLoop));
        });
        add(start);

        JButton continueGame = new JButton("Load");
        continueGame.setOpaque(false);
        continueGame.setLocation((GAME_WIDTH / 7) + 300, 3 * GAME_HEIGHT / 8);
        continueGame.setSize(240, 80);
        continueGame.setBackground(new Color(111, 155, 160));
        continueGame.setForeground(Color.LIGHT_GRAY);
        continueGame.setFont(georgia);
        continueGame.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser("./", FileSystemView.getFileSystemView());
                    fileChooser.setMultiSelectionEnabled(false);
                    fileChooser.setDialogTitle("Choose Map to Load");
                    if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        File selectedMap = fileChooser.getSelectedFile();
                        if (!selectedMap.isDirectory() && selectedMap.exists()) {
                            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(selectedMap))) {
                                Map.map = ((Map) inputStream.readObject());
                                start.doClick();
                            } catch (IOException | ClassNotFoundException | NullPointerException e1) {
                                System.err.println("Error Loading Map " + e1.getMessage());
                            }
                        } else
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "No Saved Map");
                    }
                }
        );
        add(continueGame);

        //Exit Game
        JButton exit = new JButton("Exit");
        exit.setOpaque(false);
        exit.setLocation((GAME_WIDTH / 7) + 750, 6 * GAME_HEIGHT / 8);
        exit.setSize(240, 80);
        exit.setBackground(new Color(111, 155, 160));
        exit.setForeground(Color.CYAN);
        exit.setFont(georgia);
        exit.addActionListener(e -> System.exit(0));
        add(exit);


        requestFocusInWindow();
        revalidate();
        updateUI();
    }

    public static void init(GameLoop gameLoop, GameFrame frame) {
        panel = new MainMenu(gameLoop, frame);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(AllImages.mainMenuBackGround, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
    }

    /**
     * Used for avoid GUI lock while connecting
     */
    private class CoOpWorker extends SwingWorker<String, String> {

        private final TextField statusField;
        private final int option;

        CoOpWorker(int option, TextField statusField) {
            this.statusField = statusField;
            this.option = option;
        }

        @Override
        protected String doInBackground() {
            switch (option) {
                case JOptionPane.YES_OPTION://Be Server
                    try {
                        int port = Integer.parseInt(JOptionPane.showInputDialog(JOptionPane.getRootFrame(), "Enter Port to run server On :", "7777"));
                        synchronized (statusField) {
                            statusField.notify();
                        }
                        publish("Creating Server");
                        publish("Waiting For Connection . . .");
                        CoOpManager.initServer(port);
                        publish("Ready !");
                    } catch (NumberFormatException e1) {
                        System.err.println("Invalid Input");
                        return "Failed";
                    }
                    break;
                case JOptionPane.NO_OPTION://Be Client
                    try {
                        int port = Integer.parseInt(JOptionPane.showInputDialog(JOptionPane.getRootFrame(), "Enter Port of server :", "7777"));
                        String ip = JOptionPane.showInputDialog(JOptionPane.getRootFrame(), "Enter IP of server :", "127.0.0.1");
                        synchronized (statusField) {
                            statusField.notify();
                        }
                        publish("Connecting to Server");
                        CoOpManager.initClient(ip, port);
                        publish("Getting Files from Server");
                        Map.map = CoOpManager.get();
                        publish("Ready !");
                    } catch (NumberFormatException | HeadlessException e) {
                        System.err.println("Invalid Input");
                        return "Failed";
                    }
                    break;
                default:
                    synchronized (statusField) {
                        statusField.notify();

                    }
                    publish("Cancelled");
                    return "Cancelled";
            }
            return "Done";
        }

        @Override
        protected void process(List<String> chunks) {
            chunks.forEach(statusField::setText);
        }

        @Override
        protected void done() {
            try {
                if (get().equals("Done"))
                    start.doClick();
                else
                    System.out.println(get());
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error Occurred " + e.getMessage() + ' ' + e.getCause());
            } catch (CancellationException e) {
                System.err.println("Job Cancelled ");
            } finally {
                JOptionPane.getRootFrame().dispose();
            }
        }
    }

}
