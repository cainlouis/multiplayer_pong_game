package com.almasb.fxglgames.pong;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MainMenu extends FXGLMenu {

    public MainMenu() {
        super(MenuType.MAIN_MENU);

        Text text = getUIFactoryService().newText("Pong Game", Color.BLACK, 60.0);
        text.setTranslateX(FXGL.getAppWidth() / 2 - 250 / 2);
        text.setTranslateY(FXGL.getAppHeight() / 2 - 300 / 2);

        PongMenuButton newGame = new PongMenuButton("New Game", () -> fireNewGame());
        PongMenuButton exit = new PongMenuButton("Quit", () -> fireExit());

        VBox container = new VBox(newGame, exit);
        container.setTranslateX(100);
        container.setTranslateY(450);

        getContentRoot().getChildren().addAll(text, container);
    }

}