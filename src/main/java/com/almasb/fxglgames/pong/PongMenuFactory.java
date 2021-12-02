package com.almasb.fxglgames.pong;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import static com.almasb.fxgl.dsl.FXGL.getExecutor;
import static com.almasb.fxgl.dsl.FXGL.getGameController;

public class PongMenuFactory extends SceneFactory {
    @Override
    public FXGLMenu newMainMenu() {
        return new MainMenu();
    }
    @Override
    public FXGLMenu newGameMenu() {
        getExecutor().startAsyncFX(() -> getGameController().pauseEngine());
        return new GameMenu();
    }
}
