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
    private ClientConnection connection = new ClientConnection(this);
    public final static int WINDOW_WIDTH = 1500;
    public final static int WINDOW_HEIGHT = 800;
    private String username = "NEXT";
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
        this.gameTimer = new GameTimer(this.stage, this.gameScene, gc, this.chat);
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
                connection.joinRoom(1);
                // TODO: HANDLE -1 roomId
                TextField input = new TextField();
                input.setPromptText("Enter your username");
                input.setPrefHeight(50); // Adjust height here
                input.setPrefWidth(50); // Adjust width here
                input.setMinWidth(50);  // Ensure minimum width is set
                input.setStyle("-fx-control-inner-background: #343434; "
                            + "-fx-prompt-text-fill: #aeaeae; "
                            + "-fx-border-color: #0000FF; " // Change border color here
                            + "-fx-border-width: 8px; " // Adjust border width if needed
                            + "-fx-border-radius: 8px; "
                            + "-fx-font-size: 40px");
                input.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        username = input.getText();
                        System.out.println("Username: " + username);
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
                    }
                });
                vbox.getChildren().clear();
                vbox.getChildren().addAll(title, input);
            }
        });

        b3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                connection.send("START_GAME");
            }
        });

        return vbox;
    }

    public void startGame() {
        if (animationTimer != null) {
            animationTimer.stop();
            stage.setScene(this.gameScene);
            System.out.println("WAS HERE");
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
}
