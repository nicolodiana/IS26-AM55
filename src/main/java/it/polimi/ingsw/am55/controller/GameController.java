package it.polimi.ingsw.am55.controller;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Game.Game;
import it.polimi.ingsw.am55.MesosModel.Game.GameModelInterface;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.LobbyView;
import it.polimi.ingsw.am55.dto.PlayerView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;
import it.polimi.ingsw.am55.message.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates lobby and game operations by delegating them to the game model
 * and converting their outcomes into messages for clients.
 */
public class GameController {
    /**
     * gameModel with all the game Logic
     */
    private GameModelInterface gameModel;
    /**
     * number of players
     */
    private int numPlayers;

    /**
     * Creates a controller with no active game.
     */
    public GameController() {
        this.gameModel = null;
        this.numPlayers = 0;
    }

    /**
     * Builds an action result message followed by one line for each automatically skipped player.
     *
     * @param baseMessage    initial action result text
     * @param skippedPlayers nicknames of the automatically skipped players
     * @return the composed message
     */
    private String createSkippedPlayersMessage(
            String baseMessage,
            List<String> skippedPlayers
    ) {
        StringBuilder message =
                new StringBuilder(baseMessage);

        for (String nickname : skippedPlayers) {
            message.append(System.lineSeparator())
                    .append(nickname)
                    .append(
                            " turn skipped, couldn't select any card"
                    );
        }

        return message.toString();
    }

    /**
     * Resolves events or the end game when the completed pick phase requires it.
     *
     * @param actionUpdateMessage message describing the action that completed the phase
     * @return the action message, optionally combined with the resulting phase-resolution message
     */
    private MessageToClient resolveEndOfPickPhaseIfNeeded(
            MessageToClient actionUpdateMessage
    ) {

        if (gameModel.getGameState()
                .equals(GameState.EVENTRESOLVE)) {

            List<MessageToClient> messages =
                    new ArrayList<>();


            messages.add(actionUpdateMessage);

            List<ResolveEventView> resolvedEvents =
                    gameModel.eventResolve();

            GameView viewAfterResolve =
                    gameModel.toView();

            if (resolvedEvents == null
                    || resolvedEvents.isEmpty()) {

                messages.add(new UpdateViewMessage(
                        viewAfterResolve,
                        "[no event to resolve]"
                ));

                return new MultipleMessages(messages);
            }

            viewAfterResolve.setResolveEvents(
                    resolvedEvents
            );

            messages.add(new UpdateViewMessage(
                    viewAfterResolve,
                    "event resolved"
            ));

            return new MultipleMessages(messages);
        }


        if (gameModel.getGameState()
                .equals(GameState.ENDGAMERESOLVE)) {

            List<MessageToClient> messages =
                    new ArrayList<>();

            messages.add(actionUpdateMessage);

            EndGameResultView endGameResult =
                    gameModel.endGame();

            GameView finalGameView =
                    gameModel.toView();

            messages.add(new GameEndResolveMessage(
                    finalGameView,
                    endGameResult,
                    "Game ended"
            ));

            return new MultipleMessages(messages);
        }

        return actionUpdateMessage;
    }

    /**
     * Returns a snapshot of the current lobby.
     *
     * @return the current lobby view, or an empty view when no game exists
     */
    public LobbyView getLobbyView() {
        if (gameModel == null) {
            return new LobbyView(null, null);
        }

        return new LobbyView(
                gameModel.getGameState(),
                gameModel.getPlayers()
        );
    }

    /**
     * Creates a game and adds its creator as the first player.
     *
     * @param playerId  identifier of the player creating the game
     * @param totemColor totem color selected by the player
     * @param numPlayers number of players required to start the game
     * @return a waiting message on success, or an error message if creation fails
     */
    public MessageToClient createGame(String playerId, String totemColor, int numPlayers) {
        if (gameModel != null) {
            return new ErrorMessage("Game already exists");
        }

        try {
            gameModel = new Game(numPlayers);
            gameModel.addPlayer(playerId, totemColor);
            this.numPlayers = numPlayers;
            return new WaitingMessage(
                    "game created waiting other players..", gameModel.toView()
            );

        } catch (Exception e) {
            gameModel = null;
            this.numPlayers = 0;
            return new ErrorMessage(e.getMessage());
        }
    }

    /**
     * Adds a player to the active game and starts it when the expected player count is reached.
     *
     * @param playerId  identifier of the joining player
     * @param totemColor totem color selected by the player
     * @return a lobby or game update on success, or an error message if joining fails
     */
    public MessageToClient joinGame(String playerId, String totemColor) {
        if (gameModel == null) {
            return new ErrorMessage("No game created.");
        }

        try {
            gameModel.addPlayer(playerId, totemColor);
            if (gameModel.getNumPlayers() == this.numPlayers) {
                return new UpdateViewMessage(
                        gameModel.toView(),
                        "Game started!"
                );
            }

            return new WaitingMessage(
                    "added in the game, waiting for other players..", gameModel.toView()
            );

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    /**
     * Applies a normal card pick and resolves the following phase when required.
     *
     * @param playerId identifier of the player performing the action
     * @param cardId   identifier of the selected card
     * @return the resulting client update, or an error message if the action fails
     */
    public MessageToClient pickCard(
            String playerId,
            int cardId
    ) {
        if (gameModel == null) {
            return new ErrorMessage(
                    "No game created."
            );
        }

        try {
            List<String> skippedPlayers =
                    gameModel.pickCard(
                            cardId,
                            playerId
                    );

            int newPp =
                    gameModel.getPlayerPoints(playerId);

            int newFood =
                    gameModel.getPlayerFood(playerId);

            MessageToClient pickUpdateMessage;


            if (skippedPlayers.isEmpty()) {
                pickUpdateMessage =
                        new PickCardMessage(
                                playerId,
                                cardId,
                                newFood,
                                newPp,
                                gameModel.getCurrentPlayer(),
                                gameModel.getGameState()
                        );
            } else {

                pickUpdateMessage =
                        new UpdateViewMessage(
                                gameModel.toView(),
                                createSkippedPlayersMessage(
                                        "Card drawn correctly.",
                                        skippedPlayers
                                )
                        );
            }

            return resolveEndOfPickPhaseIfNeeded(
                    pickUpdateMessage
            );

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    /**
     * Places a player's totem and resolves the following phase when required.
     *
     * @param playerId identifier of the player performing the action
     * @param index    target bidding-ticket index
     * @return the resulting client update, or an error message if the action fails
     */
    public MessageToClient placeTotem(
            String playerId,
            int index
    ) {
        if (gameModel == null) {
            return new ErrorMessage(
                    "No game created."
            );
        }

        try {
            List<String> skippedPlayers =
                    gameModel.placeTotem(
                            index,
                            playerId
                    );

            MessageToClient placeTotemUpdateMessage;

            if (skippedPlayers.isEmpty()) {
                placeTotemUpdateMessage =
                        new PlaceTotemMessage(
                                playerId,
                                index,
                                gameModel.getCurrentPlayer(),
                                gameModel.getGameState()
                        );
            } else {

                placeTotemUpdateMessage =
                        new UpdateViewMessage(
                                gameModel.toView(),
                                createSkippedPlayersMessage(
                                        "Totem placed correctly.",
                                        skippedPlayers
                                )
                        );
            }

            return resolveEndOfPickPhaseIfNeeded(
                    placeTotemUpdateMessage
            );

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    /**
     * Applies a special-card pick and resolves the resulting event or end-game phase.
     *
     * @param playerId identifier of the player performing the action
     * @param cardId   identifier of the selected special card
     * @return the resulting client messages, or an error message if the action fails
     */
    public MessageToClient pickSpecial(String playerId, int cardId) {
        if (gameModel == null) {
            return new ErrorMessage("No game created.");
        }

        try {
            gameModel.pickSpecial(cardId, playerId);
            int newPp = gameModel.getPlayerPoints(playerId);
            int newFood = gameModel.getPlayerFood(playerId);


            if (gameModel.getGameState().equals(GameState.EVENTRESOLVE)) {
                List<MessageToClient> messages = new ArrayList<>();

                messages.add(new PickCardMessage(playerId, cardId, newFood, newPp, gameModel.getCurrentPlayer(), gameModel.getGameState()));

                List<ResolveEventView> resolvedEvents = gameModel.eventResolve();
                GameView viewAfterResolve = gameModel.toView();

                if (resolvedEvents == null || resolvedEvents.isEmpty()) {
                    messages.add(new UpdateViewMessage(
                            viewAfterResolve,
                            "no event to resolve"
                    ));

                    return new MultipleMessages(messages);
                }

                viewAfterResolve.setResolveEvents(resolvedEvents);

                messages.add(new UpdateViewMessage(
                        viewAfterResolve,
                        "event resolved"
                ));

                return new MultipleMessages(messages);
            }

            if (gameModel.getGameState().equals(GameState.ENDGAMERESOLVE)) {
                List<MessageToClient> messages = new ArrayList<>();

                messages.add(new PickCardMessage(
                        playerId,
                        cardId,
                        newFood,
                        newPp,
                        gameModel.getCurrentPlayer(),
                        gameModel.getGameState()
                ));

                EndGameResultView endGameResult = gameModel.endGame();
                GameView finalGameView = gameModel.toView();

                messages.add(new GameEndResolveMessage(
                        finalGameView,
                        endGameResult,
                        "Game ended."
                ));

                return new MultipleMessages(messages);
            }

            return new ErrorMessage("Unvalid state after pickSpecial.");

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    /**
     * Marks the active game as crashed, clears the controller state, and creates a crash notification.
     *
     * @return the crash notification to broadcast to clients
     */
    public MessageToClient handleGameCrashed(){

        gameModel.handleGameCrashed();
        MessageToClient message =  new GameCrashedBroadcast("a Player got disconnected, game ended.");
        gameModel = null;
        this.numPlayers = 0;
        return message;

    }

    /**
     * Ends the active game, clears the controller state, and creates a quit notification.
     *
     * @param playerId identifier of the player leaving the game
     * @return the quit notification, or an error message if the operation fails
     */
    public MessageToClient quitGame(String playerId){
        if (gameModel == null) {
            return new ErrorMessage("No game created.");
        }

        try {
            gameModel.quitGame();

            MessageToClient message = new QuitGameMessage(gameModel.toView(),
                    "PLAYER  " + playerId + " quit. ");
            gameModel=null;
            this.numPlayers = 0;
            return message;

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }
}

