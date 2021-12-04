package com.almasb.fxglgames.pong;


import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.scene.layout.VBox;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GameMenu extends FXGLMenu {

    public GameMenu() {
        super(MenuType.GAME_MENU);

        PongMenuButton resume = new PongMenuButton("Resume", () -> fireResume());
        PongMenuButton save = new PongMenuButton("Save", () -> {
            try {
                PongApp.saveLastGame();
            } catch (Exception e) {
            }
        });
        PongMenuButton load = new PongMenuButton("Load", () -> {
            try {
                PongApp.loadLastGame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        PongMenuButton mainMenu = new PongMenuButton("Main Menu", () -> fireExitToMainMenu());
        PongMenuButton exit = new PongMenuButton("Exit Game", () -> fireExit());

        VBox container = new VBox(resume, save, load, mainMenu, exit);
        container.setTranslateX(100);
        container.setTranslateY(450);

        getContentRoot().getChildren().add(container);
    }


}
