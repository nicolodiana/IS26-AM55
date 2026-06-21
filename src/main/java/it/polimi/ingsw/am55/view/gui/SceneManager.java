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
 * Centralized JavaFX scene manager.
 * <p>
 * The primary {@link Stage} is created once; each navigation only replaces the
 * root node of the active {@link Scene} with the root loaded from the requested FXML.
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

    /**
     * Initializes the manager with the application stage and owner view.
     */
    public static void init(Stage stage, GuiView view) {
        mainStage = Objects.requireNonNull(stage, "stage");
        guiView = Objects.requireNonNull(view, "view");
    }

    /**
     * Returns the controller of the currently visible scene.
     */
    public static GenericSceneController getActiveController() {
        return activeController;
    }

    /**
     * Returns the FXML path of the currently visible scene.
     */
    public static String getCurrentFxml() {
        return currentFxml;
    }

    /**
     * Returns the enum value of the currently visible scene.
     */
    public static GuiSceneType getCurrentSceneType() {
        return currentSceneType;
    }

    /**
     * Checks whether a scene type is currently visible.
     */
    public static boolean isCurrentScene(GuiSceneType sceneType) {
        return currentSceneType == sceneType;
    }

    /**
     * Returns the JavaFX primary stage.
     */
    public static Stage getMainStage() {
        return mainStage;
    }

    /** Shows the splash/start scene. */
    public static void showStartScene() {
        changeRoot(GuiSceneType.START);
    }

    /** Shows the lobby scene. */
    public static void showLobbyScene() {
        changeRoot(GuiSceneType.LOBBY);
    }

    /** Shows the game scene. */
    public static void showGameScene() {
        changeRoot(GuiSceneType.GAME);
    }

    /** Shows the game scene only when it is not already visible. */
    public static void showGameSceneIfNeeded() {
        showSceneIfNeeded(GuiSceneType.GAME);
    }

    /** Shows the lobby scene only when it is not already visible. */
    public static void showLobbySceneIfNeeded() {
        showSceneIfNeeded(GuiSceneType.LOBBY);
    }

    /** Shows the terminal quit/crash scene. */
    public static void showQuitGameScene() {
        changeRoot(GuiSceneType.QUIT_GAME);
    }

    /** Shows the final results scene. */
    public static void showEndGameScene() {
        changeRoot(GuiSceneType.END_GAME);
    }

    /** Changes scene only when the requested scene is not active. */
    private static void showSceneIfNeeded(GuiSceneType sceneType) {
        if (currentSceneType != sceneType) {
            changeRoot(sceneType);
        }
    }

    /** Loads an FXML file, injects the owner view, and installs the resulting root. */
    private static void changeRoot(GuiSceneType sceneType) {
        String fxmlPath = sceneType.getFxmlPath();

        try {
            URL url = SceneManager.class.getResource(fxmlPath);
            if (url == null) {
                throw new IllegalStateException("FXML not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Object controller = loader.getController();

            if (!(controller instanceof GenericSceneController sceneController)) {
                throw new IllegalStateException(
                        "The controller for " + fxmlPath + " must implement GenericSceneController"
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
            throw new IllegalStateException("Error while loading FXML: " + fxmlPath, e);
        }
    }
}
