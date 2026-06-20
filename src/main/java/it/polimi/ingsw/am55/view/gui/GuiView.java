package it.polimi.ingsw.am55.view.gui;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.LobbyView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.view.ClientAction;
import it.polimi.ingsw.am55.view.ClientActionResolver;
import it.polimi.ingsw.am55.view.ClientModelObserver;
import it.polimi.ingsw.am55.view.gui.scene.*;
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
    private volatile boolean inLobby = true;
    private volatile LobbyView currentLobbyView;
    private volatile boolean startGameRequested;

    public GuiView(ClientModel model) {
        this.model = Objects.requireNonNull(model, "model");
        this.actionResolver = new ClientActionResolver();
        this.startGameRequested = false;
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

    public void showInitialScene() {
        Platform.runLater(SceneManager::showStartScene);
    }
    public void startGameFromInitialScene() {
        startGameRequested = true;

        if (currentLobbyView != null) {
            renderCurrentState();
            showPendingMessages();
            return;
        }

        if (SceneManager.getActiveController() instanceof StartSceneController start) {
            start.showMessage("Sincronizzazione lobby con il server...");
        }
    }

    @Override
    public void onModelChanged(ClientModel updatedModel) {
        Platform.runLater(() -> {

            /*
             * Stato PRECEDENTE della GuiView.
             * Serve per capire se il QuitLobbyMessage che sta arrivando
             * deriva da auto-quit perché la partita era già iniziata.
             */
            boolean wasInLobbyWithStartedGame =
                    inLobby
                            && currentLobbyView != null
                            && currentLobbyView.getGameState() != null
                            && currentLobbyView.getGameState() != GameState.CREATED;

            waitingServerResponse = false;

            currentErrorMessage = updatedModel.getLastError();
            currentInfoMessage = updatedModel.getStateRequest();
            currentGameView = updatedModel.getGameView();
            inLobby = updatedModel.isInLobby();
            currentLobbyView = updatedModel.getLobbyView();

            /*
             * Caso importante:
             * arriva il QuitLobbyMessage dopo che avevamo visto una lobby
             * con partita già iniziata.
             *
             * Quindi NON torno in lobby.
             * Mostro la QuitGameScene con il messaggio del quit + motivo.
             */
            if (wasInLobbyWithStartedGame
                    && currentInfoMessage != null
                    && currentInfoMessage.toLowerCase().contains("uscito correttamente")) {
                showTerminalMessage(
                        currentInfoMessage
                                + "\nPerché la partita è già iniziata e non accetta altri giocatori."
                );
                return;
            }

            /*
             * Se siamo già nella schermata terminale di quit/crash,
             * non dobbiamo più ricalcolare lo stato e tornare in lobby.
             */
            if (SceneManager.getActiveController() instanceof QuitGameSceneController quit) {
                if (currentErrorMessage != null && !currentErrorMessage.isBlank()) {
                    quit.render(currentErrorMessage);
                } else if (currentInfoMessage != null && !currentInfoMessage.isBlank()) {
                    quit.render(currentInfoMessage);
                }

                return;
            }

            /*
             * StartScene:
             * se non ho ancora premuto INIZIA GIOCO, non cambio scena.
             */
            if (!startGameRequested && inLobby) {
                if (SceneManager.getActiveController() instanceof StartSceneController start
                        && currentInfoMessage != null
                        && !currentInfoMessage.isBlank()) {
                    start.showMessage("CLICK BUTTON TO START  ");
                }

                return;
            }

            renderCurrentState();
            showPendingMessages();
        });
    }
    //per gestire caso mi provo ad unire con partita in corso
    private boolean isLobbyGameAlreadyStarted() {
        return currentLobbyView != null
                && currentLobbyView.getGameState() != null
                && currentLobbyView.getGameState() != GameState.CREATED;
    }
    //anche questo x gestire unione con partita in corso
    private void leaveLobbyBecauseGameAlreadyStarted() {
        if (waitingServerResponse || actionHandler == null) {
            return;
        }

        waitingServerResponse = true;

        commandExecutor.submit(() -> {
            try {
                actionHandler.onQuitSelectedLobby();
            } catch (RuntimeException e) {
                Platform.runLater(() -> {
                    waitingServerResponse = false;
                    showTerminalMessage("Errore durante l'uscita dalla lobby: " + e.getMessage());
                });
            }
        });
    }

    private void renderCurrentState() {
        /*
         * Gestione crash/connessione persa.
         * Deve avere priorità su lobby/game/endgame.
         */
        ClientAction crashAction = actionResolver.resolve(
                currentGameView,
                playerId,
                inLobby,
                model.isGameCrashed()
        );

        if (crashAction == ClientAction.CRASHED) {
            showTerminalMessage(
                    currentInfoMessage != null && !currentInfoMessage.isBlank()
                            ? currentInfoMessage
                            : "Connessione chiusa."
            );
            return;
        }

        /*
         * IMPORTANTE:
         * Questo controllo deve stare prima del resolver di game.
         * Se sei ancora in lobby, non devi calcolare action di board.
         */
        if (inLobby) {
            if (isLobbyGameAlreadyStarted()) {
                leaveLobbyBecauseGameAlreadyStarted();
                return;
            }

            showLobby(false);
            return;
        }
        EndGameResultView result = model.getEndGameResultView();

        if (result != null) {
            showEndGame(result);
            return;
        }

        /*
         * IMPORTANTE:
         * Qui il terzo parametro deve essere false.
         * Siamo già fuori lobby, quindi il resolver deve calcolare
         * PLACE_TOTEM / PICK_CARD / PICK_SPECIAL / WAITING_FOR_TURN ecc.
         */
        ClientAction action = actionResolver.resolve(
                currentGameView,
                playerId,
                false,
                model.isGameCrashed()
        );

        switch (action) {
            case LOBBY -> showLobby(false);

            case WAITING_TO_START -> showLobby(true);

            case END_GAME -> {
                EndGameResultView endGameResult = model.getEndGameResultView();

                if (endGameResult != null) {
                    showEndGame(endGameResult);
                } else {
                    showTerminalMessage(
                            currentInfoMessage != null && !currentInfoMessage.isBlank()
                                    ? currentInfoMessage
                                    : "Partita terminata."
                    );
                }
            }

            case CRASHED -> showTerminalMessage(
                    currentInfoMessage != null && !currentInfoMessage.isBlank()
                            ? currentInfoMessage
                            : "Connessione chiusa."
            );

            default -> showGame(currentGameView, action);
        }
    }

    //usato sia per crash sia per quit, la gestione è analoga di cambio di scena, è una schermata standard che mostra il messaggio finale con spiegazione della chiusura di gioco
    private void showTerminalMessage(String message) {
        SceneManager.showQuitGameScene();

        QuitGameSceneController controller =
                (QuitGameSceneController) SceneManager.getActiveController();

        controller.render(message);
    }

    private void showLobby(boolean locked) {
        SceneManager.showLobbySceneIfNeeded();

        LobbySceneController controller =
                (LobbySceneController) SceneManager.getActiveController();

        controller.renderLobby(currentLobbyView, locked || waitingServerResponse);

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
        } else if (SceneManager.getActiveController() instanceof QuitGameSceneController quit) {
            quit.render(message);
        }
    }

    private void showError(String message) {
        if (SceneManager.getActiveController() instanceof LobbySceneController lobby) {
            lobby.showError(message);
        } else if (SceneManager.getActiveController() instanceof GameSceneController game) {
            game.showError(message);
        } else if (SceneManager.getActiveController() instanceof EndGameSceneController end) {
            end.showStatus("Errore: " + message);
        } else if (SceneManager.getActiveController() instanceof QuitGameSceneController quit) {
            quit.render("Errore: " + message);
        }
    }

    public void quitGame() {
        if (!ensureActionHandler() || playerId == null) {
            return;
        }

        submitCommand(() -> actionHandler.onQuitGameSelected(playerId));
    }

    public void quitLobby() {
        if (!ensureActionHandler()) {
            return;
        }

        showTerminalMessage("Uscita dalla lobby in corso...");

        submitCommand(() -> actionHandler.onQuitSelectedLobby());
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

        submitCommand(() -> actionHandler.onPlaceTotemSelected(this.playerId, ticketIndex));
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