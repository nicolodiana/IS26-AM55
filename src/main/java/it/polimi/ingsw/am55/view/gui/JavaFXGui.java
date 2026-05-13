package it.polimi.ingsw.am55.view.gui;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Entry point JavaFX della GUI.
 *
 * Importante: questa classe NON sceglie RMI/Socket e NON crea il client di rete.
 * Il Client.main crea già ClientModel, ClientCommands e ClientController; poi passa
 * model + actionHandler alla GUI tramite launchGui(...).
 */
public class JavaFXGui extends Application {

    private static ClientModel bootstrapModel;
    private static UserActionHandler bootstrapActionHandler;

    /**
     * Avvia JavaFX usando oggetti già creati dal Client.main.
     */
    public static void launchGui(ClientModel model, UserActionHandler actionHandler) {
        bootstrapModel = model;
        bootstrapActionHandler = actionHandler;
        Application.launch(JavaFXGui.class);
    }

    @Override
    public void start(Stage stage) {
        if (bootstrapModel == null || bootstrapActionHandler == null) {
            throw new IllegalStateException(
                    "JavaFXGui deve essere avviata con launchGui(model, actionHandler)."
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
    }
}
