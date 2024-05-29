/***********************************************************
* This App class extends the Application superclass.
* This is where we override the start method of the parent
* class to begin the game.
*
* @author Quim Ramos
* @created_date 2024-04-25
*
***********************************************************/

package Main;

import javafx.application.Application;
import javafx.stage.Stage;

import MainGameStage.Game;

public class App extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage)
    {
       Game game = new Game();
       game.setStage(stage);
    }

}
