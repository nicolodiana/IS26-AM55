package it.polimi.ingsw.am55.controller;

import it.polimi.ingsw.am55.MesosModel.Game.Game;
import it.polimi.ingsw.am55.MesosModel.Game.GameModelInterface;
import it.polimi.ingsw.am55.message.ErrorMessage;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.message.UpdateViewMessage;
import it.polimi.ingsw.am55.message.WaitingMessage;

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
    /*

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
    */
}

