package it.polimi.ingsw.am55.view.gui.scene;

import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.endgame.EndGameEffectView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.dto.endgame.LeaderBoardEntryView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;
import it.polimi.ingsw.am55.view.gui.GuiView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * Controller for the final results scene.
 * <p>
 * It renders winners, final scoring effects, resolved final events, and the
 * database leaderboard included in the {@link EndGameResultView}.
 */
public class EndGameSceneController implements GenericSceneController {

    @FXML private Label titleLabel;
    @FXML private Label statusLabel;
    @FXML private VBox winnersBox;
    @FXML private VBox leaderBoardBox;
    @FXML private VBox effectsBox;
    @FXML private VBox finalEventsBox;

    /**
     * The final scene does not send commands, but the reference is accepted for a
     * uniform controller contract.
     */
    @Override
    public void setGuiView(GuiView guiView) {
        // No interactive actions are available in the final scene.
    }

    /**
     * Renders all final-result sections.
     *
     * @param result final result DTO
     * @param lastGameView last available game snapshot
     */
    public void render(EndGameResultView result, GameView lastGameView) {
        titleLabel.setText("Game Over" + (lastGameView == null ? "" : " - Game " + lastGameView.getGameId()));
        winnersBox.getChildren().clear();
        leaderBoardBox.getChildren().clear();
        effectsBox.getChildren().clear();
        finalEventsBox.getChildren().clear();

        if (result == null) {
            showStatus("Final result is not available yet.");
            return;
        }

        renderWinners(result);
        renderLeaderboard(result);
        renderEffects(result);
        renderResolvedEvents(result);
        showStatus("The game has ended.");
    }

    /**
     * Renders the winner section.
     */
    private void renderWinners(EndGameResultView result) {
        if (result.getWinners() == null || result.getWinners().isEmpty()) {
            winnersBox.getChildren().add(new Label("No winner is available."));
            return;
        }

        for (Map.Entry<String, Integer> entry : result.getWinners().entrySet()) {
            Label label = new Label(entry.getKey() + " = " + entry.getValue() + " PP");
            label.getStyleClass().add("winner-text");
            winnersBox.getChildren().add(label);
        }
    }

    /**
     * Renders the persistent database leaderboard sent by the server.
     */
    private void renderLeaderboard(EndGameResultView result) {
        if (result.getLeaderBoard() == null || result.getLeaderBoard().isEmpty()) {
            leaderBoardBox.getChildren().add(new Label("No leaderboard data is available."));
            return;
        }

        HBox header = leaderboardRow("Pos.", "Nickname", "PP", "Food", "Date");
        header.getStyleClass().add("leaderboard-header");
        leaderBoardBox.getChildren().add(header);

        for (LeaderBoardEntryView entry : result.getLeaderBoard()) {
            if (entry == null) {
                continue;
            }
            leaderBoardBox.getChildren().add(leaderboardRow(
                    String.valueOf(entry.getPosition()),
                    entry.getPlayerNickname(),
                    String.valueOf(entry.getPrestigePoint()),
                    String.valueOf(entry.getFoodPoint()),
                    String.valueOf(entry.getDate())
            ));
        }
    }

    /**
     * Creates one visual row for the leaderboard section.
     */
    private HBox leaderboardRow(String position, String nickname, String points, String food, String date) {
        HBox row = new HBox(10);
        row.getStyleClass().add("leaderboard-row");
        row.getChildren().addAll(
                fixedLabel(position, 58),
                fixedLabel(nickname, 180),
                fixedLabel(points, 60),
                fixedLabel(food, 70),
                flexibleLabel(date)
        );
        return row;
    }

    /**
     * Creates a label with a fixed preferred width.
     */
    private Label fixedLabel(String text, double width) {
        Label label = new Label(text == null ? "-" : text);
        label.setPrefWidth(width);
        return label;
    }

    /**
     * Creates a wrapping label that fills the remaining row width.
     */
    private Label flexibleLabel(String text) {
        Label label = new Label(text == null ? "-" : text);
        label.setWrapText(true);
        HBox.setHgrow(label, Priority.ALWAYS);
        return label;
    }

    /**
     * Renders the final scoring effects.
     */
    private void renderEffects(EndGameResultView result) {
        if (result.getEndGameEffects() == null || result.getEndGameEffects().isEmpty()) {
            effectsBox.getChildren().add(new Label("No final effect."));
            return;
        }

        for (EndGameEffectView effect : result.getEndGameEffects()) {
            if (effect == null) {
                continue;
            }
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

    /**
     * Renders final events resolved before the game ended.
     */
    private void renderResolvedEvents(EndGameResultView result) {
        if (result.getResolvedEvents() == null || result.getResolvedEvents().isEmpty()) {
            finalEventsBox.getChildren().add(new Label("No final event was resolved."));
            return;
        }

        for (ResolveEventView event : result.getResolvedEvents()) {
            VBox eventBox = new VBox(4);
            eventBox.getStyleClass().add("event-box");

            String name = event == null ? "Event" : event.getNameEvent();
            Label title = new Label(name == null ? "Event" : name);
            title.getStyleClass().add("event-title");

            Label details = new Label(event == null ? "" : String.valueOf(event.showEvent()));
            details.setWrapText(true);

            eventBox.getChildren().addAll(title, details);
            finalEventsBox.getChildren().add(eventBox);
        }
    }

    /**
     * Shows a non-error status message.
     */
    @Override
    public void showStatus(String message) {
        statusLabel.setText(message == null ? "" : message);
    }

    /**
     * Shows an error status message.
     */
    @Override
    public void showError(String message) {
        showStatus(message == null || message.isBlank() ? "" : "Error: " + message);
    }

    /**
     * Does nothing because the final scene has no gameplay interactions.
     */
    @Override
    public void lockInteractions(String message) {
        // Final scene: no interactions to lock.
    }
}
