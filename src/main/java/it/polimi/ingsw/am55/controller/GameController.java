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

public class GameController {
    //Le eccezioni lanciate dal model vengono catturate dal Controller in un ERROR MESSAGE
    private GameModelInterface gameModel;
    private int numPlayers;

    public GameController() {
        this.gameModel = null;
        this.numPlayers = 0;
    }

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
                            " ha saltato il turno poiché "
                                    + "non erano presenti carte "
                                    + "selezionabili da lui."
                    );
        }

        return message.toString();
    }

    /**
     * Se gli skip automatici hanno terminato l'intera fase di pesca,
     * risolve gli eventi o il fine partita.
     *
     * Altrimenti restituisce semplicemente il messaggio ricevuto.
     */
    private MessageToClient resolveEndOfPickPhaseIfNeeded(
            MessageToClient actionUpdateMessage
    ) {
        /*
         * Fine normale del round.
         */
        if (gameModel.getGameState()
                .equals(GameState.EVENTRESOLVE)) {

            List<MessageToClient> messages =
                    new ArrayList<>();

            /*
             * Prima viene mostrato l'aggiornamento della pick
             * e l'elenco dei giocatori saltati.
             */
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

        /*
         * Fine dell'ultimo round.
         */
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
                    "Partita terminata."
            ));

            return new MultipleMessages(messages);
        }

        /*
         * La fase di pesca non è ancora terminata.
         */
        return actionUpdateMessage;
    }

    public LobbyView getLobbyView() {
        //se nessuno è entrato in partita non devo aggiornare lobby ma mandare quella base con tutte le opzioni)
        if (gameModel == null) {
            return new LobbyView(null, null);
        }

        return new LobbyView(
                gameModel.getState(),
                gameModel.getPlayers()
        );
    }
    public MessageToClient createGame(String playerId, String totemColor, int numPlayers) {
        if (gameModel != null) {
            return new ErrorMessage("La partita esiste già.");
        }

        try {
            gameModel = new Game(numPlayers);
            gameModel.addPlayer(playerId, totemColor);
            this.numPlayers = numPlayers;
            return new WaitingMessage(
                    "Partita creata correttamente  "+" in attesa di altri player..", gameModel.toView()
            );

        } catch (Exception e) {
            gameModel = null;
            this.numPlayers = 0;
            return new ErrorMessage(e.getMessage());
        }
    }

    public MessageToClient joinGame(String playerId, String totemColor) {
        if (gameModel == null) {
            return new ErrorMessage("Nessuna partita creata.");
        }

        try {
            gameModel.addPlayer(playerId, totemColor);
            if (gameModel.getNumPlayers() == this.numPlayers) {
                return new UpdateViewMessage(
                        gameModel.toView(),
                        "La partita è iniziata!"
                );
            }

            return new WaitingMessage(
                    "Aggiunto correttamente in partita "+" in attesa di altri player..", gameModel.toView()
            );

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    public MessageToClient pickCard(
            String playerId,
            int cardId
    ) {
        if (gameModel == null) {
            return new ErrorMessage(
                    "Nessuna partita creata."
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

            /*
             * Nessun giocatore è stato saltato:
             * mantieni il precedente messaggio incrementale.
             */
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
                /*
                 * Quando vengono saltati uno o più giocatori,
                 * invia una GameView completa.
                 *
                 * Gli skip possono infatti modificare:
                 * - Bidding Trail;
                 * - Turn Ticket;
                 * - Cibo;
                 * - Punti Prestigio;
                 * - currentPlayer;
                 * - GameState.
                 */
                pickUpdateMessage =
                        new UpdateViewMessage(
                                gameModel.toView(),
                                createSkippedPlayersMessage(
                                        "Carta pescata correttamente.",
                                        skippedPlayers
                                )
                        );
            }

            /*
             * Gestisce anche il caso in cui gli skip abbiano
             * terminato l'intera fase di pesca.
             */
            return resolveEndOfPickPhaseIfNeeded(
                    pickUpdateMessage
            );

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    public MessageToClient placeTotem(
            String playerId,
            int index
    ) {
        if (gameModel == null) {
            return new ErrorMessage(
                    "Nessuna partita creata."
            );
        }

        try {
            List<String> skippedPlayers =
                    gameModel.placeTotem(
                            index,
                            playerId
                    );

            MessageToClient placeTotemUpdateMessage;

            /*
             * Il piazzamento non ha provocato skip:
             * mantieni il comportamento precedente.
             */
            if (skippedPlayers.isEmpty()) {
                placeTotemUpdateMessage =
                        new PlaceTotemMessage(
                                playerId,
                                index,
                                gameModel.getCurrentPlayer(),
                                gameModel.getGameState()
                        );
            } else {
                /*
                 * Questo ramo viene utilizzato quando è stato
                 * piazzato l'ultimo Totem e il primo giocatore
                 * della fase di pesca non può prendere carte.
                 */
                placeTotemUpdateMessage =
                        new UpdateViewMessage(
                                gameModel.toView(),
                                createSkippedPlayersMessage(
                                        "Totem piazzato correttamente.",
                                        skippedPlayers
                                )
                        );
            }

            /*
             * Se tutti i giocatori sono stati saltati, avvia
             * immediatamente eventi o fine partita.
             */
            return resolveEndOfPickPhaseIfNeeded(
                    placeTotemUpdateMessage
            );

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    public MessageToClient pickSpecial(String playerId, int cardId) {
        if (gameModel == null) {
            return new ErrorMessage("Nessuna partita creata.");
        }

        try {
            gameModel.pickSpecial(cardId, playerId);
            int newPp = gameModel.getPlayerPoints(playerId);
            int newFood = gameModel.getPlayerFood(playerId);

            //GameView viewAfterPickSpecial = gameModel.toView();

            /*
             * CASO 1:
             * Pick special fatta a fine round NON ultimo.
             * Dopo la pick special parte sempre la risoluzione eventi.
             */
            if (gameModel.getGameState().equals(GameState.EVENTRESOLVE)) {
                List<MessageToClient> messages = new ArrayList<>();
//
//                messages.add(new UpdateViewMessage(
//                        viewAfterPickSpecial,
//                        "pick special done"
//                ));
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

            /*
             * CASO 2:
             * Pick special fatta a fine ultimo round.
             * Dopo la pick special parte direttamente l'end game.
             */
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
                        "Partita terminata."
                ));

                return new MultipleMessages(messages);
            }

            /*
             * Caso teoricamente impossibile:
             * pickSpecial dovrebbe sempre portare a EVENTRESOLVE o ENDGAMERESOLVE.
             */
            return new ErrorMessage("Stato non valido dopo la pick special.");

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }
    public MessageToClient handleGameCrashed(){

        gameModel.handleGameCrashed();
        MessageToClient message =  new GameCrashedBroadcast("Un giocatore si è disconnesso, il gioco è terminato");
        gameModel = null;
        this.numPlayers = 0;
        return message;

    }
    public MessageToClient quitGame(String playerId){
        if (gameModel == null) {
            return new ErrorMessage("Nessuna partita creata.");
        }

        try {
            gameModel.quitGame();

            MessageToClient message = new QuitGameMessage(gameModel.toView(),
                    "PLAYER  " + playerId + " è uscito. ");
            gameModel=null;
            this.numPlayers = 0;
            return message;

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }
}

