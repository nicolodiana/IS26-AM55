package it.polimi.ingsw.am55.controller;
import it.polimi.ingsw.am55.MesosModel.Game.Game;
import it.polimi.ingsw.am55.MesosModel.Game.GameModelInterface;
import it.polimi.ingsw.am55.message.*;

/**
 * Controller server-side.
 *
 * Si occupa di:
 * - ricevere la richiesta logica dal layer di rete
 * - chiamare il model di gioco
 * - costruire il MessageToClient da restituire a RmiServer
 *
 * Non si occupa di inviare direttamente il messaggio ai client:
 * quello lo fa RmiServer.
 */
public class GameController {

    private GameModelInterface gameModel;
    private int numPlayers;

    //inizialmente nullo perchè assume un valore soltanto quando viene fatto un create game
    public GameController() {
        this.gameModel = null;
    }


    public MessageToClient createGame(String playerId, String totemColor, int numPlayers) {
        if (gameModel != null) {
            return new ErrorMessage("La partita esiste già.");
        }

        try {
            gameModel = new Game(numPlayers);
            gameModel.addPlayer(playerId, totemColor);
            this.numPlayers = numPlayers;

            //String currentPlayer = gameModel.getCurrentPlayer();

            return new GameCreatedMessage(playerId, numPlayers, "Game has been created");

        } catch (Exception e) {
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
                return new InfoMessage("Game has started");
            }

            return new InfoMessage("Il giocatore " + playerId + " si è unito alla partita.");

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
            gameModel.placeTotem(index);
            //da rivedere return new BoardUpdatedMessage(new String("board"));
            //return new BoardUpdatedMessage("board");
        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }*/
}

/*
    public MessageToClient pickCard(String playerId, int cardId) {
        try {
            game.pickCard(playerId, cardId);

            BoardDto boardDto = BoardDto.from(game.getBoard());
            return new BoardUpdatedMessage(boardDto);

        } catch (IllegalArgumentException e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    public MessageToClient endTurn(String playerId) {
        try {
            game.endTurn(playerId);

            BoardDto boardDto = BoardDto.from(game.getBoard());
            return new BoardUpdatedMessage(boardDto);

        } catch (IllegalArgumentException e) {
            return new ErrorMessage(e.getMessage());
        }

    */
