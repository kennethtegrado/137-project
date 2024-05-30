package Main;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private static final int PORT = 4000;
    private static final int MAX_PLAYERS_PER_ROOM = 4;
    private static final Map<Integer, GameRoom> gameRooms = new ConcurrentHashMap<>();
    private static final AtomicInteger roomCounter = new AtomicInteger(1);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started, waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private DataOutputStream out;
        private DataInputStream in;
        private int roomId = -1;
        public String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());

                while (true) {
                    String request = in.readUTF();
                    
                    if (!request.startsWith("KEY_PRESS") || !request.startsWith("KEY_RELEASE"))
                        System.out.println(request);

                    if (request.startsWith("CREATE_ROOM")) {
                        createRoom();
                        this.username = request.split(" ")[1];
                    } else if (request.startsWith("JOIN_ROOM")) {
                        int roomId = Integer.parseInt(request.split(" ")[1]);
                        joinRoom(roomId);
                        this.username = request.split(" ")[2];
                    } else if (request.startsWith("SEND_CHAT") || request.startsWith("KEY_PRESS") || request.startsWith("KEY_RELEASE")) {
                        GameRoom gameRoom = gameRooms.get(this.roomId);
                        gameRoom.broadcast(request);
                    } else if (request.startsWith("START_GAME")) {
                        GameRoom gameRoom = gameRooms.get(this.roomId);
                        ArrayList<String> usernames = new ArrayList<>();
                        for (ClientHandler client: gameRoom.players) usernames.add(client.username);

                        StringBuilder sb = new StringBuilder();
                        for (String username: usernames) sb.append(username).append(" ");

                        gameRoom.broadcast("START_GAME " + sb.toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                leaveRoom();
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void createRoom() {
            this.roomId = roomCounter.getAndIncrement();
            GameRoom gameRoom = new GameRoom(roomId);
            gameRooms.put(roomId, gameRoom);
            gameRoom.addPlayer(this);
            try {
                out.writeUTF("ROOM_CREATED " + roomId);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void joinRoom(int roomId) {
            GameRoom gameRoom = gameRooms.get(roomId);
            if (gameRoom != null) {
                if (gameRoom.addPlayer(this)) {
                    this.roomId = roomId;
                    try {
                        out.writeUTF("JOINED_ROOM " + roomId);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    try {
                        out.writeUTF("ROOM_FULL");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    out.writeUTF("ROOM_NOT_FOUND");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        private void leaveRoom() {
            if (roomId != -1) {
                GameRoom gameRoom = gameRooms.get(roomId);
                if (gameRoom != null) {
                    gameRoom.removePlayer(this);
                    if (gameRoom.isEmpty()) {
                        gameRooms.remove(roomId);
                    }
                }
            }
        }

        public void sendMessage(String message) {
            try {
                out.writeUTF(message);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    static class GameRoom {
        private int roomId;
        private List<ClientHandler> players = Collections.synchronizedList(new ArrayList<>());

        public GameRoom(int roomId) {
            this.roomId = roomId;
        }

        public synchronized boolean addPlayer(ClientHandler player) {
            if (players.size() < MAX_PLAYERS_PER_ROOM) {
                players.add(player);
                broadcast("PLAYER_JOINED " + players.size());
                return true;
            }
            return false;
        }

        public synchronized void removePlayer(ClientHandler player) {
            players.remove(player);
            broadcast("PLAYER_LEFT " + players.size());
        }

        public boolean isEmpty() {
            return players.isEmpty();
        }

        public void broadcast(String message) {
            synchronized (players) {
                for (ClientHandler player : players) {
                    player.sendMessage(message);
                }
            }
        }
    }
}
