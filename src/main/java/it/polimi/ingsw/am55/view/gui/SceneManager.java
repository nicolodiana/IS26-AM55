package it.polimi.ingsw.am55.view.gui;

import it.polimi.ingsw.am55.view.gui.scene.GenericSceneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Gestore centralizzato delle scene JavaFX.
 * Lo Stage resta lo stesso; quando serve una nuova schermata viene sostituita
 * la root della Scene corrente caricando un nuovo FXML.
 */
public final class SceneManager {

    private static Stage mainStage;
    private static Scene activeScene;
    private static GenericSceneController activeController;
    private static String currentFxml;
    private static GuiView guiView;

    private SceneManager() {
    }

    public static void init(Stage stage, GuiView view) {
        mainStage = Objects.requireNonNull(stage, "stage");
        guiView = Objects.requireNonNull(view, "view");
    }

    public static GenericSceneController getActiveController() {
        return activeController;
    }

    public static String getCurrentFxml() {
        return currentFxml;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void showStartScene() {
        changeRoot("/it/polimi/ingsw/am55/fxml/StartScene.fxml");
    }

    public static void showLobbyScene() {
        changeRoot("/it/polimi/ingsw/am55/fxml/LobbyScene.fxml");
    }

    public static void showGameScene() {
        changeRoot("/it/polimi/ingsw/am55/fxml/GameScene.fxml");
    }

    public static void showGameSceneIfNeeded() {
        if (!"/it/polimi/ingsw/am55/fxml/GameScene.fxml".equals(currentFxml)) {
            showGameScene();
        }
    }

    public static void showLobbySceneIfNeeded() {
        if (!"/it/polimi/ingsw/am55/fxml/LobbyScene.fxml".equals(currentFxml)) {
            showLobbyScene();
        }
    }
    public static void showQuitGameScene() {
        changeRoot("/it/polimi/ingsw/am55/fxml/QuitGameScene.fxml");
    }

    public static void showEndGameScene() {
        changeRoot("/it/polimi/ingsw/am55/fxml/EndGameScene.fxml");
    }

    private static void changeRoot(String fxmlPath) {
        try {
            URL url = SceneManager.class.getResource(fxmlPath);
            if (url == null) {
                throw new IllegalStateException("FXML non trovato: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (!(controller instanceof GenericSceneController sceneController)) {
                throw new IllegalStateException(
                        "Il controller di " + fxmlPath + " deve implementare GenericSceneController"
                );
            }

            sceneController.setGuiView(guiView);
            activeController = sceneController;
            currentFxml = fxmlPath;

            if (activeScene == null) {
                activeScene = new Scene(root);
                mainStage.setScene(activeScene);
                mainStage.show();
            } else {
                activeScene.setRoot(root);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Errore caricando FXML: " + fxmlPath, e);
        }
    }
}
