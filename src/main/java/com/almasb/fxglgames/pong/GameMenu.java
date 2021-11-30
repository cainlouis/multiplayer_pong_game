package com.almasb.fxglgames.pong;


import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.scene.layout.VBox;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GameMenu extends FXGLMenu {

    public GameMenu() {
        super(MenuType.GAME_MENU);

        PongMenuButton resume = new PongMenuButton("Resume", () -> fireResume());
        PongMenuButton save = new PongMenuButton("Save", () -> fireSave());
        PongMenuButton load = new PongMenuButton("Load", () -> PongApp.loadLastGame());
        PongMenuButton exit = new PongMenuButton("Exit Game", () -> fireExit());

        VBox container = new VBox(resume, save, load, exit);
        container.setTranslateX(100);
        container.setTranslateY(450);

        getContentRoot().getChildren().add(container);
    }

}
