package it.polimi.ingsw.am55.view.cli;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.LobbyView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.view.ClientAction;
import it.polimi.ingsw.am55.view.ClientActionResolver;
import it.polimi.ingsw.am55.view.ClientModelObserver;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Command-line implementation of the client view.
 * <p>
 * This class owns command parsing and command dispatching only. Every rendering
 * operation is delegated to {@link CLIRenderHelper}. The input loop is demand-driven:
 * the {@code >} prompt is shown only when the current client state accepts user input,
 * preventing a permanently active prompt from overlapping asynchronous server updates.
 */
public class CLIView implements ClientModelObserver {

    private static final int EVENT_RESOLUTION_DELAY_SECONDS = 4;

    private final ClientModel model;
    private final Scanner input;
    private final CLIRenderHelper renderer;
    private final ClientActionResolver actionResolver;
    private final ScheduledExecutorService scheduler;
    private final Semaphore inputRequests;
    private final AtomicBoolean promptQueued;
    private final Object outputLock;

    private UserActionHandler actionHandler;
    private volatile GameView currentGameView;
    private volatile String currentInfoMessage;
    private volatile String currentErrorMessage;
    private volatile boolean waitingServerResponse;
    private volatile String id;
    private volatile boolean inLobby;
    private volatile LobbyView currentLobbyView;
    private volatile boolean pendingEventResolutionDelay;

    /**
     * Builds the CLI view around the shared client model.
     *
     * @param model model observed by this view
     */
    public CLIView(ClientModel model) {
        this.model = Objects.requireNonNull(model, "model");
        this.input = new Scanner(System.in);
        this.renderer = new CLIRenderHelper();
        this.actionResolver = new ClientActionResolver();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("CLI-Scheduler-Thread");
            thread.setDaemon(true);
            return thread;
        });
        this.inputRequests = new Semaphore(0);
        this.promptQueued = new AtomicBoolean(false);
        this.outputLock = new Object();
        this.inLobby = true;
    }

    /**
     * Connects the view to the controller used to send commands to the server.
     *
     * @param actionHandler controller-facing command handler
     */
    public void setActionHandler(UserActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    /**
     * Starts the CLI and its demand-driven input loop.
     */
    public void start() {
        withOutput(renderer::printStartup);

        Thread inputThread = new Thread(this::inputLoop);
        inputThread.setName("CLI-Input-Thread");
        inputThread.setDaemon(true);
        inputThread.start();

        requestInputIfUseful();
    }

    /**
     * Waits until the view explicitly needs a command, prints one prompt, and handles one line.
     */
    private void inputLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                inputRequests.acquire();
                printPrompt();

                String line = input.nextLine().trim();
                promptQueued.set(false);

                if (line.isEmpty()) {
                    requestInputIfUseful();
                    continue;
                }

                handleCommand(line);
            } catch (IllegalStateException e) {
                return;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /**
     * Prints the CLI prompt in a synchronized console section.
     */
    private void printPrompt() {
        synchronized (outputLock) {
            System.out.print("> ");
            System.out.flush();
        }
    }

    /**
     * Parses and dispatches a single command typed by the user.
     *
     * @param line command line entered by the player
     */
    private void handleCommand(String line) {
        String[] parts = line.split("\\s+");
        String command = parts[0].toLowerCase();

        if (command.equals("quit") || command.equals("exit")) {
            if (!inLobby) {
                askQuitGame(id);
            } else {
                askQuitLobby();
            }
            return;
        }

        ClientAction action = resolveCurrentAction();

        if (command.equals("help")) {
            printHelp(action);
            requestInputIfUseful();
            return;
        }

        if (command.equals("myhand") || command.equals("hand")) {
            handleHandCommand(command, action, parts);
            requestInputIfUseful();
            return;
        }

        if (waitingServerResponse) {
            withOutput(() -> renderer.showInfo("Waiting for the server response. You can still type 'help' or 'quit'."));
            requestInputIfUseful();
            return;
        }

        switch (action) {
            case LOBBY -> handleLobbyCommand(command, parts);
            case PLACE_TOTEM -> handlePlaceTotemCommand(command, parts);
            case PICK_CARD -> handlePickCardCommand(command, parts);
            case PICK_SPECIAL -> handlePickSpecialCommand(command, parts);
            case WAITING_TO_START -> withOutput(() ->
                    renderer.showInfo("You are waiting for the game to start. Type 'quit' to leave."));
            case WAITING_FOR_TURN -> withOutput(() ->
                    renderer.showInfo("It is not your turn. Available commands: myhand, hand <nickname>, quit."));
            case RESOLVE_EVENTS -> withOutput(() -> renderer.showInfo("The game is resolving events. Wait for the next update."));
            case END_GAME, END_GAME_RESOLVE -> withOutput(() -> renderer.showInfo("The game has ended."));
            case CRASHED -> withOutput(() -> renderer.showError("The game is closed. No commands are available."));
            case WAITING_FOR_STATE -> withOutput(() -> renderer.showInfo("No action is available yet. Wait for a state update."));
        }

        requestInputIfUseful();
    }

    /**
     * Handles the optional commands used to inspect a player's hand.
     *
     * @param command command name typed by the user
     * @param currentState current client-side state
     * @param parts tokenized command line
     */
    private void handleHandCommand(String command, ClientAction currentState, String[] parts) {
        String targetNickname;

        if ("myhand".equals(command)) {
            if (parts.length != 1) {
                withOutput(() -> renderer.showError("Usage: myhand"));
                return;
            }
            targetNickname = id;
        } else {
            if (parts.length != 2) {
                withOutput(() -> renderer.showError("Usage: hand <nickname>"));
                return;
            }
            targetNickname = parts[1].trim();
        }

        if (targetNickname == null || targetNickname.isBlank()) {
            withOutput(() -> renderer.showError("Player nickname is not available yet."));
            return;
        }

        withOutput(() -> renderer.printHand(currentGameView, currentState, targetNickname, id));
    }

    /**
     * Dispatches lobby commands to create or join a game.
     */
    private void handleLobbyCommand(String command, String[] parts) {
        switch (command) {
            case "create" -> {
                if (currentLobbyView != null && currentLobbyView.getGameState() != null) {
                    withOutput(() -> {
                        renderer.showError("A game already exists. Use join.");
                        renderer.printLobbyHelp(currentLobbyView);
                    });
                    return;
                }
                handleCreateCommand(parts);
            }
            case "join" -> {
                if (currentLobbyView == null || currentLobbyView.getGameState() == null) {
                    withOutput(() -> {
                        renderer.showError("No game has been created yet. Use create.");
                        renderer.printLobbyHelp(currentLobbyView);
                    });
                    return;
                }

                if (currentLobbyView.getGameState() != GameState.CREATED) {
                    withOutput(() -> {
                        renderer.showError("The game does not accept new players.");
                        renderer.printLobbyHelp(currentLobbyView);
                    });
                    return;
                }
                handleJoinCommand(parts);
            }
            default -> withOutput(() -> {
                renderer.showError("Invalid lobby command.");
                renderer.printLobbyHelp(currentLobbyView);
            });
        }
    }

    /**
     * Validates and sends the create-game command.
     */
    private void handleCreateCommand(String[] parts) {
        if (parts.length != 4) {
            withOutput(() -> renderer.showError("Usage: create <nickname> <totemColor> <numPlayers>"));
            return;
        }

        Integer numPlayers = parseInt(parts[3]);
        if (numPlayers == null) {
            withOutput(() -> renderer.showError("The number of players must be an integer."));
            return;
        }

        askCreateGame(parts[1].trim(), parts[2].trim(), numPlayers);
    }

    /**
     * Validates and sends the join-game command.
     */
    private void handleJoinCommand(String[] parts) {
        if (parts.length != 3) {
            withOutput(() -> renderer.showError("Usage: join <nickname> <totemColor>"));
            return;
        }

        askJoinGame(parts[1].trim(), parts[2].trim());
    }

    /**
     * Validates and sends the place-totem command.
     */
    private void handlePlaceTotemCommand(String command, String[] parts) {
        if (!command.equals("placetotem")) {
            printExpectedAction();
            return;
        }

        if (parts.length != 2) {
            withOutput(() -> renderer.showError("Usage: placeTotem <index>"));
            return;
        }

        Integer index = parseInt(parts[1]);
        if (index == null) {
            withOutput(() -> renderer.showError("The index must be an integer."));
            return;
        }

        askPlaceTotem(index);
    }

    /**
     * Validates and sends the standard pick-card command.
     */
    private void handlePickCardCommand(String command, String[] parts) {
        if (!command.equals("pickcard")) {
            printExpectedAction();
            return;
        }

        if (parts.length != 2) {
            withOutput(() -> renderer.showError("Usage: pickCard <cardId>"));
            return;
        }

        Integer cardId = parseInt(parts[1]);
        if (cardId == null) {
            withOutput(() -> renderer.showError("The card id must be an integer."));
            return;
        }

        askPickCard(cardId);
    }

    /**
     * Validates and sends the special pick command.
     */
    private void handlePickSpecialCommand(String command, String[] parts) {
        if (!command.equals("pick")) {
            printExpectedAction();
            return;
        }

        if (parts.length != 2) {
            withOutput(() -> renderer.showError("Usage: pick <cardId>"));
            return;
        }

        Integer cardId = parseInt(parts[1]);
        if (cardId == null) {
            withOutput(() -> renderer.showError("The card id must be an integer."));
            return;
        }

        askPickSpecial(cardId);
    }

    /**
     * Sends a create-game action to the controller.
     */
    public void askCreateGame(String playerId, String totemColor, int numPlayers) {
        if (!ensureActionHandler()) {
            requestInputIfUseful();
            return;
        }

        this.id = playerId.trim();
        this.waitingServerResponse = true;
        actionHandler.onCreateGameSelected(this.id, totemColor, numPlayers);
    }

    /**
     * Sends a join-game action to the controller.
     */
    public void askJoinGame(String playerId, String totemColor) {
        if (!ensureActionHandler()) {
            requestInputIfUseful();
            return;
        }

        this.id = playerId.trim();
        this.waitingServerResponse = true;
        actionHandler.onJoinGameSelected(this.id, totemColor);
    }

    /**
     * Sends a totem-placement action to the controller.
     */
    public void askPlaceTotem(int index) {
        if (!ensureActionHandler()) {
            requestInputIfUseful();
            return;
        }

        this.waitingServerResponse = true;
        actionHandler.onPlaceTotemSelected(this.id, index);
    }

    /**
     * Sends a standard card-pick action to the controller.
     */
    public void askPickCard(int cardId) {
        if (!ensureActionHandler()) {
            requestInputIfUseful();
            return;
        }

        this.waitingServerResponse = true;
        actionHandler.onPickCardSelected(this.id, cardId);
    }

    /**
     * Sends a special card-pick action to the controller.
     */
    public void askPickSpecial(int cardId) {
        if (!ensureActionHandler()) {
            requestInputIfUseful();
            return;
        }

        this.waitingServerResponse = true;
        actionHandler.onPickSpecialSelected(this.id, cardId);
    }

    /**
     * Sends a lobby quit request to the controller.
     */
    public void askQuitLobby() {
        if (actionHandler == null) {
            withOutput(() -> renderer.showError("Action handler is not configured."));
            requestInputIfUseful();
            return;
        }

        this.waitingServerResponse = true;
        withOutput(() -> renderer.showInfo("Exit request sent to the server..."));
        actionHandler.onQuitSelectedLobby();
    }

    /**
     * Sends a game quit request to the controller.
     */
    public void askQuitGame(String playerId) {
        if (actionHandler == null || playerId == null) {
            withOutput(() -> renderer.showError("Cannot quit the game because the player is unknown."));
            requestInputIfUseful();
            return;
        }

        this.waitingServerResponse = true;
        withOutput(() -> renderer.showInfo("Exit request sent to the server..."));
        actionHandler.onQuitGameSelected(playerId.trim());
    }

    /**
     * Reacts to model updates by rendering the newest state and enabling input only when useful.
     */
    @Override
    public void onModelChanged(ClientModel updatedModel) {
        this.waitingServerResponse = false;
        this.currentErrorMessage = updatedModel.getLastError();
        this.currentInfoMessage = updatedModel.getStateRequest();
        this.currentGameView = updatedModel.getGameView();
        this.inLobby = updatedModel.isInLobby();
        this.currentLobbyView = updatedModel.getLobbyView();

        EndGameResultView endGameResultView = updatedModel.getEndGameResultView();
        boolean gameViewUpdated = updatedModel.isLastMessageUpdatedGameView();
        ClientAction action = resolveCurrentAction();

        withOutput(System.out::println);

        if (currentErrorMessage != null) {
            withOutput(() -> {
                renderer.showError(currentErrorMessage);
                printExpectedActionUnsafe();
            });
            requestInputIfUseful();
            return;
        }

        if (action == ClientAction.CRASHED) {
            withOutput(() -> renderer.showInfo(nonBlankOrDefault(currentInfoMessage, "The connection is closed.")));
            model.removeObserver(this);
            return;
        }

        if (action == ClientAction.LOBBY) {
            renderLobbyUpdate();
            requestInputIfUseful();
            return;
        }

        if (action == ClientAction.RESOLVE_EVENTS && gameViewUpdated) {
            withOutput(() -> renderer.showInfo("Event resolution is starting..."));
            pendingEventResolutionDelay = true;
            return;
        }

        if (action == ClientAction.END_GAME_RESOLVE && gameViewUpdated) {
            withOutput(() -> renderer.showInfo("The game has ended. Final results are being prepared..."));
            pendingEventResolutionDelay = true;
            return;
        }

        if (currentInfoMessage != null && !pendingEventResolutionDelay) {
            withOutput(() -> renderer.showInfo(currentInfoMessage));
        }

        if (endGameResultView != null) {
            renderEndGame(endGameResultView);
            return;
        }

        if (gameViewUpdated && currentGameView != null) {
            renderGameWithOptionalDelay(currentGameView, currentInfoMessage);
            return;
        }

        if (currentGameView != null && action == ClientAction.END_GAME) {
            withOutput(() -> renderer.showInfo("The game has ended."));
            model.removeObserver(this);
            return;
        }

        printExpectedAction();
        requestInputIfUseful();
    }

    /**
     * Renders a lobby update and triggers the legacy auto-quit flow for already-started lobbies.
     */
    private void renderLobbyUpdate() {
        withOutput(() -> {
            if (currentInfoMessage != null && !currentInfoMessage.isBlank()) {
                renderer.showInfo(currentInfoMessage);
            }
            printExpectedActionUnsafe();
        });

        if (isLobbyGameAlreadyStarted() && !waitingServerResponse && actionHandler != null) {
            waitingServerResponse = true;
            scheduler.execute(() -> {
                try {
                    actionHandler.onQuitSelectedLobby();
                } catch (RuntimeException e) {
                    waitingServerResponse = false;
                    withOutput(() -> renderer.showError("Error while leaving the lobby: " + e.getMessage()));
                }
            });
        }
    }

    /**
     * Renders final results, optionally after the event-resolution delay.
     */
    private void renderEndGame(EndGameResultView result) {
        GameView snapshot = currentGameView;

        if (pendingEventResolutionDelay) {
            pendingEventResolutionDelay = false;
            scheduler.schedule(() -> {
                withOutput(() -> {
                    if (snapshot != null) {
                        renderer.renderGame(snapshot);
                    }
                    renderer.printEndGameResult(result);
                    printExpectedActionUnsafe();
                });
                requestInputIfUseful();
            }, EVENT_RESOLUTION_DELAY_SECONDS, TimeUnit.SECONDS);
            return;
        }

        withOutput(() -> {
            if (snapshot != null) {
                renderer.renderGame(snapshot);
            }
            renderer.printEndGameResult(result);
            printExpectedActionUnsafe();
        });
        requestInputIfUseful();
    }

    /**
     * Renders game updates immediately or after the configured event-resolution delay.
     */
    private void renderGameWithOptionalDelay(GameView gameView, String infoMessage) {
        if (pendingEventResolutionDelay) {
            pendingEventResolutionDelay = false;
            scheduler.schedule(() -> {
                withOutput(() -> {
                    if (infoMessage != null && !infoMessage.isBlank()) {
                        renderer.showInfo(infoMessage);
                    }
                    renderer.renderGame(gameView);
                    printExpectedActionUnsafe();
                });
                requestInputIfUseful();
            }, EVENT_RESOLUTION_DELAY_SECONDS, TimeUnit.SECONDS);
            return;
        }

        withOutput(() -> {
            renderer.renderGame(gameView);
            printExpectedActionUnsafe();
        });
        requestInputIfUseful();
    }

    /**
     * Prints the correct help section for the current state.
     */
    private void printHelp(ClientAction action) {
        withOutput(() -> {
            if (action == ClientAction.LOBBY) {
                renderer.printLobbyHelp(currentLobbyView);
            } else {
                renderer.printGameHelp(action);
                printExpectedActionUnsafe();
            }
        });
    }

    /**
     * Prints the currently expected action in a synchronized output block.
     */
    private void printExpectedAction() {
        withOutput(this::printExpectedActionUnsafe);
    }

    /**
     * Prints the currently expected action. Caller must hold output synchronization.
     */
    private void printExpectedActionUnsafe() {
        renderer.printExpectedAction(resolveCurrentAction(), currentLobbyView, safeCurrentPlayer());
    }

    /**
     * Requests exactly one prompt when the current state accepts at least one command.
     * <p>
     * The prompt is intentionally not blocked by {@code waitingServerResponse}: global
     * commands such as {@code help} and {@code quit} must remain available while the
     * client is waiting for an acknowledgement from the server. State-changing commands
     * are still rejected in {@link #handleCommand(String)} until the response arrives.
     */
    private void requestInputIfUseful() {
        if (!isInputUseful(resolveCurrentAction())) {
            return;
        }

        if (promptQueued.compareAndSet(false, true)) {
            inputRequests.release();
        }
    }

    /**
     * Determines whether the current state should show a prompt.
     * <p>
     * Waiting states are interactive too: the user may not be allowed to play an action,
     * but must still be able to leave the game or inspect the available global commands.
     */
    private boolean isInputUseful(ClientAction action) {
        return action == ClientAction.LOBBY
                || action == ClientAction.WAITING_TO_START
                || action == ClientAction.WAITING_FOR_TURN
                || action == ClientAction.PLACE_TOTEM
                || action == ClientAction.PICK_CARD
                || action == ClientAction.PICK_SPECIAL;
    }

    /**
     * Resolves the current client action from the latest local state.
     */
    private ClientAction resolveCurrentAction() {
        return actionResolver.resolve(currentGameView, id, inLobby, model.isGameCrashed());
    }

    /**
     * Checks whether the lobby refers to a game that has already left the CREATED state.
     */
    private boolean isLobbyGameAlreadyStarted() {
        return currentLobbyView != null
                && currentLobbyView.getGameState() != null
                && currentLobbyView.getGameState() != GameState.CREATED;
    }

    /**
     * Verifies that the controller dependency is configured.
     */
    private boolean ensureActionHandler() {
        if (actionHandler != null) {
            return true;
        }

        withOutput(() -> renderer.showError("Action handler is not configured: the command cannot be sent."));
        return false;
    }

    /**
     * Parses an integer without throwing parsing exceptions into command handlers.
     */
    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Returns the current player nickname or a safe fallback.
     */
    private String safeCurrentPlayer() {
        return currentGameView != null && currentGameView.getCurrentPlayer() != null
                ? currentGameView.getCurrentPlayer()
                : "unknown";
    }

    /**
     * Returns the first non-blank message, otherwise a fallback.
     */
    private String nonBlankOrDefault(String message, String fallback) {
        return message == null || message.isBlank() ? fallback : message;
    }

    /**
     * Serializes console writes so asynchronous model updates do not interleave with each other.
     */
    private void withOutput(Runnable outputAction) {
        synchronized (outputLock) {
            outputAction.run();
        }
    }

    /**
     * Returns the local player nickname known by the CLI.
     */
    public String getId() {
        return id;
    }
}
