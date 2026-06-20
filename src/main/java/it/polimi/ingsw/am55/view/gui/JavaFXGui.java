package it.polimi.ingsw.am55.view.gui;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.network.ClientImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Entry point JavaFX della GUI.
 *
 * Importante: questa classe NON sceglie RMI/Socket e NON crea il client di rete.
 * Il Client.main crea già ClientModel, ClientCommands e ClientController; poi passa
 * model + actionHandler + client alla GUI tramite launchGui(...).
 */
public class JavaFXGui extends Application {

    private static ClientModel bootstrapModel;
    private static UserActionHandler bootstrapActionHandler;
    private static ClientImpl bootstrapClient;

    /**
     * Avvia JavaFX usando oggetti già creati dal Client.main.
     */
    public static void launchGui(
            ClientModel model,
            UserActionHandler actionHandler,
            ClientImpl client
    ) {
        bootstrapModel = model;
        bootstrapActionHandler = actionHandler;
        bootstrapClient = client;

        Application.launch(JavaFXGui.class);
    }

    @Override
    public void start(Stage stage) {
        if (bootstrapModel == null || bootstrapActionHandler == null || bootstrapClient == null) {
            throw new IllegalStateException(
                    "JavaFXGui deve essere avviata con launchGui(model, actionHandler, client)."
            );
        }

        GuiView guiView = new GuiView(bootstrapModel);
        guiView.setActionHandler(bootstrapActionHandler);

        /*
         * Fondamentale:
         * la GUI deve diventare observer PRIMA della connect(),
         * perché connect() manda RegisterLobbyCommand e il server risponde
         * con LobbyStatusMessage.
         */
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

        /*
         * La connect parte solo ora, quando la GuiView è già observer.
         * Uso un thread separato per non bloccare il thread JavaFX.
         */
        Thread connectThread = new Thread(() -> {
            try {
                bootstrapClient.connect();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.err.println("[GUI] Errore durante la connessione: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        });

        connectThread.setName("GUI-Connect-Thread");
        connectThread.setDaemon(true);
        connectThread.start();
    }
}