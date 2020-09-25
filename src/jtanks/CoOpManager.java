package jtanks;

import jtanks.GameComponents.Map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * For Multilayer
 * Uses Server-Client Mode with TCP
 */
public class CoOpManager {

    private static String myID = null;//identifier for the player , unique for each game
    private static ServerSocket server = null;
    private static Socket client = null;
    private static ObjectInputStream inputStream;
    private static ObjectOutputStream outputStream;

    /**
     * Used to start a server and host a game
     *
     * @param port the running port for server
     */
    public static void initServer(int port) {
        disconnect();
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Error Creating Server " + e.getMessage() + " , " + e.getCause());
        }
        System.out.println("Server Running At " + server.getInetAddress());
        try {
            client = server.accept();
            initStreams();
            System.out.println("Connection Established with " + client);
        } catch (IOException e) {
            System.err.println("Error Accepting Client " + e.getMessage() + " , " + e.getCause());
        }

    }

    /**
     * Used to connect to a server
     *
     * @param serverIP server's ip
     * @param port     port to connect with
     */
    public static void initClient(String serverIP, int port) {
        disconnect();
        try {
            client = new Socket(serverIP, port);
            initStreams();
        } catch (IOException e) {
            System.err.println("Error Connecting to Server " + e.getMessage() + " , " + e.getCause());
        }
        System.out.println("Connection Established");
    }

    /**
     * sends data to the other side of connection , depends who calls this
     *
     * @param map the Map sent
     */
    public static void send(Map map) {
        try {
            if (map == null)
                System.err.println("Null Map ?!?!");
            outputStream.writeObject(map);
        } catch (IOException | NullPointerException e) {
            if (!e.getMessage().contains("Socket closed"))
                System.err.println("Error Sending Data to client " + e.getMessage() + " , " + e.getCause());
            disconnect();
        }
    }

    /**
     * Receive data from other side of connection
     *
     * @return the received Map if data is received and otherwise current map
     */
    public static Map get() {
        try {
            Map map = (Map) inputStream.readObject();
            if (map == null) {
                System.err.println("Null Got :(");
                map = Map.map;
            }
            return map;
        } catch (IOException e) {
            System.err.println("Error Receiving Data from " + (isServer() ? "Client " : "Server ") + e.getMessage() + " , " + e.getCause());
            Map.map.objectsInMap.removeIf(o -> o.identifier.contains("PlayerTank0.") && !o.identifier.equals(myID));
            disconnect();
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid Data from  " + (isServer() ? "Client" : "Server") + e.getMessage() + " , " + e.getCause());
            disconnect();
        }
        return Map.map;
    }

    /**
     * Initializes input and output streams
     * Streams are not closed during game
     */
    private static void initStreams() {
        try {
            outputStream = new ObjectOutputStream(client.getOutputStream());
            inputStream = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.err.println("Error Opening Streams " + e.getMessage() + " , " + e.getCause());
        }
    }

    /**
     * When connection is lost
     */
    public static void disconnect() {
        if (isServer() || isClient())
            System.out.println("Disconnected");
        try {
            if (client != null)
                client.close();
            if (server != null)
                server.close();
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
        } catch (IOException e) {
            if (!e.getMessage().contains("Socket closed"))
                System.err.println("Error Disconnecting " + e.getMessage() + " , " + e.getCause());
        } finally {
            client = null;
            server = null;
        }
    }

    /**
     * @return Whether current machine is a server or not
     */
    public static boolean isServer() {
        return server != null;
    }

    /**
     * @return Whether current machine is a client or not (if this is server , false will be returned)
     */
    public static boolean isClient() {
        return server == null && client != null;
    }

    /**
     * @param myID player's current identifier
     */
    public static void setMyID(String myID) {
        CoOpManager.myID = myID;
    }

    /**
     * @return ID of  a player which is its tank's identifier
     */
    public static String getMyID() {
        return myID;
    }

}
