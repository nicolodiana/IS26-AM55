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
    private static GuiSceneType currentSceneType;
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

    public static GuiSceneType getCurrentSceneType() {
        return currentSceneType;
    }

    public static boolean isCurrentScene(GuiSceneType sceneType) {
        return currentSceneType == sceneType;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void showStartScene() {
        changeRoot(GuiSceneType.START);
    }

    public static void showLobbyScene() {
        changeRoot(GuiSceneType.LOBBY);
    }

    public static void showGameScene() {
        changeRoot(GuiSceneType.GAME);
    }

    public static void showGameSceneIfNeeded() {
        showSceneIfNeeded(GuiSceneType.GAME);
    }

    public static void showLobbySceneIfNeeded() {
        showSceneIfNeeded(GuiSceneType.LOBBY);
    }

    public static void showQuitGameScene() {
        changeRoot(GuiSceneType.QUIT_GAME);
    }

    public static void showEndGameScene() {
        changeRoot(GuiSceneType.END_GAME);
    }

    private static void showSceneIfNeeded(GuiSceneType sceneType) {
        if (currentSceneType != sceneType) {
            changeRoot(sceneType);
        }
    }

    private static void changeRoot(GuiSceneType sceneType) {
        String fxmlPath = sceneType.getFxmlPath();

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
            currentSceneType = sceneType;

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
