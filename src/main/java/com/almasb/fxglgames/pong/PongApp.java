/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.profile.DataFile;
import com.almasb.fxgl.profile.SaveLoadHandler;
import com.almasb.fxgl.ui.UI;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

/**
 * A simple clone of Pong.
 * Sounds from https://freesound.org/people/NoiseCollector/sounds/4391/ under CC BY 3.0.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PongApp extends GameApplication {
    private final int TCP_SERVER_PORT = 7777;
    boolean isServer;
    boolean isPaused;
    static boolean isHost;
    boolean isClient;
    private Connection<Bundle> connection;
    boolean pass;
    private static KeyStoring ks;
    private static byte[] hash;
    private final static String SAVEDFILENAME = "savedFile.sav";
    private final static Path PONGFILE = Paths.get("src", "main", "java", "com", "almasb", "fxglgames", "pong", "PongApp.java");
    private final static Path PONGSIGNATUREFILE = Paths.get("src", "main", "resources", "PongApp.sig");
    private final static Path ENCRYPTEDFILEPATH = Paths.get("src","main","resources", "savedFiles","encryptedFile");
    private BatComponent playerBat1, playerBat2;
    private Input clientInput;
    private String ipAddress;
    private boolean isPreviousGame;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Pong");
        settings.setVersion("1.0");
        settings.setFontUI("pong.ttf");
        
        //Required for multiplayer service
        settings.addEngineService(MultiplayerService.class); //Required for multiplayer service
        //Required to show main menu
        settings.setMainMenuEnabled(true);
        //Get the menus
        settings.setSceneFactory(new PongMenuFactory());
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        //Player scores
        vars.put("player1score", 0);
        vars.put("player2score", 0);
    }

    @Override
    protected void onPreInit() {
        getSaveLoadService().addHandler(new SaveLoadHandler() {
            @Override
            public void onSave(DataFile data) {
                // create a new bundle to store your data
                var bundle = new Bundle("gameData");
                
                // store some data
                IntegerProperty player1score = getip("player1score");
                bundle.put("player1score", player1score.get());
                IntegerProperty player2score = getip("player2score");
                bundle.put("player2score", player2score.get());

                // give the bundle to data file
                data.putBundle(bundle);
            }

            @Override
            public void onLoad(DataFile data) {
                // get your previously saved bundle
                var bundle = data.getBundle("gameData");

                // retrieve some data
                int player1score = bundle.get("player1score");
                int player2score = bundle.get("player2score");

                // update your game with saved data
                set("player1score", player1score);
                set("player2score", player2score);
            }
        });
    }

    public static void loadLastGame() throws Exception {
        /*
         * decrypt file and generate a savedFile.sav
         */
        File encryptedFile= new File(String.valueOf(ENCRYPTEDFILEPATH));
        File savedFile =  new File(SAVEDFILENAME);
        Encrypt.decryptFile(ks.GetSecretKey(HashingSHA3.bytesToHex(hash)), encryptedFile, savedFile);
        getSaveLoadService().readAndLoadTask(SAVEDFILENAME).run();
        savedFile.delete();

        getDialogService().showMessageBox("Game loaded!");
    }

    public static void saveLastGame() throws Exception {
        getSaveLoadService().saveAndWriteTask(SAVEDFILENAME).run();
        /*
         * generates savedFileName, encrypts it and delete the saved file.
         */
        File savedFile =  new File(SAVEDFILENAME);
        File encryptedFile= new File(String.valueOf(ENCRYPTEDFILEPATH));
        Encrypt.encryptFile(ks.GetSecretKey(HashingSHA3.bytesToHex(hash)),savedFile, encryptedFile);
        savedFile.delete();

        getDialogService().showMessageBox("Game saved!");
    }

    @Override
    protected void initGame() {
        runOnce(() -> {
            getDialogService().showConfirmationBox("Are you the host?", yes -> {
                isServer = yes;

                getWorldProperties().<Integer>addListener("player1score", (old, newScore) -> {
                    if (newScore == 11) {
                        showGameOver("Player 1");
                    }
                });

                getWorldProperties().<Integer>addListener("player2score", (old, newScore) -> {
                    if (newScore == 11) {
                        showGameOver("Player 2");
                    }
                });
                 
                //this line is needed in order for entities to be spawned
                getGameScene().setBackgroundColor(Color.rgb(0, 0, 5));
                //Add background color to the game window.
                getGameWorld().addEntityFactory(new PongFactory());

                initScreenBounds();


                if (isServer) {
                    //Ask for the password until they get the right password
                    do {
                        try {
                            //call method that display the password field return a boolean on if the password entered is right
                            pass = loginDialog();
                        } catch (NoSuchAlgorithmException | KeyStoreException e) {
                            e.printStackTrace();
                        }
                    } while (!pass);
                    try {
                        // Checks if server port is already occupied by another host
                        if (!hostServerPortAvailabilityCheck(TCP_SERVER_PORT)) {
                            throw new RuntimeException();
                        }

                        try {
                            if (Files.exists(PONGSIGNATUREFILE)) {
                                SigningFile.verifySignature(ks.GetPublicKey(HashingSHA3.bytesToHex(hash)), PONGFILE);
                            }
                        } catch (Exception e) {
                        }
                        //Setup the TCP port that the server will listen at.
                        var server = getNetService().newTCPServer(TCP_SERVER_PORT);

                        server.setOnConnected(conn -> {
                            connection = conn;
                            setConnectionListener();
                            
                            //Setup the entities and other necessary items on the server                           
                            if (!isHost) {
                                getExecutor().startAsyncFX(() -> onServer());
                                isHost = true;
                            }
                        });
                        
                        //Start listening on the specified TCP port.
                        server.startAsync();
                        //Ask the host if they want to load previous game before starting a new game
                        getDialogService().showConfirmationBox("Do you want load the previous game?", answer -> {
                            isPreviousGame = answer;
                            //If they want to load and the previous game exist
                            if (isPreviousGame && Files.exists(ENCRYPTEDFILEPATH)) {
                                //load last game
                                try {
                                    loadLastGame();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            //If the previous game was not saved show a dialog
                            else if (!Files.exists(ENCRYPTEDFILEPATH)) {
                                getDialogService().showMessageBox("There is no previous game! Starting a new game");
                            }
                        });
                    } catch (RuntimeException | IOException e) {
                        getDialogService().showErrorBox("Can't create new host! Server is already hosting.", () -> {
                            connectClient();
                        });
                    }
                } else {
                    connectClient();
                }
            });
        }, Duration.seconds(0.5));
    }

    private void connectClient() {
        getDialogService().showInputBox("Enter Server IP Address to connect as client", answer -> {
            try {
                ipAddress = InputValidation.validateIP(answer);
                InetAddress address = InetAddress.getByName(ipAddress);
                boolean reachable = address.isReachable(2000);

                // Checks if can connect to IP Address and checks if the port is free
                if (!reachable || !hostPortAvailabilityCheck(ipAddress, TCP_SERVER_PORT)) {
                    throw new RuntimeException("Server IP Address not found. Try Again!");
                }

                //Setup the connection to the server.
                var client = getNetService().newTCPClient(ipAddress, TCP_SERVER_PORT);
                client.setOnConnected(conn -> {
                    connection = conn;
                    setConnectionListener();
                    
                    //Enable the client to receive data from the server.              
                    if (!isClient) {
                        getExecutor().startAsyncFX(() -> onClient());
                        isClient = true;
                    }
                });

                //Establish the connection to the server.
                client.connectAsync();
            } catch (IllegalArgumentException e) {
                getDialogService().showErrorBox("Black listed character have been found. Invalid IP address.", () -> {
                    connectClient();
                });
            } catch (RuntimeException e) {
                getDialogService().showErrorBox("Server IP Address is not currently hosting. Try Again!", () -> {
                    connectClient();
                });
            } catch (IOException ex) {
                getDialogService().showErrorBox("Unreacheable Host/Invalid Input. Try Again!", () -> {
                    connectClient();
                });
            } catch (Exception e) {
                getDialogService().showErrorBox("An error has occurred. Try Again!", () -> {
                    e.printStackTrace();
                    connectClient(); 
                });
            }
        });
    }

    /**
     * setConnectionListner() sets up a listener to receive any message sent by either server or client. 
     * Pauses or resumes game accordingly.
     */
    private void setConnectionListener() {
        // Sets up a listener to receive any message sent by either server or client. Pauses or resumes game accordingly
        connection.addMessageHandler((connect, message) -> {
            if (message.exists("isPaused")) {
                if (message.get("isPaused")) {
                    getExecutor().startAsyncFX(() -> getGameController().pauseEngine());
                } else {
                    getExecutor().startAsyncFX(() -> getGameController().resumeEngine());
                }
            }
        });
    }
    
    /**
     * hostPortAvailabilityCheck() checks if port on selected ipAddress is available 
     * @param ipAddress
     * @param port
     * @return
     * @throws IOException
     */
    public boolean hostPortAvailabilityCheck(String ipAddress, int port) throws IOException {
        try (Socket socket = new Socket(ipAddress, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * hostServerPortAvailabilityCheck() checks if server's port is available
     *
     * @param port
     * @return
     * @throws IOException
     */
    public boolean hostServerPortAvailabilityCheck(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    //Code to setup entities on other necessities on the server.
    private void onServer() {
        initServerInput();
        initServerPhysics();
      
        //Spawn needed entities for the server
        Entity ball = spawn("ball", new SpawnData(getAppWidth() / 2 - 5, getAppHeight() / 2 - 5).put("exists", true));
        getService(MultiplayerService.class).spawn(connection, ball, "ball");
        Entity bat1 = spawn("bat", new SpawnData(getAppWidth() / 4, getAppHeight() / 2 - 30).put("exists", true));
        getService(MultiplayerService.class).spawn(connection, bat1, "bat");
        Entity bat2 = spawn("bat", new SpawnData(3 * getAppWidth() / 4 - 20, getAppHeight() / 2 - 30).put("exists", true));
        getService(MultiplayerService.class).spawn(connection, bat2, "bat");
        
        //Set entities as components 
        playerBat1 = bat1.getComponent(BatComponent.class);
        playerBat2 = bat2.getComponent(BatComponent.class);

        getService(MultiplayerService.class).addInputReplicationReceiver(connection, clientInput);
        getService(MultiplayerService.class).addPropertyReplicationSender(connection, getWorldProperties());
    }

    //Code to setup the client
    private void onClient() {
        
        //onClient handles ESC button and signals server game is paused (onKeyBuilder doesn't work so use onKeyDown instead)
        onKeyDown(KeyCode.ESCAPE, "Pause Game", () -> {
            isPaused = true;
            var bundle = new Bundle("isPaused");
            bundle.put("isPaused", true);
            connection.send(bundle);
        });
        
        getService(MultiplayerService.class).addEntityReplicationReceiver(connection, getGameWorld());
        getService(MultiplayerService.class).addPropertyReplicationReceiver(connection, getWorldProperties());
        getService(MultiplayerService.class).addInputReplicationSender(connection, getInput());
    }
    
    protected void initServerInput() {
        //Input for server side
        getInput().addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
                playerBat1.up();
            }

            @Override
            protected void onActionEnd() {
                playerBat1.stop();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Down") {
            @Override
            protected void onAction() {
                playerBat1.down();
            }

            @Override
            protected void onActionEnd() {
                playerBat1.stop();
            }
        }, KeyCode.S);

        onKeyDown(KeyCode.ESCAPE, "Pause Game", () -> {
            isPaused = true;
            var bundle = new Bundle("isPaused");
            bundle.put("isPaused", true);
            connection.send(bundle);
        });

        //Used for setting up input on the client
        clientInput = new Input();

        onKeyBuilder(clientInput, KeyCode.W)
                .onAction(() -> {
                    playerBat2.up();
                }).onActionEnd(() -> {
            playerBat2.stop();
        });

        onKeyBuilder(clientInput, KeyCode.S)
                .onAction(() -> {
                    playerBat2.down();
                }).onActionEnd(() -> {
            playerBat2.stop();
        });
    }
        
    protected void initServerPhysics() {
        getPhysicsWorld().setGravity(0, 0);

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BALL, EntityType.WALL) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                if (boxB.getName().equals("LEFT")) {
                    inc("player2score", +1);
                } else if (boxB.getName().equals("RIGHT")) {
                    inc("player1score", +1);
                }

                play("hit_wall.wav");
                getGameScene().getViewport().shakeTranslational(5);
            }
        });

        CollisionHandler ballBatHandler = new CollisionHandler(EntityType.BALL, EntityType.PLAYER_BAT) {
            @Override
            protected void onCollisionBegin(Entity a, Entity bat) {
                play("hit_bat.wav");
                playHitAnimation(bat);
            }
        };

        getPhysicsWorld().addCollisionHandler(ballBatHandler);
    }

    @Override
    protected void initUI() {
        MainUIController controller = new MainUIController();
        UI ui = getAssetLoader().loadUI("main.fxml", controller);

        controller.getLabelScorePlayer().textProperty().bind(getip("player1score").asString());
        controller.getLabelScoreEnemy().textProperty().bind(getip("player2score").asString());

        getGameScene().addUI(ui);
    }

    private void initScreenBounds() {
        Entity walls = entityBuilder()
                .type(EntityType.WALL)
                .collidable()
                .buildScreenBounds(150);

        getGameWorld().addEntity(walls);
    }

    private void playHitAnimation(Entity bat) {
        animationBuilder()
                .autoReverse(true)
                .duration(Duration.seconds(0.5))
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .rotate(bat)
                .from(FXGLMath.random(-25, 25))
                .to(0)
                .buildAndPlay();
    }

    private void showGameOver(String winner) {
        getDialogService().showMessageBox(winner + " won! Demo over\nThanks for playing", getGameController()::exit);
    }

    //This method is needed in order to move the client player.
    @Override
    protected void onUpdate(double tpf) {
        
        // Checks if game has been paused. If the game is paused, send connection message
        if (isPaused) {
            var bundle = new Bundle("isPaused");
            isPaused = false;
            bundle.put("isPaused", false);
            connection.send(bundle);
        }
        
        //Updates client inputs to match on server screen
        if (isServer && clientInput != null) {
            clientInput.update(tpf);
        }
    }

    protected boolean loginDialog() throws NoSuchAlgorithmException, KeyStoreException {
        boolean isPassword = false;
        // Create the custom dialog.
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Login Dialog");
        dialog.setHeaderText("Enter the password to log the keys.\nIf you do not have the keys, provide a new password to create them.");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

        //Creat the grid for the password
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

        //display the dialog box and store the answer
        Optional<String> result = dialog.showAndWait();

        // reading the password from the user
        String pass = password.getText();

        //hashing the password
        hash = HashingSHA3.computeHash(pass);
        System.out.println(HashingSHA3.bytesToHex(hash));

        // creating a keystore object with the hash provided.
        ks = new KeyStoring(HashingSHA3.bytesToHex(hash));

        //checks if the p12 file exists, if it does not, it creates a new one or tries to load the key
        if (Files.notExists(Paths.get("src", "main", "resources", "keystore", "keystore.p12"))) {
            System.out.println("keys created");
            isPassword = true;
            ks.createStoredKeys();
        }
        try {
            ks.LoadKey(HashingSHA3.bytesToHex(hash));
            isPassword = true;
            System.out.println("keys already created, password correct");
        } catch (CertificateException e) {
            System.out.println("Certificate");
        } catch (IOException e) {
            System.out.println("Wrong Password");
        }
        return isPassword;
    }

    /**
     * This is a helper method to sign the PongApp file when the user exits the game
     * it uses the class SigningFile*/
    public static void signFile() throws Exception {
        if(isHost) {
            SigningFile.generateSignature(ks.GetPrivateKey(HashingSHA3.bytesToHex(hash)), PONGFILE);
        }
    }

    /**
     * Main method
     * @param args 
     */
    public static void main(String[] args) {
        launch(args);
    }
}
