package MainGameStage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientConnection {
    private static final int SERVER_PORT = 4000; // Replace with your server's port number
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private String username;

    public ClientConnection(String serverIP) {
        try {
            socket = new Socket(serverIP, SERVER_PORT);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int createRoom() {
        try {
            outputStream.writeUTF("CREATE_ROOM " + username);
            receive();
            String message = receive();
            int roomId = Integer.parseInt(message.split(" ")[1]);
            System.out.println(message);
            return roomId;
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // Error occurred
        }
    }

    public int joinRoom(int roomId) {
        try {
            outputStream.writeUTF("JOIN_ROOM " + roomId + " " + username);
            String message = receive();
            System.out.println(message);
            String[] request = message.split(" ");
            if (!request[0].equals("ROOM_FULL") && !request[0].equals("ROOM_NOT_FOUND"))
                return Integer.parseInt(message.split(" ")[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void leaveRoom() {
        try {
            outputStream.writeUTF("LEAVE_ROOM");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        try {
            outputStream.writeUTF("START_GAME");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pressKey(String keypressed) {
        try {
            outputStream.writeUTF("KEY_PRESS " + username + " " + keypressed);
            // Send position information here
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void releaseKey(String keyreleased) {
        try {
            outputStream.writeUTF("KEY_RELEASE " + username + " " + keyreleased);
            // Send position information here
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String receive() {
        try {
            return inputStream.readUTF();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
