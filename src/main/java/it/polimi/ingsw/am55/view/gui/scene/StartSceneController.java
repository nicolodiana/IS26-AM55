package it.polimi.ingsw.am55.view.gui.scene;

import it.polimi.ingsw.am55.view.gui.GuiView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class StartSceneController implements GenericSceneController {

    @FXML
    private Button startButton;

    @FXML
    private Label statusLabel;

    private GuiView guiView;

    @Override
    public void setGuiView(GuiView guiView) {
        this.guiView = guiView;
    }

    @FXML
    private void initialize() {
        statusLabel.setText("Premi INIZIA GIOCO per sincronizzarti con la lobby.");
    }

    @FXML
    private void onStartClicked() {
        if (guiView == null) {
            return;
        }

        startButton.setDisable(true);
        statusLabel.setText("Sincronizzazione lobby con il server...");

        guiView.startGameFromInitialScene();
    }

    public void showMessage(String message) {
        statusLabel.setText(message);
    }
}