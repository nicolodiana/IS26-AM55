package it.polimi.ingsw.am55.view.gui.scene;

import it.polimi.ingsw.am55.view.gui.GuiView;

/**
 * Common contract implemented by all JavaFX scene controllers.
 */
public interface GenericSceneController {

    /**
     * Injects the owning GUI view after FXML loading.
     *
     * @param guiView JavaFX view facade
     */
    void setGuiView(GuiView guiView);

    /**
     * Shows a non-error status message.
     *
     * @param message status text
     */
    void showStatus(String message);

    /**
     * Shows an error status message.
     *
     * @param message error text
     */
    void showError(String message);

    /**
     * Locks user interactions while a command is waiting for a server response.
     *
     * @param message message explaining why interactions are locked
     */
    void lockInteractions(String message);
}
