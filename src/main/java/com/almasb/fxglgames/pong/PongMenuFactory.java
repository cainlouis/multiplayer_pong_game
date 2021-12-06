package com.almasb.fxglgames.pong;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;

/**
 * PongMenuFactory creates the objects of MainMenu and GameMenu
 * @author Nael Louis
 */
public class PongMenuFactory extends SceneFactory {
    @Override
    public FXGLMenu newMainMenu() {
        return new MainMenu();
    }
    
    @Override
    public FXGLMenu newGameMenu() {
        return new GameMenu();
    }
}
