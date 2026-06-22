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
     * Locks user interactions without changing the currently visible status message.
     * <p>
     * Each scene controller decides which controls must be disabled for its own
     * layout. The GUI view uses this method before dispatching a command so that
     * repeated clicks are prevented while the next server update is pending.
     */
    void lockInteractions();
}
