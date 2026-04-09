package it.polimi.ingsw.am55.MesosModel.Game;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Exceptions.*;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.util.List;


/**
 * This interface contains all methods can be called by Controller
 **/
public interface GameModelInterface {
    /**
     * Returns the game's id
     * @return game's id
    **/
    String getIdGame();
    /**
     * Add a player in the game
     * @throws NicknameAlreadyUsed if the nickname is already used from another player in game
     * @throws TotemAlreadyUsed if totem is already used from another player in game
     * **/
    void addPlayer(Player player) throws NicknameAlreadyUsed,TotemAlreadyUsed,PlayerNumberOutOfRange;
    /**
     * Returns the players in the game
     * @return the number of players in the game
     **/
    int getNumPlayers();
    /**
     * Return if the game's state
     * @return GameState which consist of game's state
     **/
    GameState getGameState();
    /**
     * Allows start a new game
     * @throws GameAlreadyStarted if the game is already started
     * **/
    void startGame() throws GameAlreadyStarted;
    /**
     * Allows to have the current player in the game
     * @return current player's nickname
     **/
    String getCurrentPlayer();

    /**
     * * Places the current player's totem from turn order ticket to the indicated bidding ticket.
     * If after placement, there are still players who need to place
     * their totem, the turn passes to the next player on the turn order ticket.
     * Otherwise, the placement phase ends and the first player in the second phase is set as current player
     * and  updates the game round.
     *
     * @param index must find an existing and untaken  bidding ticket.
      * @throws IllegalArgumentException if the currentPlayer is null
     * @throws BiddingTicketIsTaken if the indicated bidding ticket has already taken
     * @throws IndexOutOfBoundsException if the index is out of the range of bidding tickets
     * */
    void placeTotem(int index) throws BiddingTicketIsTaken,IndexOutOfBoundsException,IllegalArgumentException;

    /**
     * Send the winner or winners to the views
     * @throws GameNotFinished if the game isn't ended
    **/
    List<String> getWinners() throws GameNotFinished;


    public void pickCard(int index);
    /**
     * Allows for a client player gets foods
     * @throws IllegalStateException if it will be when there are less than 5 players
     **/
    public void pickFood() throws IllegalStateException;

    /**
     * Allows end the game
     **/
    public void endGame();
}
