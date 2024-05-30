package MainGameStage;

import java.util.ArrayList;

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
                // get the remaining usernames
                ArrayList<String> usernames = new ArrayList<>();
                for (int i = 0; i < result.length - 1; i++) {
                    usernames.add(result[1+i]);
                    // System.out.println(result[i+1]);
                }
                Platform.runLater(() -> game.getGameTimer().setPlayers(usernames));
                Platform.runLater(() -> game.startGame());
            } else if (code.equals("SEND_CHAT")) {
                Platform.runLater(() -> game.getChat().appendMessage(result[1] + " "+  result[2]));
            } else if (code.equals("JOINED_ROOM")) {
                Platform.runLater(() -> game.setNumPlayers(Integer.parseInt(result[1])));
            } else if (code.equals("KEY_PRESS")) {
                Platform.runLater(() -> game.getGameTimer().handleKeyPress(result[1], result[2], Double.parseDouble(result[3]), Double.parseDouble(result[4])));
            } else if (code.equals("KEY_RELEASE")) {
                Platform.runLater(() -> game.getGameTimer().handleKeyRelease(result[1], result[2], Double.parseDouble(result[3]), Double.parseDouble(result[4])));
            }
        }
    }
}


