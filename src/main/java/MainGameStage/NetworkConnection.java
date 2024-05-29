package MainGameStage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class NetworkConnection {
    private int numPlayers = 1;
    private int maxPlayers = 4;
    private ArrayList<ConnectionThread> connectionThreads = new ArrayList<ConnectionThread>();
    private Consumer<Serializable> onReceiveCallback;

    public NetworkConnection(Consumer<Serializable> onReceiveCallback) {
        this.onReceiveCallback = onReceiveCallback;
    }
    
    public void startServerConnection() throws Exception {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(getPort())) {
                System.out.println("Waiting for connection..." + numPlayers);
                while (numPlayers != maxPlayers) {
                    Socket client = serverSocket.accept();
                    ConnectionThread connectionThread = new ConnectionThread(client);
                    connectionThreads.add(connectionThread);
                    numPlayers++;
                    connectionThread.start();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }).start();
    }

    public void startClientConnection() throws Exception {
        new Thread(() -> {
            try {
                Socket client = new Socket(getIP(), getPort());
                ConnectionThread connectionThread = new ConnectionThread(client);
                connectionThreads.add(connectionThread);
                connectionThread.start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }).start();
    }

    public int players() {
        return this.numPlayers;
    }

    public void send(Serializable data) throws Exception {
        for (ConnectionThread thread : connectionThreads) {
            thread.send(data);
        }
    }

    public void closeConnection() throws Exception {
        for (ConnectionThread thread : connectionThreads) {
            thread.closeConnection();
        }
    }

    protected abstract boolean isServer();
    protected abstract String getIP();
    protected abstract int getPort();

    private class ConnectionThread extends Thread {
        private Socket socket;
        private ObjectOutputStream out;
        
        ConnectionThread(Socket socket) {
            this.socket = socket;
        }
        // TCP - slow, reliable
        // UDP - fast, unreliable

        public void send(Serializable data) throws Exception {
            out.writeObject(data);
        }
    
        public void closeConnection() throws Exception {
            socket.close();
        }

        @Override
        public void run() {
            if (isServer()) {
                System.out.println("Player " + numPlayers + " joined the game.");
            }
            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                this.out = out;
                socket.setTcpNoDelay(true);

                while (true) {
                    Serializable data = (Serializable) in.readObject();
                    // Broadcast the received data to all clients including the server
                    for (ConnectionThread thread : connectionThreads) {
                        if (thread != this) { // Exclude the current thread (server)
                            thread.send(data);
                        }
                    }
                    
                    // Also handle the received data locally
                    onReceiveCallback.accept(data);
                }
            }
            catch (Exception e) {
                DataPacket closePacket = new DataPacket("Connection closed".getBytes());
                onReceiveCallback.accept(closePacket);
            }
        }
    }
}
