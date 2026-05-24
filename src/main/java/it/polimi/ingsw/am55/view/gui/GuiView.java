package it.polimi.ingsw.am55.view.gui;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.view.ClientAction;
import it.polimi.ingsw.am55.view.ClientActionResolver;
import it.polimi.ingsw.am55.view.ClientModelObserver;
import it.polimi.ingsw.am55.view.gui.scene.EndGameSceneController;
import it.polimi.ingsw.am55.view.gui.scene.GameSceneController;
import it.polimi.ingsw.am55.view.gui.scene.LobbySceneController;
import javafx.application.Platform;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuiView implements ClientModelObserver {

    private static final String EVENT_RESOLUTION_START_MESSAGE = "Inizia la risoluzione degli eventi...";

    private final ClientModel model;
    private final ClientActionResolver actionResolver;
    private final ExecutorService commandExecutor;

    private UserActionHandler actionHandler;
    private volatile GameView currentGameView;
    private volatile String currentInfoMessage;
    private volatile String currentErrorMessage;
    private volatile boolean waitingServerResponse;
    private volatile String playerId;

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

    public void setActionHandler(UserActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    public String getPlayerId() {
        return playerId;
    }

    public boolean isWaitingServerResponse() {
        return waitingServerResponse;
    }

    public void showInitialScene() {
        Platform.runLater(() -> {
            SceneManager.showLobbyScene();
            LobbySceneController controller = (LobbySceneController) SceneManager.getActiveController();
            controller.showMessage("Connesso al server. Crea o unisciti a una partita.");
        });
    }

    @Override
    public void onModelChanged(ClientModel updatedModel) {
        Platform.runLater(() -> {
            waitingServerResponse = false;

            currentErrorMessage = updatedModel.getLastError();
            currentInfoMessage = updatedModel.getStateRequest();
            currentGameView = updatedModel.getGameView();

            renderCurrentState();
            showPendingMessages();
        });
    }

    private void renderCurrentState() {
        EndGameResultView result = model.getEndGameResultView();

        if (result != null) {
            showEndGame(result);
            return;
        }

        ClientAction action = actionResolver.resolve(currentGameView, playerId);

        switch (action) {
            case LOBBY -> showLobby(false);

            case WAITING_TO_START -> showLobby(true);

            case END_GAME -> {
                EndGameResultView endGameResult = model.getEndGameResultView();
                if (endGameResult != null) {
                    showEndGame(endGameResult);
                } else {
                    showGame(currentGameView, action);
                }
            }

            default -> showGame(currentGameView, action);
        }
    }

    private void showLobby(boolean locked) {
        SceneManager.showLobbySceneIfNeeded();

        LobbySceneController controller =
                (LobbySceneController) SceneManager.getActiveController();

        if (locked) {
            controller.lock(
                    currentInfoMessage != null && !currentInfoMessage.isBlank()
                            ? currentInfoMessage
                            : "Partita creata. In attesa degli altri giocatori..."
            );
        }
    }

    private void showGame(GameView gameView, ClientAction action) {
        if (gameView == null) {
            showLobby(false);
            return;
        }

        SceneManager.showGameSceneIfNeeded();

        GameSceneController controller =
                (GameSceneController) SceneManager.getActiveController();

        controller.render(gameView, action, playerId, waitingServerResponse);

        if (EVENT_RESOLUTION_START_MESSAGE.equals(currentInfoMessage)) {
            controller.showStatus("Risoluzione eventi in corso...");
        }
    }

    private void showEndGame(EndGameResultView result) {
        SceneManager.showEndGameScene();

        EndGameSceneController controller =
                (EndGameSceneController) SceneManager.getActiveController();

        controller.render(result, currentGameView);
    }

    private void showPendingMessages() {
        if (currentErrorMessage != null && !currentErrorMessage.isBlank()) {
            showError(currentErrorMessage);
            return;
        }

        if (currentInfoMessage != null && !currentInfoMessage.isBlank()) {
            showInfo(currentInfoMessage);
        }
    }

    private void showInfo(String message) {
        if (SceneManager.getActiveController() instanceof LobbySceneController lobby) {
            lobby.showMessage(message);
        } else if (SceneManager.getActiveController() instanceof GameSceneController game) {
            game.showStatus(message);
        } else if (SceneManager.getActiveController() instanceof EndGameSceneController end) {
            end.showStatus(message);
        }
    }

    private void showError(String message) {
        if (SceneManager.getActiveController() instanceof LobbySceneController lobby) {
            lobby.showError(message);
        } else if (SceneManager.getActiveController() instanceof GameSceneController game) {
            game.showError(message);
        } else if (SceneManager.getActiveController() instanceof EndGameSceneController end) {
            end.showStatus("Errore: " + message);
        }
    }

    public void createGame(String playerId, String totemColor, int numPlayers) {
        if (!ensureActionHandler()) {
            return;
        }

        this.playerId = playerId.trim();
        submitCommand(() -> actionHandler.onCreateGameSelected(this.playerId, totemColor, numPlayers));
    }

    public void joinGame(String playerId, String totemColor) {
        if (!ensureActionHandler()) {
            return;
        }

        this.playerId = playerId.trim();
        submitCommand(() -> actionHandler.onJoinGameSelected(this.playerId, totemColor));
    }

    public void placeTotem(int ticketIndex) {
        if (!ensureActionHandler()) {
            return;
        }

        submitCommand(() -> actionHandler.onPlaceTotemSelected(this.playerId,ticketIndex));
    }

    public void pickCard(int cardId) {
        if (!ensureActionHandler() || playerId == null) {
            return;
        }

        submitCommand(() -> actionHandler.onPickCardSelected(playerId, cardId));
    }

    public void pickSpecial(int cardId) {
        if (!ensureActionHandler() || playerId == null) {
            return;
        }

        submitCommand(() -> actionHandler.onPickSpecialSelected(playerId, cardId));
    }

    public void refreshCurrentScene() {
        Platform.runLater(() -> {
            renderCurrentState();
            showPendingMessages();
        });
    }

    private void submitCommand(Runnable command) {
        waitingServerResponse = true;

        if (SceneManager.getActiveController() instanceof GameSceneController game) {
            game.lockInteractions("Comando inviato. In attesa del server...");
        } else if (SceneManager.getActiveController() instanceof LobbySceneController lobby) {
            lobby.lock("Comando inviato. In attesa del server...");
        }

        commandExecutor.submit(() -> {
            try {
                command.run();
            } catch (RuntimeException e) {
                Platform.runLater(() -> {
                    waitingServerResponse = false;
                    showError(e.getMessage());
                });
            }
        });
    }

    private boolean ensureActionHandler() {
        if (actionHandler != null) {
            return true;
        }

        showError("ActionHandler non configurato: impossibile inviare il comando.");
        return false;
    }

    public void shutdown() {
        commandExecutor.shutdownNow();
    }
}