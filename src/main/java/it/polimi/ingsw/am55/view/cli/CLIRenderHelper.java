package it.polimi.ingsw.am55.view.cli;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.dto.BiddingTicketView;
import it.polimi.ingsw.am55.dto.BoardView;
import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.LobbyView;
import it.polimi.ingsw.am55.dto.PlayerView;
import it.polimi.ingsw.am55.dto.endgame.EndGameEffectView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.dto.endgame.LeaderBoardEntryView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;
import it.polimi.ingsw.am55.view.ClientAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Centralizes every textual rendering operation used by the command-line view.
 * <p>
 * {@link CLIView} owns user input and delegates all screen output to this class so
 * that the CLI remains small, readable, and focused on interaction flow.
 */
public class CLIRenderHelper {

    private static final int CARD_WIDTH = 36;
    private static final int CARDS_PER_LINE = 2;
    private static final int TICKET_WIDTH = 32;
    private static final int TICKETS_PER_LINE = 5;

    /**
     * Prints the startup banner shown when the CLI is ready.
     */
    public void printStartup() {
        System.out.println(ConsoleColor.CYAN_BOLD + "AM55 CLI client started." + ConsoleColor.RESET);
        System.out.println(ConsoleColor.YELLOW_BOLD
                + "Lobby state synchronized with the server."
                + ConsoleColor.RESET);
    }

    /**
     * Prints an informational message using the standard CLI prefix.
     *
     * @param message message to show to the player
     */
    public void showInfo(String message) {
        System.out.println(ConsoleColor.GREEN_BOLD + "[INFO] " + ConsoleColor.RESET + safe(message));
    }

    /**
     * Prints an error message using the standard CLI prefix.
     *
     * @param message error message to show to the player
     */
    public void showError(String message) {
        System.out.println(ConsoleColor.RED_BOLD + "[ERROR] " + ConsoleColor.RESET + safe(message));
    }

    /**
     * Prints the commands available in the lobby according to the current lobby snapshot.
     *
     * @param lobbyView latest lobby state, or {@code null} when no game exists yet
     */
    public void printLobbyHelp(LobbyView lobbyView) {
        System.out.println();
        System.out.println(ConsoleColor.YELLOW_BOLD + "========== LOBBY COMMANDS ==========" + ConsoleColor.RESET);

        if (lobbyView == null || lobbyView.getGameState() == null) {
            System.out.println("No game has been created yet.");
            System.out.println("create <nickname> <totemColor> <numPlayers> "
                    + "(TOTEMS: "
                    + ConsoleColor.GREEN_BOLD
                    + "BLUE, ORANGE, PURPLE, YELLOW, WHITE"
                    + ConsoleColor.RESET
                    + ")");
        } else if (lobbyView.getGameState() == GameState.CREATED) {
            System.out.println("A game is already open. You can join it.");
            System.out.println("join <nickname> <totemColor> "
                    + "(AVAILABLE TOTEMS: "
                    + ConsoleColor.GREEN_BOLD
                    + availableTotemsForLobby(lobbyView)
                    + ConsoleColor.RESET
                    + ")");
        } else {
            System.out.println("The game has already started and cannot accept new players.");
        }

        System.out.println("help");
        System.out.println("quit");
        System.out.println(ConsoleColor.YELLOW_BOLD + "====================================" + ConsoleColor.RESET);
    }

    /**
     * Prints a compact help section for the current game interaction.
     *
     * @param action current client-side action
     */
    public void printGameHelp(ClientAction action) {
        System.out.println();
        System.out.println(ConsoleColor.YELLOW_BOLD + "========== GAME COMMANDS ==========" + ConsoleColor.RESET);
        System.out.println("help");
        System.out.println("quit");
        System.out.println("myhand");
        System.out.println("hand <nickname>");

        switch (action) {
            case PLACE_TOTEM -> System.out.println("placeTotem <index>");
            case PICK_CARD -> System.out.println("pickCard <cardId>");
            case PICK_SPECIAL -> System.out.println("pick <cardId>");
            default -> System.out.println("No game command is currently available.");
        }

        System.out.println(ConsoleColor.YELLOW_BOLD + "===================================" + ConsoleColor.RESET);
    }

    /**
     * Prints the action that the player is expected to perform next.
     *
     * @param action current client-side action
     * @param lobbyView latest lobby state
     * @param currentPlayer nickname of the current player, when available
     */
    public void printExpectedAction(ClientAction action, LobbyView lobbyView, String currentPlayer) {
        switch (action) {
            case LOBBY -> printLobbyHelp(lobbyView);
            case WAITING_TO_START -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Available command: quit (or attend other players)."
                    + ConsoleColor.RESET);
            case WAITING_FOR_TURN -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Wait for " + safeCurrentPlayer(currentPlayer) + " to finish the turn. Available commands: myhand | hand <nickname> | quit"
                    + ConsoleColor.RESET);
            case PLACE_TOTEM -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Available commands: placeTotem <index> | myhand | hand <nickname> | quit"
                    + ConsoleColor.RESET);
            case PICK_CARD -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Available commands: pickCard <cardId> | myhand | hand <nickname> | quit"
                    + ConsoleColor.RESET);
            case PICK_SPECIAL -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Available commands: pick <cardId> | myhand | hand <nickname> | quit"
                    + ConsoleColor.RESET);
            case CRASHED -> System.out.println(ConsoleColor.RED_BOLD
                    + "The connection is closed."
                    + ConsoleColor.RESET);
            case RESOLVE_EVENTS, END_GAME, END_GAME_RESOLVE, WAITING_FOR_STATE -> {
                // These states do not require a player command.
            }
        }
    }

    /**
     * Prints the complete game snapshot: metadata, players, board and pending events.
     *
     * @param gameView game state to render
     */
    public void renderGame(GameView gameView) {
        if (gameView == null) {
            showError("No game state is available yet.");
            return;
        }

        System.out.println();
        System.out.println(ConsoleColor.CYAN_BOLD + "========== GAME ==========" + ConsoleColor.RESET);
        System.out.println("Game id: " + gameView.getGameId());
        System.out.println("State: " + gameView.getState());
        System.out.println("Round: " + gameView.getRound());
        System.out.println("Current player: " + safeCurrentPlayer(gameView.getCurrentPlayer()));
        System.out.println();
        renderPlayers(gameView.getPlayers());

        if (gameView.getBoard() != null) {
            printBoard(gameView.getBoard());
        } else {
            showError("Board is not available.");
        }

        System.out.println(ConsoleColor.CYAN_BOLD + "==========================" + ConsoleColor.RESET);
        printResolveEvents(gameView.getResolveEvents());
    }

    /**
     * Prints the shared board using card boxes and bidding ticket boxes.
     *
     * @param boardView board state to render
     */
    public void printBoard(BoardView boardView) {
        System.out.println(renderBoard(boardView));
    }

    /**
     * Prints a player's personal deck.
     *
     * @param cards cards in the deck
     * @param ownerNickname deck owner nickname
     * @param isMyHand whether the deck belongs to the local player
     */
    public void printPersonalDeck(List<CardView> cards, String ownerNickname, boolean isMyHand) {
        System.out.println();

        String title = isMyHand ? "MY HAND" : ownerNickname + "'s HAND";

        if (cards == null || cards.isEmpty()) {
            showError(title + " is empty.");
            return;
        }

        System.out.println(renderCardRow(title, cards, ConsoleColor.GREEN_BOLD));
        System.out.println(ConsoleColor.GREEN_BOLD + "=============================" + ConsoleColor.RESET);
    }

    /**
     * Prints a player's deck by nickname, using the latest game snapshot.
     *
     * @param gameView latest game state
     * @param currentState current client-side action
     * @param targetNickname nickname whose hand must be displayed
     * @param myNickname local player nickname
     */
    public void printHand(GameView gameView, ClientAction currentState, String targetNickname, String myNickname) {
        if (currentState == ClientAction.LOBBY
                || currentState == ClientAction.WAITING_TO_START
                || gameView == null) {
            showError("You cannot inspect hands before the game starts.");
            return;
        }

        PlayerView targetPlayer = findPlayer(gameView, targetNickname);

        if (targetPlayer == null) {
            showError("Player not found: " + targetNickname);
            return;
        }

        boolean isMyHand = targetPlayer.getNickname().equalsIgnoreCase(myNickname);
        printPersonalDeck(targetPlayer.getMyHand(), targetPlayer.getNickname(), isMyHand);
    }

    /**
     * Prints final events, end-game effects, database leaderboard and winners.
     *
     * @param result final result DTO sent by the server
     */
    public void printEndGameResult(EndGameResultView result) {
        if (result == null) {
            return;
        }

        printFinalResolvedEvents(result);
        printEndGameEffects(result);
        printLeaderboard(result);
        printWinners(result);
    }

    /**
     * Returns the lobby totems that are not currently taken.
     *
     * @param lobbyView current lobby state
     * @return comma-separated list of available totems
     */
    public String availableTotemsForLobby(LobbyView lobbyView) {
        List<String> colors = new ArrayList<>(List.of("BLUE", "ORANGE", "PURPLE", "YELLOW", "WHITE"));

        if (lobbyView == null || lobbyView.getChosenTotems() == null) {
            return String.join(", ", colors);
        }

        for (String chosenTotem : lobbyView.getChosenTotems()) {
            colors.removeIf(color -> color.equalsIgnoreCase(chosenTotem));
        }

        if (colors.isEmpty()) {
            return "no available totem";
        }

        return String.join(", ", colors);
    }

    /**
     * Prints the players panel for the current game.
     */
    private void renderPlayers(List<PlayerView> players) {
        System.out.println(ConsoleColor.YELLOW_BOLD + "Players:" + ConsoleColor.RESET);

        if (players == null || players.isEmpty()) {
            System.out.println("- No players available");
            return;
        }

        for (PlayerView player : players) {
            if (player == null) {
                continue;
            }

            System.out.println("- "
                    + ConsoleColor.totemColor(player.getTotemColor())
                    + player.getNickname()
                    + ConsoleColor.RESET
                    + " | Totem: " + player.getTotemColor()
                    + " | Food: " + player.getFood()
                    + " | Points: " + player.getPoints());
        }
    }

    /**
     * Builds the complete board string before printing it.
     */
    private String renderBoard(BoardView boardView) {
        if (boardView == null) {
            return ConsoleColor.RED_BOLD + "Board is not available." + ConsoleColor.RESET;
        }

        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append(ConsoleColor.CYAN_BOLD);
        sb.append("╔════════════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                                  BOARD                                     ║\n");
        sb.append("╚════════════════════════════════════════════════════════════════════════════╝\n");
        sb.append(ConsoleColor.RESET);
        sb.append(renderTurnTicket(boardView.getTurnTicket())).append('\n');
        sb.append(renderCardRow("UPPER ROW", boardView.getUpperRow(), ConsoleColor.BLUE_BOLD)).append('\n');
        sb.append(renderBiddingTrail(boardView.getBiddingTrail())).append('\n');
        sb.append(renderCardRow("LOWER ROW", boardView.getLowerRow(), ConsoleColor.GREEN_BOLD)).append('\n');
        return sb.toString();
    }

    /**
     * Renders the turn ticket containing the second-phase player order.
     */
    private String renderTurnTicket(List<PlayerView> turnTicket) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColor.YELLOW_BOLD);
        sb.append("╔════════════════════════════ TURN TICKET ═════════════════════════════════╗\n");
        sb.append(ConsoleColor.RESET);

        if (turnTicket == null || turnTicket.isEmpty()) {
            sb.append("║ ").append(padRight("[ empty ]", 70)).append(" ║\n");
        } else {
            sb.append("║ ");
            for (int i = 0; i < turnTicket.size(); i++) {
                PlayerView player = turnTicket.get(i);
                if (player == null) {
                    sb.append(ConsoleColor.BLACK_BRIGHT).append("[").append(i).append(": empty]").append(ConsoleColor.RESET);
                } else {
                    sb.append("[").append(i).append(": ")
                            .append(ConsoleColor.totemColor(player.getTotemColor()))
                            .append(player.getNickname()).append(" / ").append(player.getTotemColor())
                            .append(ConsoleColor.RESET).append("]");
                }
                if (i < turnTicket.size() - 1) {
                    sb.append("  ");
                }
            }
            sb.append('\n');
        }

        sb.append(ConsoleColor.YELLOW_BOLD);
        sb.append("╚═══════════════════════════════════════════════════════════════════════════╝\n");
        sb.append(ConsoleColor.RESET);
        return sb.toString();
    }

    /**
     * Renders a horizontal card row split across multiple terminal lines.
     */
    private String renderCardRow(String title, List<CardView> cards, String color) {
        StringBuilder sb = new StringBuilder();
        sb.append(color).append("╔════════════════════════════ ").append(title).append(" ═════════════════════════════╗\n")
                .append(ConsoleColor.RESET);

        if (cards == null || cards.isEmpty()) {
            sb.append("  ").append(ConsoleColor.BLACK_BRIGHT).append("[ empty row ]").append(ConsoleColor.RESET).append('\n');
            sb.append(color).append("╚═══════════════════════════════════════════════════════════════════════════╝\n")
                    .append(ConsoleColor.RESET);
            return sb.toString();
        }

        List<String[]> boxes = new ArrayList<>();
        for (CardView card : cards) {
            boxes.add(renderCardBox(card));
        }

        appendBoxRows(sb, boxes, CARDS_PER_LINE, CARD_WIDTH);
        sb.append(color).append("╚═══════════════════════════════════════════════════════════════════════════╝\n")
                .append(ConsoleColor.RESET);
        return sb.toString();
    }

    /**
     * Renders one card as an ASCII box.
     */
    private String[] renderCardBox(CardView card) {
        List<String> lines = new ArrayList<>();

        String category = card == null ? "Unknown" : getCardCategory(card);
        String header = category + " #" + (card == null ? "-" : card.getId());
        String borderColor = card == null ? ConsoleColor.WHITE_BRIGHT : getCardColor(card);

        lines.add(borderColor + "┌" + repeat("─", CARD_WIDTH) + "┐" + ConsoleColor.RESET);
        lines.add(borderColor + "│" + ConsoleColor.RESET + center(header, CARD_WIDTH) + borderColor + "│" + ConsoleColor.RESET);
        lines.add(borderColor + "├" + repeat("─", CARD_WIDTH) + "┤" + ConsoleColor.RESET);
        lines.add(borderColor + "│" + ConsoleColor.RESET + " " + padRight("Era: " + (card == null ? "-" : card.getEra()), CARD_WIDTH - 1) + borderColor + "│" + ConsoleColor.RESET);
        addCardDetails(lines, card, borderColor);
        lines.add(borderColor + "└" + repeat("─", CARD_WIDTH) + "┘" + ConsoleColor.RESET);

        return lines.toArray(new String[0]);
    }

    /**
     * Adds all card-specific details to an ASCII card box.
     */
    private void addCardDetails(List<String> lines, CardView card, String borderColor) {
        if (card == null || card.getCliCardInfo() == null || card.getCliCardInfo().details() == null) {
            appendDetail(lines, borderColor, "Type", "Unknown");
            return;
        }

        for (CliCardDetails detail : card.getCliCardInfo().details()) {
            appendDetail(lines, borderColor, detail.label(), detail.value());
        }
    }

    /**
     * Renders all bidding tickets currently visible on the board.
     */
    private String renderBiddingTrail(List<BiddingTicketView> biddingTrail) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColor.PURPLE_BOLD);
        sb.append("╔════════════════════════════ BIDDING TRAIL ═══════════════════════════════╗\n");
        sb.append(ConsoleColor.RESET);

        if (biddingTrail == null || biddingTrail.isEmpty()) {
            sb.append("  ").append(ConsoleColor.BLACK_BRIGHT).append("[ empty bidding trail ]").append(ConsoleColor.RESET).append('\n');
            sb.append(ConsoleColor.PURPLE_BOLD);
            sb.append("╚═══════════════════════════════════════════════════════════════════════════╝\n");
            sb.append(ConsoleColor.RESET);
            return sb.toString();
        }

        List<String[]> ticketBoxes = new ArrayList<>();
        for (BiddingTicketView ticket : biddingTrail) {
            ticketBoxes.add(renderBiddingTicketBox(ticket));
        }

        appendBoxRows(sb, ticketBoxes, TICKETS_PER_LINE, TICKET_WIDTH);
        sb.append(ConsoleColor.PURPLE_BOLD);
        sb.append("╚═══════════════════════════════════════════════════════════════════════════╝\n");
        sb.append(ConsoleColor.RESET);
        return sb.toString();
    }

    /**
     * Renders a single bidding ticket as an ASCII box.
     */
    private String[] renderBiddingTicketBox(BiddingTicketView ticket) {
        List<String> lines = new ArrayList<>();
        String borderColor = ConsoleColor.PURPLE_BRIGHT;
        String title = ticket == null ? "Ticket" : "Ticket " + ticket.getTrailPlacement();
        String effect = ticket == null ? "No effect" : renderTicketEffectPlain(ticket);
        String playerBox = ticket == null ? "[ empty ]" : renderTicketPlayerBoxPlain(ticket);

        lines.add(borderColor + "┌" + repeat("─", TICKET_WIDTH) + "┐" + ConsoleColor.RESET);
        lines.add(borderColor + "│" + ConsoleColor.RESET + center(title, TICKET_WIDTH) + borderColor + "│" + ConsoleColor.RESET);
        lines.add(borderColor + "├" + repeat("─", TICKET_WIDTH) + "┤" + ConsoleColor.RESET);
        lines.add(borderColor + "│" + ConsoleColor.RESET + " " + padRight("Effect: " + effect, TICKET_WIDTH - 1) + borderColor + "│" + ConsoleColor.RESET);
        lines.add(borderColor + "│" + ConsoleColor.RESET + " " + padRight("Totem: " + playerBox, TICKET_WIDTH - 1) + borderColor + "│" + ConsoleColor.RESET);
        lines.add(borderColor + "└" + repeat("─", TICKET_WIDTH) + "┘" + ConsoleColor.RESET);
        return lines.toArray(new String[0]);
    }

    /**
     * Renders the effect text printed inside a bidding ticket box.
     */
    private String renderTicketEffectPlain(BiddingTicketView ticket) {
        if (ticket.getFoodBonus() > 0) {
            return "+" + ticket.getFoodBonus() + " FOOD";
        }

        StringBuilder sb = new StringBuilder();
        if (ticket.getChooseLowerCard() > 0) {
            sb.append("↓ Lower x").append(ticket.getChooseLowerCard());
        }
        if (ticket.getChooseUpperCard() > 0) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }
            sb.append("↑ Upper x").append(ticket.getChooseUpperCard());
        }
        return sb.isEmpty() ? "No effect" : sb.toString();
    }

    /**
     * Renders the player currently occupying a bidding ticket.
     */
    private String renderTicketPlayerBoxPlain(BiddingTicketView ticket) {
        if (ticket.getPlayer() == null) {
            return "[ empty ]";
        }

        PlayerView player = ticket.getPlayer();
        return player.getNickname() + " / " + player.getTotemColor();
    }

    /**
     * Appends a detail line to an ASCII box, wrapping long text safely.
     */
    private void appendDetail(List<String> lines, String borderColor, String label, String value) {
        String separator = "Info".equals(label) ? " - " : ": ";
        String[] parts = safe(value).split("\\R");

        for (int i = 0; i < parts.length; i++) {
            String prefix = i == 0 ? label + separator : repeat(" ", label.length() + separator.length());
            List<String> wrapped = wrap(prefix + parts[i], CARD_WIDTH - 2);

            for (String line : wrapped) {
                lines.add(borderColor + "│" + ConsoleColor.RESET
                        + " " + padRight(line, CARD_WIDTH - 1)
                        + borderColor + "│" + ConsoleColor.RESET);
            }
        }
    }

    /**
     * Appends a collection of ASCII boxes in row groups.
     */
    private void appendBoxRows(StringBuilder sb, List<String[]> boxes, int boxesPerLine, int boxWidth) {
        for (int i = 0; i < boxes.size(); i += boxesPerLine) {
            int end = Math.min(i + boxesPerLine, boxes.size());
            List<String[]> group = boxes.subList(i, end);
            int maxLines = maxLines(group);

            for (int line = 0; line < maxLines; line++) {
                sb.append("  ");
                for (String[] box : group) {
                    sb.append(line < box.length ? box[line] : repeat(" ", boxWidth + 2));
                    sb.append("  ");
                }
                sb.append('\n');
            }
            sb.append('\n');
        }
    }

    /**
     * Prints event resolution entries available in a game or end-game result.
     */
    private void printResolveEvents(List<ResolveEventView> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        System.out.println(ConsoleColor.CYAN_BOLD + "RESOLVED EVENTS" + ConsoleColor.RESET);
        for (ResolveEventView view : events) {
            if (view == null) {
                continue;
            }
            System.out.println(ConsoleColor.RED_BOLD + view.getNameEvent() + ConsoleColor.RESET);
            System.out.println(view.showEvent());
            System.out.println();
        }
    }

    /**
     * Prints the events resolved during the final game resolution.
     */
    private void printFinalResolvedEvents(EndGameResultView result) {
        if (result.getResolvedEvents() == null || result.getResolvedEvents().isEmpty()) {
            return;
        }

        System.out.println(ConsoleColor.CYAN_BOLD + "========== RESOLVED FINAL EVENTS ==========" + ConsoleColor.RESET);
        printResolveEvents(result.getResolvedEvents());
    }

    /**
     * Prints final scoring effects.
     */
    private void printEndGameEffects(EndGameResultView result) {
        if (result.getEndGameEffects() == null || result.getEndGameEffects().isEmpty()) {
            return;
        }

        System.out.println(ConsoleColor.PURPLE_BOLD + "========== FINAL EFFECTS ==========" + ConsoleColor.RESET);
        for (EndGameEffectView effect : result.getEndGameEffects()) {
            if (effect == null) {
                continue;
            }
            String sign = effect.getPointDelta() >= 0 ? "+" : "";
            System.out.println(effect.getPlayerNickname()
                    + ": " + effect.getDescription()
                    + " (" + sign + effect.getPointDelta() + " PP)");
        }
        System.out.println();
    }

    /**
     * Prints the database leaderboard included in the final result.
     */
    private void printLeaderboard(EndGameResultView result) {
        if (result.getLeaderBoard() == null || result.getLeaderBoard().isEmpty()) {
            return;
        }

        System.out.println(ConsoleColor.CYAN_BOLD + "========== GLOBAL LEADERBOARD ==========" + ConsoleColor.RESET);
        System.out.printf("%-8s %-25s %-8s %-8s %-25s%n", "Pos.", "Nickname", "PP", "Food", "Date");

        for (LeaderBoardEntryView entry : result.getLeaderBoard()) {
            if (entry == null) {
                continue;
            }

            System.out.printf("%-8d %-25s %-8d %-8d %-25s%n",
                    entry.getPosition(),
                    entry.getPlayerNickname(),
                    entry.getPrestigePoint(),
                    entry.getFoodPoint(),
                    entry.getDate());
        }

        System.out.println(ConsoleColor.CYAN_BOLD + "========================================" + ConsoleColor.RESET);
        System.out.println();
    }

    /**
     * Prints the winner section of the final result.
     */
    private void printWinners(EndGameResultView result) {
        if (result.getWinners() == null || result.getWinners().isEmpty()) {
            return;
        }

        System.out.println(ConsoleColor.YELLOW_BOLD + "========== WINNER(S) ==========" + ConsoleColor.RESET);
        for (Map.Entry<String, Integer> winner : result.getWinners().entrySet()) {
            System.out.println(winner.getKey() + " = " + winner.getValue() + " PP");
        }
        System.out.println(ConsoleColor.YELLOW_BOLD + "===============================" + ConsoleColor.RESET);
    }

    /**
     * Finds a player by nickname in the given game snapshot.
     */
    private PlayerView findPlayer(GameView gameView, String nickname) {
        if (gameView.getPlayers() == null || nickname == null) {
            return null;
        }

        for (PlayerView player : gameView.getPlayers()) {
            if (player != null && player.getNickname().equalsIgnoreCase(nickname)) {
                return player;
            }
        }

        return null;
    }

    /**
     * Returns the category provided by the card-specific CLI metadata.
     */
    private String getCardCategory(CardView card) {
        CliCardInfo info = card.getCliCardInfo();
        return info == null || info.category() == null ? "Card" : info.category();
    }

    /**
     * Returns the ANSI color provided by the card-specific CLI metadata.
     */
    private String getCardColor(CardView card) {
        CliCardInfo info = card.getCliCardInfo();
        return info == null || info.Color() == null ? ConsoleColor.WHITE_BRIGHT : info.Color();
    }

    /**
     * Returns the largest number of text lines in a group of ASCII boxes.
     */
    private int maxLines(List<String[]> boxes) {
        int max = 0;
        for (String[] box : boxes) {
            max = Math.max(max, box.length);
        }
        return max;
    }

    /**
     * Returns a printable value for nullable strings.
     */
    private String safe(String value) {
        return value == null ? "" : value;
    }

    /**
     * Returns a printable current player name.
     */
    private String safeCurrentPlayer(String currentPlayer) {
        return currentPlayer == null || currentPlayer.isBlank() ? "unknown" : currentPlayer;
    }

    /**
     * Repeats a string a fixed number of times.
     */
    private String repeat(String value, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * Pads text on the right and trims it when it is too long.
     */
    private String padRight(String text, int width) {
        if (text == null) {
            text = "";
        }
        if (text.length() > width) {
            return text.substring(0, width);
        }
        return text + repeat(" ", width - text.length());
    }

    /**
     * Centers text in a fixed-width area.
     */
    private String center(String text, int width) {
        if (text == null) {
            text = "";
        }
        if (text.length() > width) {
            return text.substring(0, width);
        }
        int left = (width - text.length()) / 2;
        int right = width - text.length() - left;
        return repeat(" ", left) + text + repeat(" ", right);
    }

    /**
     * Wraps a long string to terminal-friendly line widths.
     */
    private List<String> wrap(String text, int width) {
        List<String> result = new ArrayList<>();
        if (text == null) {
            result.add("");
            return result;
        }

        String remaining = text;
        while (remaining.length() > width) {
            int breakingPoint = remaining.lastIndexOf(" ", width);
            if (breakingPoint <= 0) {
                breakingPoint = width;
            }
            result.add(remaining.substring(0, breakingPoint).trim());
            remaining = remaining.substring(breakingPoint).trim();
        }
        result.add(remaining);
        return result;
    }
}
