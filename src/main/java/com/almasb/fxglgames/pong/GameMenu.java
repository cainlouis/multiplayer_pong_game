package com.almasb.fxglgames.pong;


import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import static com.almasb.fxgl.dsl.FXGL.getExecutor;
import static com.almasb.fxgl.dsl.FXGL.getGameController;
import javafx.scene.layout.VBox;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GameMenu extends FXGLMenu {

    public GameMenu() {
        super(MenuType.GAME_MENU);

        //Create the button for the main menu and their action
        PongMenuButton resume = new PongMenuButton("Resume", () -> fireResume()); //Allow the user to continue the current game
        PongMenuButton save = new PongMenuButton("Save", () -> {
            try {
                PongApp.saveLastGame();
            } catch (Exception e) {
            }
        }); //Allow the user to save the current game
        PongMenuButton load = new PongMenuButton("Load", () -> {
            try {
                PongApp.loadLastGame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }); //Allow the user to load the previous game
        PongMenuButton mainMenu = new PongMenuButton("Main Menu", () -> fireExitToMainMenu()); //Allow the user to return to the main menu
        PongMenuButton exit = new PongMenuButton("Exit Game", () -> fireExit()); //exit the application

        //Create a vbox that acts like a container for the buttons and set the position to the lower left corner
        VBox container = new VBox(resume, save, load, mainMenu, exit);
        container.setTranslateX(100);
        container.setTranslateY(450);

        //Add the container to the scene
        getContentRoot().getChildren().add(container);
    }


}
