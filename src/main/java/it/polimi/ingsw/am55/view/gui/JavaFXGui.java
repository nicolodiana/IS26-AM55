package it.polimi.ingsw.am55.view.gui;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;

/**
 * JavaFX application entry point for the GUI client.
 * <p>
 * This class only initializes the JavaFX view and registers it as an observer
 * of the client model. The network connection is intentionally started by
 * {@code Client} after the GUI is ready, so the first server update cannot be lost.
 */
public class JavaFXGui extends Application {

    private static final CountDownLatch GUI_READY = new CountDownLatch(1);

    private static ClientModel bootstrapModel;
    private static UserActionHandler bootstrapActionHandler;

    /**
     * Launches JavaFX using the already-created client dependencies.
     * <p>
     * This method blocks until the JavaFX application is closed, so callers that
     * need to continue execution should invoke it from a dedicated thread.
     *
     * @param model observed client model
     * @param actionHandler command handler used by the GUI
     */
    public static void launchGui(ClientModel model, UserActionHandler actionHandler) {
        bootstrapModel = model;
        bootstrapActionHandler = actionHandler;
        Application.launch(JavaFXGui.class);
    }

    /**
     * Waits until the GUI has created its view, registered the observer and
     * initialized the first scene.
     *
     * @throws InterruptedException if the waiting thread is interrupted
     */
    public static void awaitGuiReady() throws InterruptedException {
        GUI_READY.await();
    }

    /**
     * Initializes the stage and registers the GUI observer before allowing the
     * network connection to start.
     *
     * @param stage primary JavaFX stage
     */
    @Override
    public void start(Stage stage) {
        if (bootstrapModel == null || bootstrapActionHandler == null) {
            throw new IllegalStateException(
                    "JavaFXGui must be started with launchGui(model, actionHandler)."
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

        GUI_READY.countDown();
    }
}