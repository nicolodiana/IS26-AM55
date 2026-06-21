package it.polimi.ingsw.am55.view.gui;

/**
 * Identifies every FXML scene managed by the JavaFX client.
 */
public enum GuiSceneType {
    START("/it/polimi/ingsw/am55/fxml/StartScene.fxml"),
    LOBBY("/it/polimi/ingsw/am55/fxml/LobbyScene.fxml"),
    GAME("/it/polimi/ingsw/am55/fxml/GameScene.fxml"),
    QUIT_GAME("/it/polimi/ingsw/am55/fxml/QuitGameScene.fxml"),
    END_GAME("/it/polimi/ingsw/am55/fxml/EndGameScene.fxml");

    private final String fxmlPath;

    /**
     * Stores the classpath resource used to load the scene.
     */
    GuiSceneType(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    /**
     * Returns the classpath path of the FXML resource.
     *
     * @return FXML resource path
     */
    public String getFxmlPath() {
        return fxmlPath;
    }
}
