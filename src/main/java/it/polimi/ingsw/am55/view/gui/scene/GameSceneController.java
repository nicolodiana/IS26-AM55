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
import it.polimi.ingsw.am55.view.gui.assets.CardAssetResolver;
import it.polimi.ingsw.am55.view.gui.assets.CardFormatter;
import it.polimi.ingsw.am55.view.gui.assets.ImageResources;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public void setGuiView(GuiView guiView) {
        this.guiView = guiView;
    }

    public void render(GameView gameView, ClientAction action, String myPlayerId, boolean locked) {
        if (gameView == null) {
            return;
        }

        this.mode = locked ? GuiInteractionMode.LOCKED : toGuiMode(action);

        gameIdLabel.setText("Game: " + safe(gameView.getGameId()));
        stateLabel.setText("State: " + safe(gameView.getState()));
        roundLabel.setText("Round: " + gameView.getRound());
        currentPlayerLabel.setText("Current player: " + safe(gameView.getCurrentPlayer()));

        renderPlayers(gameView.getPlayers(), myPlayerId);
        renderTurnTicket(gameView.getBoard());
        renderBiddingTrail(gameView.getBoard(), myPlayerId);
        renderRows(gameView.getBoard(), myPlayerId);
        renderEvents(gameView.getResolveEvents());
        updateInstruction(action, gameView, myPlayerId, locked);
    }

    public void showStatus(String message) {
        statusLabel.getStyleClass().removeAll("error-text", "info-text");
        statusLabel.getStyleClass().add("info-text");
        statusLabel.setText(message == null ? "" : message);
    }

    public void showError(String message) {
        statusLabel.getStyleClass().removeAll("error-text", "info-text");
        statusLabel.getStyleClass().add("error-text");
        statusLabel.setText(message == null ? "" : message);
    }

    public void lockInteractions(String message) {
        this.mode = GuiInteractionMode.LOCKED;
        disableChildrenClicks(biddingTrailBox);
        disableChildrenClicks(upperRowBox);
        disableChildrenClicks(lowerRowBox);
        showStatus(message);
        instructionLabel.setText("Attendi la risposta del server...");
    }

    @FXML
    private void onRefreshClick() {
        guiView.refreshCurrentScene();
    }

    @FXML
    private void onQuitClick() {
        guiView.quitGame();
    }

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

    private void renderPlayers(List<PlayerView> players, String myPlayerId) {
        playersBox.getChildren().clear();

        if (players == null || players.isEmpty()) {
            playersBox.getChildren().add(new Label("Nessun giocatore"));
            return;
        }

        for (PlayerView player : players) {
            if (player == null) {
                continue;
            }

            HBox row = new HBox(8);
            row.getStyleClass().add("player-row");
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
            playersBox.getChildren().add(row);
            // to be able to click the player to see his hand
            row.setOnMouseClicked((event -> showPlayerCardPopUp(player)));
            row.getStyleClass().add("clickable-player");
        }

    }

    private void showPlayerCardPopUp(PlayerView player) {
        Stage stage = new Stage();
        stage.setTitle("Cards of " + player.getNickname());
        stage.setResizable(true);

        HBox cardsBox = new HBox(12); // 12 pixel between one element and another
        cardsBox.setAlignment(Pos.CENTER);

        List<CardView> cards = player.getMyHand();
        ScrollPane scrollPane = new ScrollPane(cardsBox);

        if (cards == null || cards.isEmpty()) {
            cardsBox.getChildren().add(new Label("No card visible"));
        }
        else {
            for (CardView card : cards) {
                cardsBox.getChildren().add(createResizableCardBox(card, scrollPane));
            }
        }

        /*scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(false);
        scrollPane.setPrefViewportHeight(260);
        scrollPane.setPrefViewportWidth(700);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);*/

        /*dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefSize(760, 340);

        dialog.show();*/
        Scene scene = new Scene(scrollPane, 760, 340);
        stage.setScene(scene);
        stage.show();
    }

    private void renderTurnTicket(BoardView board) {
        turnTicketBox.getChildren().clear();
        if (board == null || board.getTurnTicket() == null) {
            return;
        }

        int numPlayers = board.getTurnTicket().size();
        Image turnTicket = imageResources.loadTurnTicket(numPlayers);

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

    private void renderBiddingTrail(BoardView board, String myPlayerId) {
        biddingTrailBox.getChildren().clear();
        if (board == null || board.getBiddingTrail() == null) {
            return;
        }

        for (int i = 0; i < board.getBiddingTrail().size(); i++) {
            BiddingTicketView ticket = board.getBiddingTrail().get(i);
            StackPane ticketPane = createBiddingTicketPane(ticket, i, myPlayerId);
            biddingTrailBox.getChildren().add(ticketPane);
        }
    }

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

        pane.getChildren().add(overlay);

        boolean canClick = mode == GuiInteractionMode.PLACE_TOTEM
                && ticket != null
                && !ticket.isTaken();

        if (canClick) {
            pane.getStyleClass().add("clickable-highlight");
            pane.setOnMouseClicked(event -> guiView.placeTotem(index));
            Tooltip.install(pane, new Tooltip("Piazza il tuo totem sul ticket "
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

    private void renderRows(BoardView board, String myPlayerId) {
        upperRowBox.getChildren().clear();
        lowerRowBox.getChildren().clear();

        if (board == null) {
            return;
        }

        Set<RowName> enabledRows = resolveEnabledRows(board, myPlayerId);

        renderCardRow(upperRowBox, board.getUpperRow(), RowName.UPPER, enabledRows.contains(RowName.UPPER));
        renderCardRow(lowerRowBox, board.getLowerRow(), RowName.LOWER, enabledRows.contains(RowName.LOWER));
    }

    private Set<RowName> resolveEnabledRows(BoardView board, String myPlayerId) {
        Set<RowName> rows = new HashSet<>();

        if (mode == GuiInteractionMode.PICK_SPECIAL) {
            rows.add(RowName.UPPER);
            return rows;
        }

        if (mode != GuiInteractionMode.PICK_CARD || board.getBiddingTrail() == null) {
            return rows;
        }

        BiddingTicketView myTicket = null;
        for (BiddingTicketView ticket : board.getBiddingTrail()) {
            if (ticket != null && ticket.getPlayer() != null
                    && equalsIgnoreCase(ticket.getPlayer().getNickname(), myPlayerId)) {
                myTicket = ticket;
                break;
            }
        }

        if (myTicket == null) {
            return rows;
        }

        if (myTicket.getChooseUpperCard() > 0) {
            rows.add(RowName.UPPER);
        }
        if (myTicket.getChooseLowerCard() > 0) {
            rows.add(RowName.LOWER);
        }

        return rows;
    }

    private void renderCardRow(HBox rowBox, List<CardView> cards, RowName rowName, boolean rowEnabled) {
        if (cards == null || cards.isEmpty()) {
            Label empty = new Label("Nessuna carta");
            empty.getStyleClass().add("muted-text");
            rowBox.getChildren().add(empty);
            return;
        }

        for (CardView card : cards) {
            StackPane pane = createCardPane(card, rowName, rowEnabled);
            rowBox.getChildren().add(pane);
        }
    }

    private StackPane createCardPane(CardView card, RowName rowName, boolean rowEnabled) {
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

    private StackPane createHandCardPane(CardView card) {
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

        /*pane.setTranslateY(70);

        // serve per far zoomare la carta dopo due secondi che sono sopra con il mouse
        PauseTransition hoverDelay = new PauseTransition(Duration.seconds(2));

        pane.setOnMouseEntered(event -> {
            hoverDelay.setOnFinished(e -> {
                pane.setTranslateY(0);
                pane.setScaleX(1.35);
                pane.setScaleY(1.35);
                pane.toFront();
            });
            hoverDelay.playFromStart();
        });

        pane.setOnMouseExited(event -> {
            hoverDelay.stop();
            pane.setTranslateY(70);
            pane.setScaleX(1.0);
            pane.setScaleY(1.0);
        });*/

        return pane;
    }

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
            String name = event == null ? "Evento" : event.getNameEvent();
            Label title = new Label(name == null ? "Evento" : name);
            title.getStyleClass().add("event-title");
            Label detail = new Label(event == null ? "" : event.showEvent().toString());
            detail.setWrapText(true);
            VBox.setVgrow(detail, Priority.ALWAYS);
            eventBox.getChildren().addAll(title, detail);
            eventsBox.getChildren().add(eventBox);
        }
    }



    private void updateInstruction(ClientAction action, GameView gameView, String myPlayerId, boolean locked) {
        if (locked) {
            instructionLabel.setText("Comando inviato. Attendi la risposta del server.");
            return;
        }

        String currentPlayer = gameView == null ? null : gameView.getCurrentPlayer();
        switch (action) {
            case PLACE_TOTEM -> instructionLabel.setText("È il tuo turno: scegli un ticket libero sulla Bidding Trail.");
            case PICK_CARD -> instructionLabel.setText("È il tuo turno: scegli una carta evidenziata dalla riga consentita dal tuo ticket.");
            case PICK_SPECIAL -> instructionLabel.setText("Building 13: scegli una carta dalla UPPER ROW.");
            case WAITING_FOR_TURN -> instructionLabel.setText("Attendi il turno di " + safe(currentPlayer) + ".");
            case RESOLVE_EVENTS -> instructionLabel.setText("Risoluzione eventi: leggi il riepilogo, poi attendi il prossimo aggiornamento.");
            case END_GAME -> instructionLabel.setText("Partita terminata.");
            case CRASHED -> instructionLabel.setText("La partita è in stato CRASHED.");
            default -> instructionLabel.setText("Attendi un aggiornamento dello stato.");
        }

        if (myPlayerId == null || myPlayerId.isBlank()) {
            instructionLabel.setText(instructionLabel.getText() + " Nickname locale non ancora noto.");
        }
    }

    private void disableChildrenClicks(HBox box) {
        for (Node child : box.getChildren()) {
            child.setOnMouseClicked(null);
            child.setDisable(true);
        }
    }

    private String safe(Object value) {
        return value == null ? "-" : String.valueOf(value);
    }

    private boolean equalsIgnoreCase(String a, String b) {
        return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
    }

    private enum RowName {
        UPPER,
        LOWER
    }


    private VBox createResizableCardBox(CardView card, ScrollPane pane) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("card-pane");

        Image image = card == null ? null : imageResources.loadCard(card.getId());

        if (image != null) {
            ImageView view = new ImageView(image);

            view.fitHeightProperty().bind(pane.heightProperty().multiply(0.75)); // height of the card is 75% of the panel height
            view.setPreserveRatio(true);

            box.getChildren().add(view);
        } else {
            Label fallback = new Label(CardFormatter.shortLabel(card));
            fallback.setWrapText(true);
            fallback.getStyleClass().add("card-fallback");
            box.getChildren().add(fallback);
        }

        if (card != null) {
            Tooltip.install(pane, new Tooltip(CardFormatter.tooltip(card)));
        }

        return box;
    }
}
