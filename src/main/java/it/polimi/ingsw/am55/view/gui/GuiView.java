package it.polimi.ingsw.am55.view.gui;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.LobbyView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.message.ConnectionLostMessage;
import it.polimi.ingsw.am55.message.GameCrashedBroadcast;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.message.QuitGameMessage;
import it.polimi.ingsw.am55.message.QuitLobbyMessage;
import it.polimi.ingsw.am55.view.ClientAction;
import it.polimi.ingsw.am55.view.ClientActionResolver;
import it.polimi.ingsw.am55.view.ClientModelObserver;
import it.polimi.ingsw.am55.view.gui.scene.EndGameSceneController;
import it.polimi.ingsw.am55.view.gui.scene.GameSceneController;
import it.polimi.ingsw.am55.view.gui.scene.GenericSceneController;
import it.polimi.ingsw.am55.view.gui.scene.LobbySceneController;
import it.polimi.ingsw.am55.view.gui.scene.TerminalGameSceneController;
import javafx.application.Platform;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Questa classe osserva {@link ClientModel}, sceglie la scena corretta e delega
 * i dettagli di rendering ai controller FXML. I comandi del controller vengono eseguiti su un
 * thread in background dedicato, in modo che il thread dell'applicazione JavaFX rimanga reattivo.
 */
public class GuiView implements ClientModelObserver {

    private static final String EVENT_RESOLUTION_START_MESSAGE = "Event resolution is starting...";

    private final ClientModel model;
    private final ClientActionResolver actionResolver;
    private final ExecutorService commandExecutor;

    private UserActionHandler actionHandler;

    private volatile GameView currentGameView;

    private volatile String currentInfoMessage;

    private volatile String currentErrorMessage;

    private volatile String playerId;

    private volatile boolean inLobby = true;

    private volatile LobbyView currentLobbyView;

    private volatile boolean startGameRequested;

    /**
     * Creates a GUI view bound to a client model.
     *
     * @param model observed client model
     */
    public GuiView(ClientModel model) {
        this.model = Objects.requireNonNull(model, "model");
        this.actionResolver = new ClientActionResolver();
        this.commandExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("GUI-Command-Thread");
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Injects the command handler used to send player actions to the server.
     *
     * @param actionHandler controller-facing action handler
     */
    public void setActionHandler(UserActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    /**
     * Returns the nickname selected by the local player.
     *
     * @return local player nickname, or {@code null} before create/join
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * Shows the start scene on the JavaFX application thread.
     */

    public void showInitialScene() {
        Platform.runLater(SceneManager::showStartScene);
    }

    /**
     * Lets the player leave the splash scene and synchronize with the lobby state.
     */

    public void startGameFromInitialScene() {
        startGameRequested = true;

        if (currentLobbyView != null) {
            renderCurrentState();
        }

    }

    /**
     * Receives model updates and refreshes the correct scene.
     */
    @Override
    public void onModelChanged(ClientModel updatedModel) {
        Platform.runLater(() -> {
            MessageToClient lastmessage=updatedModel.getLastMessage();
            currentErrorMessage = updatedModel.getLastError();
            currentInfoMessage = updatedModel.getStateRequest();
            currentGameView = updatedModel.getGameView();
            inLobby = updatedModel.isInLobby();
            currentLobbyView = updatedModel.getLobbyView();

            if (!startGameRequested && inLobby) {
                return;
            }

            renderCurrentState();
            showPendingMessages();
        });
    }


    /**
     * Checks whether the lobby is already past the joinable CREATED state.
     */
    private boolean isLobbyGameAlreadyStarted() {
        return currentLobbyView != null
                && currentLobbyView.getGameState() != null
                && currentLobbyView.getGameState() != GameState.CREATED;
    }

    /**
     * Leaves the lobby automatically when the server says the game has already started.
     */
    private void leaveLobbyBecauseGameAlreadyStarted() {
        if (actionHandler == null) {
            return;
        }

        commandExecutor.submit(() -> {
            try {
                actionHandler.onQuitSelectedLobby();
            } catch (RuntimeException e) {
                Platform.runLater(() -> showTerminalMessage("Error while leaving the lobby: " + e.getMessage()));
            }
        });
    }

    /**
     * Routes the current model snapshot to the proper scene.
     */
    /**
     * Routes the current model snapshot to the proper scene.
     * <p>
     * This method is the GUI counterpart of the CLI expected-action rendering:
     * it reads the latest model snapshot, resolves the current client action and
     * chooses which scene must be shown.
     */
    /**
     * Routes the current model snapshot to the proper scene.
     * <p>
     * This method is the GUI counterpart of the CLI expected-action rendering:
     * it reads the latest model snapshot, resolves the current client action and
     * chooses which scene must be shown.
     */
    private void renderCurrentState() {
        ClientAction action = actionResolver.resolve(currentGameView, playerId, inLobby, model.isGameCrashed(), model.isGameEnded());

        switch (action) {
            case CRASHED -> showTerminalMessage(
                    nonBlankOrDefault(currentInfoMessage, "The connection is closed.")
            );

            case END_GAME -> {
                EndGameResultView endGameResult = model.getEndGameResultView();

                if (endGameResult != null) {
                    showEndGame(endGameResult);
                } else {
                    showTerminalMessage(nonBlankOrDefault(currentInfoMessage, "The game has ended."));
                }
            }

            case LOBBY -> {
                if (isLobbyGameAlreadyStarted()) {
                    leaveLobbyBecauseGameAlreadyStarted();
                    return;
                }
                showLobby(false);
            }

            case WAITING_TO_START -> showLobby(true);

            default -> showGame(currentGameView, action);
        }
    }

    /**
     * Shows the terminal scene used for quit and crash messages.
     */

    private void showTerminalMessage(String message) {
        SceneManager.showQuitGameScene();
        TerminalGameSceneController controller = (TerminalGameSceneController) SceneManager.getActiveController();
        controller.render(message);
    }

    /**
     * Shows the lobby scene and renders the latest lobby state.
     */
    private void showLobby(boolean locked) {

        SceneManager.showLobbySceneIfNeeded();
        LobbySceneController controller = (LobbySceneController) SceneManager.getActiveController();

        controller.renderLobby(currentLobbyView, false);

        if (locked) {
            controller.lockInteractions();
        }
    }

    /**
     * Shows the game scene and renders the latest game board.
     */
    private void showGame(GameView gameView, ClientAction action) {

        if (gameView == null) {
            showLobby(false);
            return;
        }

        SceneManager.showGameSceneIfNeeded();
        GameSceneController controller = (GameSceneController) SceneManager.getActiveController();
        controller.render(gameView, action, playerId);

        if (EVENT_RESOLUTION_START_MESSAGE.equals(currentInfoMessage)) {
            controller.showStatus("Event resolution in progress...");
        }
    }

    /**
     * Shows the end-game scene and renders final results.
     */
    private void showEndGame(EndGameResultView result) {
        SceneManager.showEndGameScene();
        EndGameSceneController controller = (EndGameSceneController) SceneManager.getActiveController();
        controller.render(result, currentGameView);
    }

    /**
     * Displays pending info or error messages on the active scene.
     */
    private void showPendingMessages() {
        if (currentErrorMessage != null && !currentErrorMessage.isBlank()) {
            showError(currentErrorMessage);
            return;
        }
        if (currentInfoMessage != null && !currentInfoMessage.isBlank()) {
            showInfo(currentInfoMessage);
        }
    }

    /**
     * Sends an info message to the active scene controller.
     */
    private void showInfo(String message) {
        GenericSceneController controller = SceneManager.getActiveController();
        if (controller != null) {
            controller.showStatus(message);
        }
    }

    /**
     * Sends an error message to the active scene controller.
     */
    private void showError(String message) {
        GenericSceneController controller = SceneManager.getActiveController();
        if (controller != null) {
            controller.showError(message);
        }
    }

    /**
     * Requests to leave the current game.
     */
    public void quitGame() {
        if (!ensureActionHandler() || playerId == null) {
            return;
        }
        submitCommand(() -> actionHandler.onQuitGameSelected(playerId));
    }

    /**
     * Requests to leave the lobby.
     */
    public void quitLobby() {
        if (!ensureActionHandler()) {
            return;
        }
        submitCommand(() -> actionHandler.onQuitSelectedLobby());
    }

    /**
     * Sends a create-game command.
     */
    public void createGame(String playerId, String totemColor, int numPlayers) {
        if (!ensureActionHandler()) {
            return;
        }
        this.playerId = playerId.trim();
        submitCommand(() -> actionHandler.onCreateGameSelected(this.playerId, totemColor, numPlayers));
    }

    /**
     * Sends a join-game command.
     */
    public void joinGame(String playerId, String totemColor) {
        if (!ensureActionHandler()) {
            return;
        }
        this.playerId = playerId.trim();
        submitCommand(() -> actionHandler.onJoinGameSelected(this.playerId, totemColor));
    }

    /**
     * Sends a totem-placement command.
     */
    public void placeTotem(int ticketIndex) {
        if (!ensureActionHandler()) {
            return;
        }
        submitCommand(() -> actionHandler.onPlaceTotemSelected(this.playerId, ticketIndex));
    }

    /**
     * Sends a standard card-pick command.
     */
    public void pickCard(int cardId) {
        if (!ensureActionHandler() || playerId == null) {
            return;
        }
        submitCommand(() -> actionHandler.onPickCardSelected(playerId, cardId));
    }

    /**
     * Sends a special card-pick command.
     */
    public void pickSpecial(int cardId) {
        if (!ensureActionHandler() || playerId == null) {
            return;
        }
        submitCommand(() -> actionHandler.onPickSpecialSelected(playerId, cardId));
    }

    /**
     * Runs a server command off the JavaFX thread and silently locks the active scene.
     * <p>
     * The lock is delegated to the active scene controller through
     * {@link GenericSceneController#lockInteractions()}, so each scene disables
     * only the controls that belong to it. No temporary status message is shown:
     * the next server update will redraw the scene and display the real server
     * message, if any.
     */

    private void submitCommand(Runnable command) {
        GenericSceneController controller = SceneManager.getActiveController();
        if (controller != null) {
            controller.lockInteractions();
        }

        commandExecutor.submit(() -> {
            try {
                command.run();
            } catch (RuntimeException e) {
                Platform.runLater(() -> {
                    renderCurrentState();
                    showError(e.getMessage());
                });
            }
        });
    }

    /**
     * Verifies that the command handler dependency is available.
     */
    private boolean ensureActionHandler() {
        if (actionHandler != null) {
            return true;
        }
        showError("Action handler is not configured: the command cannot be sent.");
        return false;
    }

    /**
     * Returns a fallback when a message is blank.
     */
    private String nonBlankOrDefault(String message, String fallback) {
        return message == null || message.isBlank() ? fallback : message;
    }

    /**
     * Stops the background command executor.
     */
    public void shutdown() {
        commandExecutor.shutdownNow();
    }
}
