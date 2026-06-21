package it.polimi.ingsw.am55.view.gui.scene;

import it.polimi.ingsw.am55.view.gui.GuiView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class QuitGameSceneController implements GenericSceneController {

    @FXML private Label messageLabel;

    private GuiView guiView;

    @Override
    public void setGuiView(GuiView guiView) {
        this.guiView = guiView;
    }

    public void render(String message) {
        messageLabel.setText(message == null || message.isBlank()
                ? "La partita è stata chiusa."
                : message);
    }

    @Override
    public void showStatus(String message) {
        render(message);
    }

    @Override
    public void showError(String message) {
        render(message == null || message.isBlank() ? "Errore." : "Errore: " + message);
    }

    @Override
    public void lockInteractions(String message) {
        // Schermata terminale: evita di sovrascrivere il messaggio finale.
    }
}
