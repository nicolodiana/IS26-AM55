package it.polimi.ingsw.am55.view.gui.scene;

import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.endgame.EndGameEffectView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;
import it.polimi.ingsw.am55.view.gui.GuiView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Map;

public class EndGameSceneController implements GenericSceneController {

    @FXML private Label titleLabel;
    @FXML private Label statusLabel;
    @FXML private VBox winnersBox;
    @FXML private VBox effectsBox;
    @FXML private VBox finalEventsBox;

    private GuiView guiView;

    @Override
    public void setGuiView(GuiView guiView) {
        this.guiView = guiView;
    }

    public void render(EndGameResultView result, GameView lastGameView) {
        titleLabel.setText("Fine partita" + (lastGameView == null ? "" : " - Game " + lastGameView.getGameId()));
        winnersBox.getChildren().clear();
        effectsBox.getChildren().clear();
        finalEventsBox.getChildren().clear();

        if (result == null) {
            showStatus("Risultato finale non ancora disponibile.");
            return;
        }

        renderWinners(result);
        renderEffects(result);
        renderResolvedEvents(result);
        showStatus("Partita terminata.");
    }

    private void renderWinners(EndGameResultView result) {
        if (result.getWinners() == null || result.getWinners().isEmpty()) {
            winnersBox.getChildren().add(new Label("Nessun vincitore disponibile."));
            return;
        }

        for (Map.Entry<String, Integer> entry : result.getWinners().entrySet()) {
            Label label = new Label(entry.getKey() + " = " + entry.getValue() + " PP");
            label.getStyleClass().add("winner-text");
            winnersBox.getChildren().add(label);
        }
    }

    private void renderEffects(EndGameResultView result) {
        if (result.getEndGameEffects() == null || result.getEndGameEffects().isEmpty()) {
            effectsBox.getChildren().add(new Label("Nessun effetto finale."));
            return;
        }

        for (EndGameEffectView effect : result.getEndGameEffects()) {
            String sign = effect.getPointDelta() >= 0 ? "+" : "";
            Label label = new Label(effect.getPlayerNickname()
                    + ": "
                    + effect.getDescription()
                    + " ("
                    + sign
                    + effect.getPointDelta()
                    + " PP)");
            label.setWrapText(true);
            effectsBox.getChildren().add(label);
        }
    }

    private void renderResolvedEvents(EndGameResultView result) {
        if (result.getResolvedEvents() == null || result.getResolvedEvents().isEmpty()) {
            finalEventsBox.getChildren().add(new Label("Nessun evento finale risolto."));
            return;
        }

        for (ResolveEventView event : result.getResolvedEvents()) {
            VBox eventBox = new VBox(4);
            eventBox.getStyleClass().add("event-box");

            String name = event == null ? "Evento" : event.getNameEvent();

            Label title = new Label(name == null ? "Evento" : name);
            title.getStyleClass().add("event-title");

            Label details = new Label(event == null ? "" : event.showEvent().toString());
            details.setWrapText(true);

            eventBox.getChildren().addAll(title, details);
            finalEventsBox.getChildren().add(eventBox);
        }
    }

    @FXML
    private void onRefreshClick() {
        guiView.refreshCurrentScene();
    }

    public void showStatus(String message) {
        statusLabel.setText(message == null ? "" : message);
    }

    public void showError(String message) { statusLabel.setText(message == null ? "" : ("Errore: " + message)); }

    @Override
    public void lockInteractions(String message) { }
}
