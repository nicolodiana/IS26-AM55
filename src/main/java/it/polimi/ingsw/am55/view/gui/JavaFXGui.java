package it.polimi.ingsw.am55.view.gui;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * JavaFX application entry point for the GUI client.
 * <p>
 * This class is responsible only for creating the JavaFX view, registering it
 * as an observer of the client model, initializing the scene manager and showing
 * the first scene. The network connection is started by the bootstrap code
 * through a callback executed after the GUI is ready.
 */
public class JavaFXGui extends Application {

    private static ClientModel bootstrapModel;
    private static UserActionHandler bootstrapActionHandler;
    private static Runnable onGuiReady;

    /**
     * Launches the JavaFX GUI.
     * <p>
     * {@link Application#launch(Class, String...)} is blocking, so the caller
     * should not expect this method to return until the JavaFX application is closed.
     * The {@code readyCallback} is executed after the GUI has registered its observer
     * and displayed the initial scene.
     *
     * @param model observed client model.
     * @param actionHandler command handler used by the GUI.
     * @param readyCallback callback executed when the GUI is ready to receive updates.
     */
    public static void launchGui(
            ClientModel model,
            UserActionHandler actionHandler,
            Runnable readyCallback
    ) {
        bootstrapModel = model;
        bootstrapActionHandler = actionHandler;
        onGuiReady = readyCallback;
        Application.launch(JavaFXGui.class);
    }

    /**
     * Initializes the JavaFX stage and signals that the GUI can safely receive
     * server-driven model updates.
     *
     * @param stage primary JavaFX window.
     */
    @Override
    public void start(Stage stage) {
        if (bootstrapModel == null || bootstrapActionHandler == null) {
            throw new IllegalStateException(
                    "JavaFXGui must be launched with a model and an action handler."
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

        if (onGuiReady != null) {
            new Thread(onGuiReady, "GUI-Connect-Thread").start();
        }
    }
}