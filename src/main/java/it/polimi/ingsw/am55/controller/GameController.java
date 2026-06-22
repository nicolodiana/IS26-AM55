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
    private GameModelInterface gameModel;
    private int numPlayers;

    public GameController() {
        this.gameModel = null;
        this.numPlayers = 0;
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
            return new ErrorMessage("The game already exists.");
        }

        try {
            gameModel = new Game(numPlayers);
            gameModel.addPlayer(playerId, totemColor);
            this.numPlayers = numPlayers;
            return new WaitingMessage(
                    "Match created successfully "+" waiting for more players..", gameModel.toView()
            );

        } catch (Exception e) {
            gameModel = null;
            this.numPlayers = 0;
            return new ErrorMessage(e.getMessage());
        }
    }

    public MessageToClient joinGame(String playerId, String totemColor) {
        if (gameModel == null) {
            return new ErrorMessage("No matches created.");
        }

        try {
            gameModel.addPlayer(playerId, totemColor);
            if (gameModel.getNumPlayers() == this.numPlayers) {
                return new UpdateViewMessage(
                        gameModel.toView(),
                        "The game has begun!"
                );
            }

            return new WaitingMessage(
                    "Successfully added to game "+" waiting for more players..", gameModel.toView()
            );

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    public MessageToClient pickCard(String playerId, int cardId) {
        if (gameModel == null) {
            return new ErrorMessage("No matches created.");
        }

        try {
            gameModel.pickCard(cardId, playerId);
            int newPp = gameModel.getPlayerPoints(playerId);
            int newFood = gameModel.getPlayerFood(playerId);
            //GameView viewAfterPick = gameModel.toView();
            /*
             * CASO 1:
             * Fine round normale: devo risolvere gli eventi della lower row(spetta a me se non ho pickspecial da fare)
             * + e prima di mostrare messaggio di risoluzione eventi mando prima board after pick
             */
            if (gameModel.getGameState().equals(GameState.EVENTRESOLVE)) {
                List<MessageToClient> messages = new ArrayList<>();
                //accodo primo messaggio della board post pick
//                messages.add(new UpdateViewMessage(
//                        viewAfterPick,
//                        "pick done"
//                ));
                messages.add(new PickCardMessage(playerId, cardId, newFood, newPp, gameModel.getCurrentPlayer(), gameModel.getGameState()));

                List<ResolveEventView> resolvedEvents = gameModel.eventResolve();
                GameView viewAfterResolve = gameModel.toView();
                //ACCODO 2 MESSAGGIO DISTINGUENDO SE HA EVENTI RISOLTI O MENO PERCHE CAMBIA IL MESSAGGIO ASSOCIATO
                //se non ho eventi da risolvere devo comunque mandare la board aggiornata perche si e fatto swap delle row
                if (resolvedEvents == null || resolvedEvents.isEmpty()) {
                    messages.add(new UpdateViewMessage(
                            viewAfterResolve,
                            "[no event to resolve]"
                    ));

                    return new MultipleMessages(messages);
                }

//se invece ci sono eventi da risolvere li copio nella Gameview nella lista dedicada, e poi ritorno la view aggiornata
                viewAfterResolve.setResolveEvents(resolvedEvents);
                messages.add(new UpdateViewMessage(
                        viewAfterResolve,
                        "event resolved"
                ));

                return new MultipleMessages(messages);
            }

            /*
             * CASO 2:
             * Fine ultimo round (sempre senza pickspecial) : devo risolvere l'end game.
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
                        "Game ENDED"
                ));
                gameModel=null;
                this.numPlayers=0;
                return new MultipleMessages(messages);
            }

            /*
             * CASO 3:
             * Pick normale (non ultimo player, ricevo subito board aggiornata)
             */
            return new PickCardMessage(playerId, cardId, newFood, newPp, gameModel.getCurrentPlayer(), gameModel.getGameState());

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    public MessageToClient placeTotem(String playerId, int index) {
        if (gameModel == null) {
            return new ErrorMessage("No matches created.");
        }

        try {
            gameModel.placeTotem(index, playerId);

            //            return new UpdateViewMessage(
//                    gameModel.toView(),
//                    "Totem piazzato correttamente."
//            );
            return new PlaceTotemMessage(playerId, index, gameModel.getCurrentPlayer(), gameModel.getGameState());


        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    public MessageToClient pickSpecial(String playerId, int cardId) {
        if (gameModel == null) {
            return new ErrorMessage("No matches created.");
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
                        "Game ENDED"
                ));
                gameModel=null;
                this.numPlayers=0;
                return new MultipleMessages(messages);
            }

            /*
             * Caso teoricamente impossibile:
             * pickSpecial dovrebbe sempre portare a EVENTRESOLVE o ENDGAMERESOLVE.
             */
            return new ErrorMessage("Invalid state after pick special.");

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }
    public MessageToClient handleGameCrashed(){

        gameModel.handleGameCrashed();
        MessageToClient message =  new GameCrashedBroadcast("A player has disconnected, the game has crashed");
        gameModel = null;
        this.numPlayers = 0;
        return message;

    }
    public MessageToClient quitGame(String playerId){
        if (gameModel == null) {
            return new ErrorMessage("No matches created.");
        }

        try {
            gameModel.quitGame();

            MessageToClient message = new QuitGameMessage(gameModel.toView(),
                    "Player " + playerId + " came out. ");
            gameModel=null;
            this.numPlayers = 0;
            return message;

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }
}

