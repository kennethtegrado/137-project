package MainGameStage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ChatApp extends Application {
    private String username;
    private TextArea messages = new TextArea();
    private TextField input = new TextField();
    private ClientConnection connection;
    
    public ChatApp(ClientConnection connection, String username) {
        this.connection = connection;
        this.username = username;
    }

    public void setUsername(String newName) {
        this.username = newName;
    }

    public void appendMessage(String message) {
        messages.appendText(message + "\n");
    }

    public VBox createContent() {
        messages.setFont(Font.font(14));
        messages.setPrefHeight(350);
        messages.setWrapText(true);
        messages.setStyle("-fx-control-inner-background: #000000; -fx-border-style: none;");
        messages.setEditable(false);

        input.setPromptText("Press \'/\' to open the chat");
        input.setStyle("-fx-control-inner-background: #343434; -fx-prompt-text-fill: #aeaeae");
        input.setOnAction(event -> {
            String message = "SEND_CHAT " + username + ":";
            message += input.getText();
            input.clear();
            connection.send(message);
        });

        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Request focus on another node to unfocus the TextField
                input.getParent().requestFocus();
            }
        });

        messages.setFocusTraversable(false);
        input.setFocusTraversable(false);

        VBox root = new VBox(5, messages, input);
        // Set minimum width and height
        root.setMinWidth(100);
        root.setMinHeight(75);

        // Set maximum width and height
        root.setMaxWidth(382);
        root.setMaxHeight(440);
        return root;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }
}
