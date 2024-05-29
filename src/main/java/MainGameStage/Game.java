package MainGameStage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.File;

public class Game {
    private Stage stage;
    private Scene splashScene;     // the splash scene
    private Scene gameScene;       // the game scene
    private StackPane root;
    private ChatApp chat;
    private Canvas canvas;         // the canvas where the animation happens
    private double bgOffsetX = 0;  // Initial X offset for the background image
    private AnimationTimer animationTimer; // Declare AnimationTimer as a class member
    private ServerSocket serverSocket;
    private List<Socket> clients;
    private int numPlayers;
    private final int maxPlayers = 4;

    public final static int WINDOW_WIDTH = 1500;
    public final static int WINDOW_HEIGHT = 800;

    public Game() {
        this.canvas = new Canvas(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        this.chat = new ChatApp();
        this.root = new StackPane();
        playStartSound();
        StackPane.setAlignment(this.canvas, Pos.CENTER);
        this.root.getChildren().addAll(this.canvas);
        this.gameScene = new Scene(this.root);
        this.clients = new ArrayList<>();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setTitle("Tanks 2024");

        this.initSplash(stage);         // initializes the Splash Screen with the New Game button

        stage.setScene(this.splashScene);
        stage.setResizable(false);
        stage.show();
    }

    private void playStartSound() {
        Platform.runLater(() -> {
            try {
                String audioFilePath = "start.mp3";
                File audioFile = new File(audioFilePath);
                // if (audioFile.exists()) {
                //     // Media sound = new Media(audioFile.toURI().toString());
                //     // MediaPlayer mediaPlayer = new MediaPlayer(sound);
                //     // mediaPlayer.play();
                // } else {
                //     System.err.println("Audio file not found: " + audioFilePath);
                // }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initSplash(Stage stage) {
        StackPane root = new StackPane();
        root.getChildren().addAll(this.createCanvas(), this.createVBox());
        this.splashScene = new Scene(root);
    }

    private Canvas createCanvas() {
        Canvas canvas = new Canvas(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Use an AnimationTimer to continuously redraw the background
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                drawBackground(gc);
            }
        };
        animationTimer.start();

        return canvas;
    }

    // Method to draw the background image with scrolling effect
    private void drawBackground(GraphicsContext gc) {
        // Clear canvas
        gc.clearRect(0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);

        // Draw background image
        Image bg = new Image(getClass().getResourceAsStream("/images/gameScreen.png"));

        // Calculate the new offset based on time or player position
        // For example, you can use time to make it scroll automatically
        bgOffsetX -= 1; // Adjust the scrolling speed as needed
        
        // Draw the background image twice to create the scrolling effect
        gc.drawImage(bg, bgOffsetX, 0);
        gc.drawImage(bg, bgOffsetX + bg.getWidth(), 0);

        // If the first image is out of view, reset the offset
        if (bgOffsetX <= -bg.getWidth()) {
            bgOffsetX = 0;
        }
    }

    private VBox createVBox() {
        Image newGame = new Image(getClass().getResourceAsStream("/images/create.png"));
        ImageView title = new ImageView(new Image(getClass().getResourceAsStream("/images/logo.png")));
        ImageView newGameView = new ImageView(newGame);

        ImageView joinGameView = new ImageView(new Image(getClass().getResourceAsStream("/images/join.png")));
        ImageView startGameView = new ImageView(new Image(getClass().getResourceAsStream("/images/new.png")));

        VBox vbox = new VBox(title);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(120));
        vbox.setSpacing(40);

        title.setFitWidth(600);
        title.setPreserveRatio(true);
        newGameView.setFitHeight(70);
        newGameView.setFitWidth(200);
        newGameView.setPreserveRatio(true);
        startGameView.setFitHeight(70);
        startGameView.setFitWidth(200);
        startGameView.setPreserveRatio(true);
        joinGameView.setFitHeight(70);
        joinGameView.setFitWidth(200);
        joinGameView.setPreserveRatio(true);

        Button b1 = new Button();
        b1.setStyle("-fx-background-color: blue");
        b1.setPrefSize(220, 70);
        b1.setGraphic(newGameView);

        Button b2 = new Button();
        b2.setStyle("-fx-background-color: blue");
        b2.setPrefSize(220, 70);
        b2.setGraphic(joinGameView);

        Button b3 = new Button();
        b3.setStyle("-fx-background-color: blue");
        b3.setPrefSize(220, 70);
        b3.setGraphic(startGameView);

        vbox.getChildren().addAll(b1, b2);

        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                chat.setIsServer(true);
                chat.createContent();
                VBox chatBox = chat.createContent();
                chatBox.setPadding(new Insets(0, 64, 0, 64));
                StackPane.setAlignment(chatBox, Pos.BOTTOM_RIGHT);
                root.getChildren().add(chatBox);
                b1.setVisible(false);
                b2.setVisible(false);
                vbox.getChildren().clear();
                vbox.getChildren().addAll(title, b3);
                vbox.setSpacing(155);
                
                // Start the server
                new Thread(() -> startServer(12345)).start();
            }
        });

        b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                chat.setIsServer(false);
                chat.createContent();
                VBox chatBox = chat.createContent();
                chatBox.setPadding(new Insets(0, 64, 0, 64));
                StackPane.setAlignment(chatBox, Pos.BOTTOM_RIGHT);
                root.getChildren().add(chatBox);
                b1.setVisible(false);
                b2.setVisible(false);

                Label infoLabel = new Label("Waiting for players...");
                infoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));
                infoLabel.setTextFill(Color.WHITE);
                vbox.getChildren().clear();
                vbox.getChildren().addAll(title, infoLabel);
                vbox.setSpacing(172);

                // Connect to the server
                new Thread(() -> connectToServer("localhost", 12345)).start();
            }
        });

        b3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                sendMessageToAllClients("START_GAME");
                setGame(stage);
            }
        });

        return vbox;
    }

    private void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToServer(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            clients.add(socket);
            System.out.println("Connected to server");

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        String serverMessage = dis.readUTF();
                        System.out.println("Server: " + serverMessage);
                        // Handle server message here (e.g., update game state)
                        if (serverMessage.equals("START_GAME")) {
                            Platform.runLater(() -> startClientGame());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        String serverMessage = dis.readUTF();
                        System.out.println("Server: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            while (true) {
                String message = dis.readUTF();
                System.out.println("Received: " + message);

                // Broadcast message to all clients
                for (Socket socket : clients) {
                    if (!socket.equals(clientSocket)) {
                        DataOutputStream dosClient = new DataOutputStream(socket.getOutputStream());
                        dosClient.writeUTF(message);
                    }
                }

                // Server logic to send messages to the client
                String serverMessage = "Server message to client " + clientSocket.getRemoteSocketAddress();
                dos.writeUTF(serverMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToAllClients(String message) {
        for (Socket socket : clients) {
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startClientGame() {
        if (animationTimer != null) {
            animationTimer.stop();
            stage.setScene(this.gameScene);

            GraphicsContext gc = this.canvas.getGraphicsContext2D(); // we will pass this gc to be able to draw on this Game's canvas
            GameTimer gameTimer = new GameTimer(stage, this.gameScene, gc, this.chat);
            gameTimer.start(); // this internally calls the handle() method of our GameTimer
        }
    }

    void setGame(Stage stage) {
        if (animationTimer != null) {
            animationTimer.stop();
            stage.setScene(this.gameScene);

            GraphicsContext gc = this.canvas.getGraphicsContext2D(); // we will pass this gc to be able to draw on this Game's canvas
            GameTimer gameTimer = new GameTimer(stage, this.gameScene, gc, this.chat);
            gameTimer.start(); // this internally calls the handle() method of our GameTimer   
        }
    }
}
