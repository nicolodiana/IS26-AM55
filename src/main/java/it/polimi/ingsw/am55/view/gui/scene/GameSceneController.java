package it.polimi.ingsw.am55.view.gui.scene;

import it.polimi.ingsw.am55.dto.BiddingTicketView;
import it.polimi.ingsw.am55.dto.BoardView;
import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.PlayerView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;
import it.polimi.ingsw.am55.view.ClientAction;
import it.polimi.ingsw.am55.view.gui.GuiInteractionMode;
import it.polimi.ingsw.am55.view.gui.GuiView;
import it.polimi.ingsw.am55.view.gui.assets.CardFormatter;
import it.polimi.ingsw.am55.view.gui.assets.ImageResources;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Controller for the in-game board scene.
 * <p>
 * The controller renders players, turn ticket, bidding trail, board rows and
 * resolved events. It also determines which board elements are clickable for the
 * local player. Standard multi-row picks are highlighted one row at a time: lower
 * required picks are offered first, then upper required picks, matching the
 * distinct pick steps performed by the model.
 */
public class GameSceneController implements GenericSceneController {

    @FXML private Label gameIdLabel;
    @FXML private Label stateLabel;
    @FXML private Label roundLabel;
    @FXML private Label currentPlayerLabel;
    @FXML private Label instructionLabel;
    @FXML private Label statusLabel;
    @FXML private VBox playersBox;
    @FXML private HBox turnTicketBox;
    @FXML private HBox biddingTrailBox;
    @FXML private HBox upperRowBox;
    @FXML private HBox lowerRowBox;
    @FXML private VBox eventsBox;
    @FXML private ScrollPane eventsScrollPane;

    private final ImageResources imageResources = new ImageResources();

    private GuiView guiView;
    private GuiInteractionMode mode = GuiInteractionMode.READ_ONLY;
    private BoardSnapshot previousBoardSnapshot;
    private PickProgress pickProgress;
    private RowName currentStandardPickRow;

    /**
     * Injects the owning GUI view.
     */
    @Override
    public void setGuiView(GuiView guiView) {
        this.guiView = guiView;
    }

    /**
     * Renders the full game scene from the latest snapshot.
     *
     * @param gameView latest game snapshot
     * @param action current client action
     * @param myPlayerId local player nickname
     */
    public void render(GameView gameView, ClientAction action, String myPlayerId) {
        if (gameView == null) {
            return;
        }

        this.mode = toGuiMode(action);
        synchronizePickProgress(gameView, myPlayerId);

        gameIdLabel.setText("Game: " + safe(gameView.getGameId()));
        stateLabel.setText("State: " + safe(gameView.getState()));
        roundLabel.setText("Round: " + gameView.getRound());
        currentPlayerLabel.setText("Current player: " + safe(gameView.getCurrentPlayer()));

        renderPlayers(gameView.getPlayers(), myPlayerId);
        renderTurnTicket(gameView.getBoard());
        renderBiddingTrail(gameView.getBoard(), myPlayerId);
        renderRows(gameView.getBoard());
        renderEvents(gameView.getResolveEvents());
        updateInstruction(action, gameView, myPlayerId);

        previousBoardSnapshot = BoardSnapshot.from(gameView.getBoard());
    }

    /**
     * Shows a non-error status message.
     */
    @Override
    public void showStatus(String message) {
        statusLabel.getStyleClass().removeAll("error-text", "info-text");
        statusLabel.getStyleClass().add("info-text");
        statusLabel.setText(message == null ? "" : message);
    }

    /**
     * Shows an error status message.
     */
    @Override
    public void showError(String message) {
        statusLabel.getStyleClass().removeAll("error-text", "info-text");
        statusLabel.getStyleClass().add("error-text");
        statusLabel.setText(message == null ? "" : message);
    }

    /**
     * Disables all clickable board elements without changing the current messages.
     */
    @Override
    public void lockInteractions() {
        this.mode = GuiInteractionMode.LOCKED;
        disableChildrenClicks(biddingTrailBox);
        disableChildrenClicks(upperRowBox);
        disableChildrenClicks(lowerRowBox);
    }

    /**
     * Handles the quit button in the game scene.
     */
    @FXML
    private void onQuitClick() {
        guiView.quitGame();
    }

    /**
     * Converts a shared client action into a local GUI interaction mode.
     */
    private GuiInteractionMode toGuiMode(ClientAction action) {
        if (action == null) {
            return GuiInteractionMode.READ_ONLY;
        }
        return switch (action) {
            case PLACE_TOTEM -> GuiInteractionMode.PLACE_TOTEM;
            case PICK_CARD -> GuiInteractionMode.PICK_CARD;
            case PICK_SPECIAL -> GuiInteractionMode.PICK_SPECIAL;
            case RESOLVE_EVENTS -> GuiInteractionMode.RESOLVE_EVENTS;
            case END_GAME -> GuiInteractionMode.END_GAME;
            default -> GuiInteractionMode.READ_ONLY;
        };
    }

    /**
     * Renders all players and lets the user click a player to inspect that hand.
     */
    private void renderPlayers(List<PlayerView> players, String myPlayerId) {
        playersBox.getChildren().clear();

        if (players == null || players.isEmpty()) {
            playersBox.getChildren().add(new Label("No players"));
            return;
        }

        for (PlayerView player : players) {
            if (player == null) {
                continue;
            }

            HBox row = new HBox(8);
            row.getStyleClass().addAll("player-row", "clickable-player");
            row.setAlignment(Pos.CENTER_LEFT);

            Image totem = imageResources.loadTotem(player.getTotemColor());
            if (totem != null) {
                ImageView totemView = new ImageView(totem);
                totemView.setFitWidth(34);
                totemView.setFitHeight(34);
                totemView.setPreserveRatio(true);
                row.getChildren().add(totemView);
            }

            Label label = new Label(player.getNickname()
                    + " | Totem: " + player.getTotemColor()
                    + " | Food: " + player.getFood()
                    + " | PP: " + player.getPoints());

            if (equalsIgnoreCase(player.getNickname(), myPlayerId)) {
                label.getStyleClass().add("my-player-text");
            }

            row.getChildren().add(label);
            row.setOnMouseClicked(event -> showPlayerCardPopUp(player));
            playersBox.getChildren().add(row);
        }
    }

    /**
     * Opens a small secondary window containing a player's visible hand.
     */
    private void showPlayerCardPopUp(PlayerView player) {
        Stage stage = new Stage();
        stage.setTitle("Cards of " + player.getNickname());
        stage.setResizable(true);

        HBox cardsBox = new HBox(12);
        cardsBox.setAlignment(Pos.CENTER_LEFT);
        ScrollPane scrollPane = new ScrollPane(cardsBox);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        List<CardView> cards = player.getMyHand();
        if (cards == null || cards.isEmpty()) {
            cardsBox.getChildren().add(new Label("No visible card"));
        } else {
            for (CardView card : cards) {
                cardsBox.getChildren().add(createHandCardPane(card, scrollPane));
            }
        }

        stage.setScene(new Scene(scrollPane, 760, 340));
        stage.show();
    }

    /**
     * Renders the second-phase turn ticket and its occupants.
     */
    private void renderTurnTicket(BoardView board) {
        turnTicketBox.getChildren().clear();
        if (board == null || board.getTurnTicket() == null) {
            return;
        }

        Image turnTicket = imageResources.loadTurnTicket(board.getTurnTicket().size());
        if (turnTicket != null) {
            ImageView view = new ImageView(turnTicket);
            view.setFitWidth(170);
            view.setPreserveRatio(true);
            turnTicketBox.getChildren().add(view);
        }

        for (int i = 0; i < board.getTurnTicket().size(); i++) {
            PlayerView player = board.getTurnTicket().get(i);
            StackPane slot = new StackPane();
            slot.getStyleClass().add("turn-slot");
            slot.setPrefSize(90, 64);

            if (player == null) {
                slot.getChildren().add(new Label("-"));
            } else {
                VBox content = new VBox(2);
                content.setAlignment(Pos.CENTER);
                Image totem = imageResources.loadTotem(player.getTotemColor());
                if (totem != null) {
                    ImageView totemView = new ImageView(totem);
                    totemView.setFitWidth(32);
                    totemView.setFitHeight(32);
                    totemView.setPreserveRatio(true);
                    content.getChildren().add(totemView);
                }
                content.getChildren().add(new Label(player.getNickname()));
                slot.getChildren().add(content);
            }
            turnTicketBox.getChildren().add(slot);
        }
    }

    /**
     * Renders bidding trail tickets and enables free tickets during totem placement.
     */
    private void renderBiddingTrail(BoardView board, String myPlayerId) {
        biddingTrailBox.getChildren().clear();
        if (board == null || board.getBiddingTrail() == null) {
            return;
        }

        for (int i = 0; i < board.getBiddingTrail().size(); i++) {
            BiddingTicketView ticket = board.getBiddingTrail().get(i);
            biddingTrailBox.getChildren().add(createBiddingTicketPane(ticket, i, myPlayerId));
        }
    }

    /**
     * Creates one visual bidding ticket node.
     */
    private StackPane createBiddingTicketPane(BiddingTicketView ticket, int index, String myPlayerId) {
        StackPane pane = new StackPane();
        pane.getStyleClass().add("ticket-pane");
        pane.setPrefSize(140, 150);

        Image ticketImage = ticket == null ? null : imageResources.loadBiddingTicket(ticket.getTrailPlacement());
        if (ticketImage != null) {
            ImageView view = new ImageView(ticketImage);
            view.setFitWidth(124);
            view.setPreserveRatio(true);
            pane.getChildren().add(view);
        } else {
            pane.getChildren().add(new Label(ticket == null ? "Ticket" : "Ticket " + ticket.getTrailPlacement()));
        }

        pane.getChildren().add(ticketOverlay(ticket));

        boolean canClick = mode == GuiInteractionMode.PLACE_TOTEM && ticket != null && !ticket.isTaken();
        if (canClick) {
            pane.getStyleClass().add("clickable-highlight");
            pane.setOnMouseClicked(event -> guiView.placeTotem(index));
            Tooltip.install(pane, new Tooltip("Place your totem on ticket "
                    + ticket.getTrailPlacement() + " (index " + index + ")"));
        } else {
            pane.setOnMouseClicked(null);
            if (ticket != null && ticket.isTaken()
                    && ticket.getPlayer() != null
                    && equalsIgnoreCase(ticket.getPlayer().getNickname(), myPlayerId)) {
                pane.getStyleClass().add("selected-ticket");
            }
        }

        return pane;
    }

    /**
     * Creates the bottom overlay for a bidding ticket.
     */
    private VBox ticketOverlay(BiddingTicketView ticket) {
        VBox overlay = new VBox(3);
        overlay.setAlignment(Pos.BOTTOM_CENTER);
        overlay.getStyleClass().add("ticket-overlay");

        if (ticket != null && ticket.getPlayer() != null) {
            Label playerLabel = new Label(ticket.getPlayer().getNickname());
            playerLabel.getStyleClass().add("small-pill");
            overlay.getChildren().add(playerLabel);

            Image totem = imageResources.loadTotem(ticket.getPlayer().getTotemColor());
            if (totem != null) {
                ImageView totemView = new ImageView(totem);
                totemView.setFitWidth(34);
                totemView.setFitHeight(34);
                totemView.setPreserveRatio(true);
                overlay.getChildren().add(totemView);
            }
        }
        return overlay;
    }

    /**
     * Renders upper and lower card rows with one enabled row at a time.
     */
    private void renderRows(BoardView board) {
        upperRowBox.getChildren().clear();
        lowerRowBox.getChildren().clear();

        if (board == null) {
            return;
        }

        currentStandardPickRow = null;
        Set<RowName> enabledRows = resolveEnabledRows(board);
        renderCardRow(upperRowBox, board.getUpperRow(), RowName.UPPER, enabledRows.contains(RowName.UPPER));
        renderCardRow(lowerRowBox, board.getLowerRow(), RowName.LOWER, enabledRows.contains(RowName.LOWER));
    }

    /**
     * Decides which row is currently pickable.
     */
    private Set<RowName> resolveEnabledRows(BoardView board) {
        Set<RowName> rows = new HashSet<>();

        if (mode == GuiInteractionMode.PICK_SPECIAL) {
            rows.add(RowName.UPPER);
            return rows;
        }

        if (mode != GuiInteractionMode.PICK_CARD || pickProgress == null) {
            return rows;
        }

        RowName nextRow = nextStandardPickRow(board);
        if (nextRow != null) {
            currentStandardPickRow = nextRow;
            rows.add(nextRow);
        }
        return rows;
    }

    /**
     * Returns the next row to offer for a standard pick, lower first and then upper.
     */
    private RowName nextStandardPickRow(BoardView board) {
        int remainingLower = Math.max(0, pickProgress.requiredLower - pickProgress.pickedLower);
        int remainingUpper = Math.max(0, pickProgress.requiredUpper - pickProgress.pickedUpper);
        boolean lowerPickable = hasPickableCard(board.getLowerRow());
        boolean upperPickable = hasPickableCard(board.getUpperRow());

        if (remainingLower > 0 && lowerPickable) {
            return RowName.LOWER;
        }
        if (remainingUpper > 0 && upperPickable) {
            return RowName.UPPER;
        }
        if (remainingLower > 0) {
            return RowName.LOWER;
        }
        if (remainingUpper > 0) {
            return RowName.UPPER;
        }
        return null;
    }

    /**
     * Renders all cards in a row.
     */
    private void renderCardRow(HBox rowBox, List<CardView> cards, RowName rowName, boolean rowEnabled) {
        if (cards == null || cards.isEmpty()) {
            Label empty = new Label("No cards");
            empty.getStyleClass().add("muted-text");
            rowBox.getChildren().add(empty);
            return;
        }

        for (CardView card : cards) {
            rowBox.getChildren().add(createCardPane(card, rowName, rowEnabled));
        }
    }

    /**
     * Creates one board card node and attaches the correct pick handler when enabled.
     */
    private StackPane createCardPane(CardView card, RowName rowName, boolean rowEnabled) {
        StackPane pane = baseCardPane(card);

        boolean canPickStandard = mode == GuiInteractionMode.PICK_CARD
                && rowEnabled
                && card != null
                && !CardFormatter.isEvent(card);
        boolean canPickSpecial = mode == GuiInteractionMode.PICK_SPECIAL
                && rowName == RowName.UPPER
                && card != null
                && !CardFormatter.isEvent(card);

        if (canPickStandard || canPickSpecial) {
            pane.getStyleClass().add("clickable-highlight");
            int cardId = card.getId();
            pane.setOnMouseClicked(event -> {
                if (mode == GuiInteractionMode.PICK_SPECIAL) {
                    guiView.pickSpecial(cardId);
                } else {
                    guiView.pickCard(cardId);
                }
            });
        } else if (rowEnabled && card != null && CardFormatter.isEvent(card)) {
            pane.getStyleClass().add("event-card-disabled");
        }

        return pane;
    }

    /**
     * Creates a non-clickable hand-card node for the card popup.
     */
    private VBox createHandCardPane(CardView card, ScrollPane scrollPane) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("card-pane");

        Image image = card == null ? null : imageResources.loadCard(card.getId());
        if (image != null) {
            ImageView view = new ImageView(image);
            view.fitHeightProperty().bind(scrollPane.heightProperty().multiply(0.75));
            view.setPreserveRatio(true);
            box.getChildren().add(view);
        } else {
            Label fallback = new Label(CardFormatter.shortLabel(card));
            fallback.setWrapText(true);
            fallback.getStyleClass().add("card-fallback");
            box.getChildren().add(fallback);
        }

        if (card != null) {
            Tooltip.install(box, new Tooltip(CardFormatter.tooltip(card)));
        }
        return box;
    }

    /**
     * Creates the shared visual structure used for board cards.
     */
    private StackPane baseCardPane(CardView card) {
        StackPane pane = new StackPane();
        pane.getStyleClass().add("card-pane");
        pane.setPrefSize(132, 190);

        Image image = card == null ? null : imageResources.loadCard(card.getId());
        if (image != null) {
            ImageView view = new ImageView(image);
            view.setFitWidth(122);
            view.setPreserveRatio(true);
            pane.getChildren().add(view);
        } else {
            Label fallback = new Label(CardFormatter.shortLabel(card));
            fallback.setWrapText(true);
            fallback.getStyleClass().add("card-fallback");
            pane.getChildren().add(fallback);
        }

        Label idLabel = new Label(card == null ? "" : "#" + card.getId());
        idLabel.getStyleClass().add("card-id-pill");
        StackPane.setAlignment(idLabel, Pos.TOP_RIGHT);
        pane.getChildren().add(idLabel);

        if (card != null) {
            Tooltip.install(pane, new Tooltip(CardFormatter.tooltip(card)));
        }
        return pane;
    }

    /**
     * Renders event resolution details when the model provides them.
     */
    private void renderEvents(List<ResolveEventView> events) {
        eventsBox.getChildren().clear();

        if (events == null || events.isEmpty()) {
            eventsScrollPane.setVisible(false);
            eventsScrollPane.setManaged(false);
            return;
        }

        eventsScrollPane.setVisible(true);
        eventsScrollPane.setManaged(true);

        for (ResolveEventView event : events) {
            VBox eventBox = new VBox(4);
            eventBox.getStyleClass().add("event-box");

            String name = event == null ? "Event" : event.getNameEvent();
            Label title = new Label(name == null ? "Event" : name);
            title.getStyleClass().add("event-title");

            Label detail = new Label(event == null ? "" : String.valueOf(event.showEvent()));
            detail.setWrapText(true);
            VBox.setVgrow(detail, Priority.ALWAYS);

            eventBox.getChildren().addAll(title, detail);
            eventsBox.getChildren().add(eventBox);
        }
    }

    /**
     * Updates the instruction banner according to the current action.
     */
    private void updateInstruction(ClientAction action, GameView gameView, String myPlayerId) {
        String currentPlayer = gameView == null ? null : gameView.getCurrentPlayer();
        String text = switch (action) {
            case PLACE_TOTEM -> "It is your turn: choose a free ticket on the Bidding Trail.";
            case PICK_CARD -> pickInstruction();
            case PICK_SPECIAL -> "Building 13: choose a card from the UPPER ROW.";
            case WAITING_FOR_TURN -> "Wait for " + safe(currentPlayer) + " to finish the turn.";
            case RESOLVE_EVENTS -> "Event resolution: read the summary and wait for the next update.";
            case END_GAME -> "The game has ended.";
            case CRASHED -> "The game is closed.";
            default -> "Wait for a state update.";
        };

        if (myPlayerId == null || myPlayerId.isBlank()) {
            text += " Local nickname is not known yet.";
        }
        instructionLabel.setText(text);
    }

    /**
     * Builds a pick instruction matching the one highlighted row.
     */
    private String pickInstruction() {
        RowName nextRow = currentStandardPickRow != null ? currentStandardPickRow : nextStandardPickRowFromProgressOnly();
        if (nextRow == RowName.LOWER) {
            return "It is your turn: choose a highlighted card from the LOWER ROW.";
        }
        if (nextRow == RowName.UPPER) {
            return "It is your turn: choose a highlighted card from the UPPER ROW.";
        }
        return "It is your turn: choose one highlighted card.";
    }

    /**
     * Computes the next row using only tracked progress for instruction text.
     */
    private RowName nextStandardPickRowFromProgressOnly() {
        if (pickProgress == null) {
            return null;
        }
        if (pickProgress.pickedLower < pickProgress.requiredLower) {
            return RowName.LOWER;
        }
        if (pickProgress.pickedUpper < pickProgress.requiredUpper) {
            return RowName.UPPER;
        }
        return null;
    }

    /**
     * Synchronizes local standard-pick progress by comparing board snapshots.
     */
    private void synchronizePickProgress(GameView gameView, String myPlayerId) {
        BoardView board = gameView.getBoard();
        BoardSnapshot currentSnapshot = BoardSnapshot.from(board);
        BiddingTicketView ticket = findMyTicket(board, myPlayerId);

        if (mode != GuiInteractionMode.PICK_CARD || ticket == null) {
            pickProgress = null;
            previousBoardSnapshot = currentSnapshot;
            return;
        }

        String key = gameView.getGameId()
                + "|" + gameView.getRound()
                + "|" + safe(gameView.getCurrentPlayer())
                + "|" + ticket.getTrailPlacement();

        if (pickProgress == null || !Objects.equals(pickProgress.key, key)) {
            pickProgress = new PickProgress(key, ticket.getChooseUpperCard(), ticket.getChooseLowerCard());
        } else {
            pickProgress.applyBoardDiff(previousBoardSnapshot, currentSnapshot);
        }
    }

    /**
     * Finds the bidding ticket occupied by the local player.
     */
    private BiddingTicketView findMyTicket(BoardView board, String myPlayerId) {
        if (board == null || board.getBiddingTrail() == null || myPlayerId == null) {
            return null;
        }

        for (BiddingTicketView ticket : board.getBiddingTrail()) {
            if (ticket != null
                    && ticket.getPlayer() != null
                    && equalsIgnoreCase(ticket.getPlayer().getNickname(), myPlayerId)) {
                return ticket;
            }
        }
        return null;
    }

    /**
     * Checks whether a row has at least one non-event card.
     */
    private boolean hasPickableCard(List<CardView> cards) {
        if (cards == null) {
            return false;
        }
        for (CardView card : cards) {
            if (card != null && !CardFormatter.isEvent(card)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes mouse handlers and disables all nodes in an HBox.
     */
    private void disableChildrenClicks(HBox box) {
        if (box == null) {
            return;
        }
        for (Node child : box.getChildren()) {
            child.setOnMouseClicked(null);
            child.setDisable(true);
        }
    }

    /**
     * Converts nullable values into visible labels.
     */
    private String safe(Object value) {
        return value == null ? "-" : String.valueOf(value);
    }

    /**
     * Null-safe case-insensitive comparison.
     */
    private boolean equalsIgnoreCase(String a, String b) {
        return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
    }

    /**
     * Board row names used by highlight logic.
     */
    private enum RowName {
        UPPER,
        LOWER
    }

    /**
     * Tracks standard-pick progress for the current player turn.
     */
    private static final class PickProgress {
        private final String key;
        private final int requiredUpper;
        private final int requiredLower;
        private int pickedUpper;
        private int pickedLower;

        private PickProgress(String key, int requiredUpper, int requiredLower) {
            this.key = key;
            this.requiredUpper = requiredUpper;
            this.requiredLower = requiredLower;
        }

        /**
         * Updates picked counters when a board snapshot shows that a card was removed.
         */
        private void applyBoardDiff(BoardSnapshot previous, BoardSnapshot current) {
            if (previous == null || current == null) {
                return;
            }
            pickedUpper = Math.min(requiredUpper, pickedUpper + previous.removedUpperCount(current));
            pickedLower = Math.min(requiredLower, pickedLower + previous.removedLowerCount(current));
        }
    }

    /**
     * Minimal card-id snapshot used to infer which row changed after a pick update.
     */
    private static final class BoardSnapshot {
        private final Set<Integer> upperIds;
        private final Set<Integer> lowerIds;

        private BoardSnapshot(Set<Integer> upperIds, Set<Integer> lowerIds) {
            this.upperIds = upperIds;
            this.lowerIds = lowerIds;
        }

        /**
         * Creates a snapshot from a board DTO.
         */
        private static BoardSnapshot from(BoardView board) {
            if (board == null) {
                return new BoardSnapshot(Set.of(), Set.of());
            }
            return new BoardSnapshot(idsOf(board.getUpperRow()), idsOf(board.getLowerRow()));
        }

        /**
         * Counts cards that disappeared from the upper row.
         */
        private int removedUpperCount(BoardSnapshot current) {
            return removedCount(upperIds, current.upperIds);
        }

        /**
         * Counts cards that disappeared from the lower row.
         */
        private int removedLowerCount(BoardSnapshot current) {
            return removedCount(lowerIds, current.lowerIds);
        }

        /**
         * Extracts card ids from a row.
         */
        private static Set<Integer> idsOf(List<CardView> cards) {
            Set<Integer> ids = new HashSet<>();
            if (cards == null) {
                return ids;
            }
            for (CardView card : cards) {
                if (card != null) {
                    ids.add(card.getId());
                }
            }
            return ids;
        }

        /**
         * Counts ids present in the previous set but missing in the current set.
         */
        private static int removedCount(Set<Integer> previous, Set<Integer> current) {
            int removed = 0;
            for (Integer id : previous) {
                if (!current.contains(id)) {
                    removed++;
                }
            }
            return removed;
        }
    }
}
