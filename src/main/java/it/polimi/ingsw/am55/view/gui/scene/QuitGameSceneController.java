package it.polimi.ingsw.am55.view.gui.scene;

import it.polimi.ingsw.am55.view.gui.GuiView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for terminal messages such as quit, crash, and connection loss.
 */
public class QuitGameSceneController implements GenericSceneController {

    @FXML private Label messageLabel;


    /**
     * Injects the owning GUI view.
     */
    @Override
    public void setGuiView(GuiView guiView) {
        // No interactive actions are available in this terminal scene.
    }

    /**
     * Renders the terminal message.
     *
     * @param message message to display
     */
    public void render(String message) {
        messageLabel.setText(message == null || message.isBlank()
                ? "The game has been closed."
                : message);
    }

    /**
     * Shows a non-error status message.
     */
    @Override
    public void showStatus(String message) {
        render(message);
    }

    /**
     * Shows an error message.
     */
    @Override
    public void showError(String message) {
        render(message == null || message.isBlank() ? "Error." : "Error: " + message);
    }

    /**
     * Keeps the final message stable because this scene has no active controls.
     */
    @Override
    public void lockInteractions(String message) {
        // Terminal scene: there are no interactions to lock.
    }
}
