package it.polimi.ingsw.am55.view.gui.scene;

import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.view.gui.GuiView;

/** Marker comune per tutti i controller FXML della GUI. */
public interface GenericSceneController {
    void setGuiView(GuiView guiView);
    void showStatus(String message);
    //void showMessage(MessageToClient message);
    void showError(String message);
    void lockInteractions(String message);
}
