package it.polimi.ingsw.am55.controller;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Game.Game;
import it.polimi.ingsw.am55.MesosModel.Game.GameModelInterface;
import it.polimi.ingsw.am55.dto.GameView;
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

    public MessageToClient createGame(String playerId, String totemColor, int numPlayers) {
        if (gameModel != null) {
            return new ErrorMessage("La partita esiste già.");
        }

        try {
            gameModel = new Game(numPlayers);
            gameModel.addPlayer(playerId, totemColor);
            this.numPlayers = numPlayers;

            if (gameModel.getNumPlayers() == this.numPlayers) {
                return new UpdateViewMessage(
                        gameModel.toView(),
                        "La partita è iniziata!"
                );
            }

            return new WaitingMessage(
                    "Partita creata correttamente con id " + gameModel.getIdGame() + ", in attesa di altri player."
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

            return new WaitingMessage("Ti sei unito alla partita. In attesa di altri player.");

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    public MessageToClient pickCard(String playerId, int cardId) {
        if (gameModel == null) {
            return new ErrorMessage("Nessuna partita creata.");
        }

        try {
            gameModel.pickCard(cardId, playerId);

            GameView viewAfterPick = gameModel.toView();

            /*
             * CASO 1:
             * Fine round normale: devo risolvere gli eventi della lower row(spetta a me se non ho pickspecial da fare)
             * + e prima di mostrare messaggio di risoluzione eventi mando prima board after pick
             */
            if (gameModel.getGameState().equals(GameState.EVENTRESOLVE)) {
                List<MessageToClient> messages = new ArrayList<>();




                messages.add(new UpdateViewMessage(
                        viewAfterPick,
                        "pick done"
                ));

                List<ResolveEventView> resolvedEvents = gameModel.eventResolve();

                GameView viewAfterResolve = gameModel.toView();

                messages.add(new GameBroadcastInfo(
                        "Inizia la risoluzione degli eventi..."
                ));
                //se non ho eventi da risolvere devo comunque mandare la board aggiornata perche si e fatto swap delle row

                if (resolvedEvents == null || resolvedEvents.isEmpty()) {
                    messages.add(new UpdateViewMessage(
                            viewAfterResolve,
                            "Nessun evento da risolvere."
                    ));

                    return new MultipleMessages(messages);
                }


                viewAfterResolve.setResolveEvents(resolvedEvents);

                messages.add(new UpdateViewMessage(
                        viewAfterResolve,
                        "Risoluzione eventi completata."
                ));

                return new MultipleMessages(messages);
            }

            /*
             * CASO 2:
             * Fine ultimo round (sempre senza pickspecial) : devo risolvere l'end game.
             */
            if (gameModel.getGameState().equals(GameState.ENDGAMERESOLVE)) {
                EndGameResultView endGameResult = gameModel.endGame();

                GameView finalGameView = gameModel.toView();

                return new GameEndedMessage(
                        finalGameView,
                        endGameResult,
                        "Partita terminata."
                );
            }

            /*
             * CASO 3:
             * Pick normale (non ultimo player, ricevo subito board aggiornata)
             */
            return new UpdateViewMessage(
                    viewAfterPick,
                    "pick done"
            );

        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    public MessageToClient placeTotem(String playerId, int index) {
        if (gameModel == null) {
            return new ErrorMessage("Nessuna partita creata.");
        }

        try {
            gameModel.placeTotem(index, playerId);

            return new UpdateViewMessage(
                    gameModel.toView(),
                    "Totem piazzato correttamente."
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

            GameView viewAfterPickSpecial = gameModel.toView();

            /*
             * CASO 1:
             * Pick special fatta a fine round NON ultimo.
             * Dopo la pick special parte sempre la risoluzione eventi.
             */
            if (gameModel.getGameState().equals(GameState.EVENTRESOLVE)) {
                List<MessageToClient> messages = new ArrayList<>();

                messages.add(new UpdateViewMessage(
                        viewAfterPickSpecial,
                        "pick special done"
                ));

                messages.add(new GameBroadcastInfo(
                        "Inizia la risoluzione degli eventi..."
                ));

                List<ResolveEventView> resolvedEvents = gameModel.eventResolve();

                GameView viewAfterResolve = gameModel.toView();

                if (resolvedEvents == null || resolvedEvents.isEmpty()) {
                    messages.add(new UpdateViewMessage(
                            viewAfterResolve,
                            "Nessun evento da risolvere."
                    ));

                    return new MultipleMessages(messages);
                }

                viewAfterResolve.setResolveEvents(resolvedEvents);

                messages.add(new UpdateViewMessage(
                        viewAfterResolve,
                        "Risoluzione eventi completata."
                ));

                return new MultipleMessages(messages);
            }

            /*
             * CASO 2:
             * Pick special fatta a fine ultimo round.
             * Dopo la pick special parte direttamente l'end game.
             */
            if (gameModel.getGameState().equals(GameState.ENDGAMERESOLVE)) {
                EndGameResultView endGameResult = gameModel.endGame();

                GameView finalGameView = gameModel.toView();

                return new GameEndedMessage(
                        finalGameView,
                        endGameResult,
                        "Partita terminata."
                );
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


}

