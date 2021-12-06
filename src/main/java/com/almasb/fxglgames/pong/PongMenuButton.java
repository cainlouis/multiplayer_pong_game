package com.almasb.fxglgames.pong;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

/**
 * PongMenuButton specifies the style of a button on the menu
 * @author Nael Louis
 */
public class PongMenuButton extends StackPane {
    private String name;
    private Runnable action;
    private Text text;
    private Circle shape;

    public PongMenuButton(String name, Runnable action) {
        this.name = name;
        this.action = action;

        text = getUIFactoryService().newText(name, Color.LIGHTBLUE, 20.0);
        shape = new Circle(4, Color.LIGHTBLUE);
        shape.setTranslateX(-20);
        shape.setTranslateY(-2);
        shape.visibleProperty().bind(focusedProperty());

        text.fillProperty().bind(Bindings.when(focusedProperty()).then(Color.LIGHTBLUE).otherwise(Color.GRAY));
        setAlignment(Pos.CENTER_LEFT);
        setFocusTraversable(true);

        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                action.run();
            }
        });
        getChildren().addAll(shape, text);
    }
}
