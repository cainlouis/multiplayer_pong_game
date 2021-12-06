package com.almasb.fxglgames.pong;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

/**
 * MainMenu represents the menu when the user launches the application
 * @author Almas Baimagambetov, Nael Louis
 */
public class MainMenu extends FXGLMenu {

    public MainMenu() {
        super(MenuType.MAIN_MENU);

        //Create text for the name of the game
        Text text = getUIFactoryService().newText("Pong Game", Color.BLACK, 60.0);
        text.setTranslateX(FXGL.getAppWidth() / 2 - 250 / 2);
        text.setTranslateY(FXGL.getAppHeight() / 2 - 300 / 2);

        //Create the button for the main menu and their action
        PongMenuButton newGame = new PongMenuButton("New Game", () -> fireNewGame()); //Create a new instance of the game
        PongMenuButton exit = new PongMenuButton("Quit", () -> fireExit()); //Exit the application

        //Create a vbox that acts like a container for the buttons and set the position to the lower left corner
        VBox container = new VBox(newGame, exit);
        container.setTranslateX(100);
        container.setTranslateY(450);

        //Add the container and the text for the game to the scene
        getContentRoot().getChildren().addAll(text, container);
    }
}