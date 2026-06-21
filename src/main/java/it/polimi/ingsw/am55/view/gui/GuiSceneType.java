package it.polimi.ingsw.am55.view.gui;

/** Identifica le scene FXML gestite dalla GUI. */
public enum GuiSceneType {
    START("/it/polimi/ingsw/am55/fxml/StartScene.fxml"),
    LOBBY("/it/polimi/ingsw/am55/fxml/LobbyScene.fxml"),
    GAME("/it/polimi/ingsw/am55/fxml/GameScene.fxml"),
    QUIT_GAME("/it/polimi/ingsw/am55/fxml/QuitGameScene.fxml"),
    END_GAME("/it/polimi/ingsw/am55/fxml/EndGameScene.fxml");

    private final String fxmlPath;

    GuiSceneType(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }
}
