package com.almasb.fxglgames.pong;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import kotlin.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Optional;

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

        PongMenuButton newGame = new PongMenuButton("New Game", () -> {
            try {
                loginDialog();
            } catch (NoSuchAlgorithmException | KeyStoreException e) {
                e.printStackTrace();
            }
        });
        PongMenuButton exit = new PongMenuButton("Quit", () -> fireExit());

        VBox container = new VBox(newGame, exit);
        container.setTranslateX(100);
        container.setTranslateY(450);

        getContentRoot().getChildren().addAll(text, container);
    }

    protected void loginDialog() throws NoSuchAlgorithmException, KeyStoreException {
        // Create the custom dialog.
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Login Dialog");
        dialog.setHeaderText("Password input");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Create the password label and field.
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Password:"), 0, 0);
        grid.add(password, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> password.requestFocus());

        Optional<String> result = dialog.showAndWait();

        // reading the password from the user
        String pass = password.getText();
        System.out.println(pass);

        byte [] hash = HashingSHA3.computeHash(pass);
        System.out.println(HashingSHA3.bytesToHex(hash));

        KeyStoring ks = new KeyStoring(HashingSHA3.bytesToHex(hash));

        //checks if the p12 file exists, if it does not, it creates a new one or tries to load the key
        if (Files.notExists(Paths.get("src", "main", "resources", "keystore", "keystore.p12"))){
            System.out.println("keys created");
            ks.createStoredKeys();
            fireNewGame();
        }
        try {
            ks.LoadKey(HashingSHA3.bytesToHex(hash));
            System.out.println("keys already created, password correct");
            fireNewGame();
        } catch (CertificateException e) {
            System.out.println("Certificate");
        } catch (IOException e) {
            System.out.println("Wrong Password");
        }

        //Uncomment if password is right
        //fireNewGame();
    }

}