package it.polimi.ingsw.am55.view.gui;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.network.ClientImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * JavaFX application entry point for the GUI client.
 * <p>
 * Network objects are created by the regular client bootstrap. This class only
 * receives them through {@link #launchGui(ClientModel, UserActionHandler, ClientImpl)}
 * and wires them into the JavaFX view.
 */
public class JavaFXGui extends Application {

    private static ClientModel bootstrapModel;
    private static UserActionHandler bootstrapActionHandler;
    private static ClientImpl bootstrapClient;

    /**
     * Launches JavaFX using the already-created client dependencies.
     *
     * @param model observed client model
     * @param actionHandler command handler used by the view
     * @param client network client that connects to the server
     */
    public static void launchGui(ClientModel model, UserActionHandler actionHandler, ClientImpl client) {
        bootstrapModel = model;
        bootstrapActionHandler = actionHandler;
        bootstrapClient = client;
        Application.launch(JavaFXGui.class);
    }

    /**
     * Initializes the stage, registers the GUI observer, and starts the network connection.
     */
    @Override
    public void start(Stage stage) {
        if (bootstrapModel == null || bootstrapActionHandler == null || bootstrapClient == null) {
            throw new IllegalStateException(
                    "JavaFXGui must be started with launchGui(model, actionHandler, client)."
            );
        }

        GuiView guiView = new GuiView(bootstrapModel);
        guiView.setActionHandler(bootstrapActionHandler);
        bootstrapModel.addObserver(guiView);

        SceneManager.init(stage, guiView);
        stage.setTitle("AM55 - Mesos");
        stage.setMinWidth(1150);
        stage.setMinHeight(760);
        stage.setOnCloseRequest(event -> {
            guiView.shutdown();
            Platform.exit();
        });

        guiView.showInitialScene();
        startConnectionThread();
    }

    /**
     * Connects the network client without blocking the JavaFX application thread.
     */
    private void startConnectionThread() {
        Thread connectThread = new Thread(() -> {
            try {
                bootstrapClient.connect();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.err.println("[GUI] Connection error: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        });

        connectThread.setName("GUI-Connect-Thread");
        connectThread.setDaemon(true);
        connectThread.start();
    }
}
