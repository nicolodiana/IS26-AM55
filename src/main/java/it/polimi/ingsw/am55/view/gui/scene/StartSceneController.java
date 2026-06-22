package it.polimi.ingsw.am55.view.gui.scene;

import it.polimi.ingsw.am55.view.gui.GuiView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for the splash scene shown before the lobby is synchronized.
 */
public class StartSceneController implements GenericSceneController {

    @FXML private Button startButton;
    @FXML private Label statusLabel;

    private GuiView guiView;

    /**
     * Injects the owning GUI view.
     */
    @Override
    public void setGuiView(GuiView guiView) {
        this.guiView = guiView;
    }

    /**
     * Initializes static text for the start scene.
     */
    @FXML
    private void initialize() {
        statusLabel.setText("Press START GAME to enter in lobby ");
    }

    /**
     * Handles the start button and asks the view to enter the lobby flow.
     */
    @FXML
    private void onStartClicked() {
        if (guiView == null) {
            return;
        }
        startButton.setDisable(true);
        statusLabel.setText("Synchronizing lobby with the server...");
        guiView.startGameFromInitialScene();
    }

    /**
     * Shows a status message.
     */
    public void showMessage(String message) {
        showStatus(message);
    }

    /**
     * Shows a non-error status message.
     */
    @Override
    public void showStatus(String message) {
        statusLabel.setText(message == null ? "" : message);
    }

    /**
     * Shows an error and re-enables the start button.
     */
    @Override
    public void showError(String message) {
        statusLabel.setText(message == null || message.isBlank() ? "" : "Error: " + message);
        startButton.setDisable(false);
    }

    /**
     * Locks the start button without changing the current status message.
     */
    @Override
    public void lockInteractions() {
        startButton.setDisable(true);
    }
}
