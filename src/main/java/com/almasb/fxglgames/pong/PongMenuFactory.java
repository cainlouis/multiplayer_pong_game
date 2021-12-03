package com.almasb.fxglgames.pong;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;

public class PongMenuFactory extends SceneFactory {
    @Override
    public FXGLMenu newMainMenu() {
        return new MainMenu();
    }
    @Override
    public FXGLMenu newGameMenu() {
        //return new SimpleGameMenu();
        return new GameMenu();
    }
}
