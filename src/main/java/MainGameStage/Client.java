package MainGameStage;

import javafx.application.Platform;

public class Client implements Runnable {
    private Game game;
    private volatile boolean running = true;

    public Client(Game game) {
        this.game = game;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            String message = game.getConnection().receive();
            String[] result = message.split(" ");
            String code = result[0];
            System.out.println(message);
            if (code.equals("START_GAME")) {  // Use equals for string comparison
                Platform.runLater(() -> game.startGame());
            } else if (code.equals("SEND_CHAT")) {
                Platform.runLater(() -> game.getChat().appendMessage(result[1]));
            }
        }
    }
}


