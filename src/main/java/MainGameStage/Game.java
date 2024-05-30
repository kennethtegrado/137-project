package MainGameStage;

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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.File;
import java.util.Scanner;

public class Game {
    private Stage stage;
    private Scene splashScene;     // the splash scene
    private Scene gameScene;       // the game scene
    private StackPane root;
    private ChatApp chat;
    private Canvas canvas;         // the canvas where the animation happens
    private double bgOffsetX = 0;  // Initial X offset for the background image
    private AnimationTimer animationTimer; // Declare AnimationTimer as a class member
    private int numPlayers = 1;
    private final int maxPlayers = 4;
    private ClientConnection connection = createConnection();
    public final static int WINDOW_WIDTH = 1500;
    public final static int WINDOW_HEIGHT = 800;
    private String username;
    private int roomId;
    private GameTimer gameTimer;

    public Game() {
        this.canvas = new Canvas(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        this.chat = new ChatApp(connection, username);
        this.root = new StackPane();
        playStartSound();
        StackPane.setAlignment(this.canvas, Pos.CENTER);
        this.root.getChildren().addAll(this.canvas);
        this.gameScene = new Scene(this.root);
        GraphicsContext gc = this.canvas.getGraphicsContext2D(); // we will pass this gc to be able to draw on this Game's canvas
        this.gameTimer = new GameTimer(this.root, this.gameScene, gc, this.connection);
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

    ClientConnection createConnection() {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Enter Server's IP Address: ");
            String serverIP = sc.nextLine();
            return new ClientConnection(serverIP);
        }
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
        ImageView backView = new ImageView(new Image(getClass().getResourceAsStream("/images/back.png")));

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

        Button b4 = new Button();
        b4.setStyle("-fx-background-color: blue");
        b4.setPrefSize(10, 5);
        b4.setGraphic(backView);


        TextField input = new TextField();
        input.setPromptText("Enter your username");
        input.setPrefHeight(50); 
        input.setMaxWidth(600);  
        input.setStyle("-fx-control-inner-background: #343434; "
                    + "-fx-prompt-text-fill: #aeaeae; "
                    + "-fx-border-color: blue; " 
                    + "-fx-border-width: 8px; " 
                    + "-fx-border-radius: 8px; "
                    + "-fx-font-size: 36px");
        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                username = input.getText();
                chat.setUsername(username);
                connection.setUsername(username);
                gameTimer.setUsername(username);
                System.out.println("Username: " + username);
                
                vbox.getChildren().clear();
                vbox.getChildren().addAll(title, b1, b2);
                vbox.setSpacing(40);

                Client client = new Client(this);
                Thread clientThread = new Thread(client);

                b1.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        roomId = connection.createRoom(); // update roomId
                        VBox chatBox = chat.createContent();
                        chatBox.setPadding(new Insets(0, 64, 0, 64));
                        StackPane.setAlignment(chatBox, Pos.BOTTOM_RIGHT);
                        root.getChildren().add(chatBox);
                        b1.setVisible(false);
                        b2.setVisible(false);
                        vbox.getChildren().clear();
                        vbox.getChildren().addAll(title, b3);
                        vbox.setSpacing(155);
                        clientThread.start();
                    }
                });
        
                b2.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        TextField inputRoomId = new TextField();
                        inputRoomId.setPromptText("Enter Room ID");
                        inputRoomId.setPrefHeight(50); 
                        inputRoomId.setMaxWidth(600);  
                        inputRoomId.setStyle("-fx-control-inner-background: #343434; "
                                    + "-fx-prompt-text-fill: #aeaeae; "
                                    + "-fx-border-color: blue; " 
                                    + "-fx-border-width: 8px; " 
                                    + "-fx-border-radius: 8px; "
                                    + "-fx-font-size: 36px");
                        inputRoomId.setOnKeyPressed(event -> {
                            if (event.getCode() == KeyCode.ENTER) {
                                roomId = Integer.parseInt(inputRoomId.getText());
                                if (connection.joinRoom(roomId) != -1) {
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
                    
                                    clientThread.start();
                                } else {
                                    Label infoLabel = new Label("Cannot enter the room!");
                                    infoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));
                                    infoLabel.setTextFill(Color.WHITE);
                                    vbox.getChildren().clear();
                                    vbox.getChildren().addAll(title, infoLabel, b4);
                                    vbox.setSpacing(100);

                                    b4.setOnAction(new EventHandler<ActionEvent>() {
                                        @Override
                                        public void handle(ActionEvent e) {
                                            vbox.getChildren().clear();
                                            b1.setVisible(true);
                                            b2.setVisible(true);
                                            vbox.getChildren().addAll(title, b1, b2);
                                            vbox.setSpacing(40);
                                        }
                                    });
                                }
                                
                            }
                        });
                        vbox.getChildren().clear();
                        vbox.getChildren().addAll(title, inputRoomId);
                        vbox.setSpacing(136);
                    }
                });
        
                b3.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        connection.send("START_GAME");
                    }
                });
            }
        });
        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, input);
        vbox.setSpacing(136);

        return vbox;
    }

    public void startGame() {
        if (animationTimer != null) {
            animationTimer.stop();
            stage.setScene(this.gameScene);
            gameTimer.start(); // this internally calls the handle() method of our GameTimer
        }
    }

    @SuppressWarnings("exports")
    public GameTimer getGameTimer() {
        return this.gameTimer;
    }

    public ClientConnection getConnection() {
        return this.connection;
    }

    public ChatApp getChat() {
        return this.chat;
    }

    public String getUsername() {
        return this.username;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getNumPlayers() {
        return this.numPlayers;
    }
}
