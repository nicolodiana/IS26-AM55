package it.polimi.ingsw.am55.MesosModel.Game;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Exceptions.*;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This interface contains all methods can be called by Controller
 **/
public interface GameModelInterface {
    /**
     * Returns the game's id
     * @return game's id
    **/
    GameView toView();
    //serve per ottenere il dto dello stato di gioco dal server
    String getIdGame();
    /**
     * A modifier method that allows a new player to join the game.
     * It allocates a new Player object using the two parameters received and adds it to the end of the players list.
     *
     * @param nickname that is the player's nickname to add in the game
     * @param totem that is the player's totem to add in the game
     * @throws TotemAlreadyUsed if the totem has been already taken
     * @throws PlayerNumberOutOfRange if player is equals or greater than 5
     * @throws NicknameAlreadyUsed if the nickname has been already taken
     * **/
    String addPlayer(String nickname, String totem) throws NicknameAlreadyUsed,TotemAlreadyUsed,PlayerNumberOutOfRange;
    /**
     * Returns the current numbers of players in the game
     * @return the number of players in the game
     **/
    int getNumPlayers();
    /**
     * Return if the game's state
     * @return GameState which consist of game's state
     **/
    GameState getGameState();

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
    void placeTotem(int index, String id) throws BiddingTicketIsTaken,IndexOutOfBoundsException,IllegalArgumentException;


    /**
     * Return the available color for totems in this game
     * @return available color for totems in this game
     * **/
    Set<String> getTotemColorsValid();
    /**
     * Executes the card picking action for the current player.
     * This method validates the card ID, ensures the player is allowed to pick from the
     * specified row (based on their bidding trail position), and handles the logic
     * for card acquisition. It also checks if the player's turn has ended to apply
     * bonuses or maluses, manages the transition to the next player, and triggers
     * round-end procedures (event resolution and board restoration) if the last
     * player has finished.
     *
     * @param index the unique identifier of the card to be picked.
     * @throws IllegalStateException if the game is not in the PICKCARD state.
     * @throws IllegalArgumentException if the provided card ID is out of the valid range (1-120).
     * @throws CantPickFromRow if the player has already reached their limit for the row containing the card.
     * @throws CannotAffordBuildingException if the player lacks sufficient food to pay for a building card.
     */
    void pickCard(int index,String idPlayer);

    /**
     * Allows for a client player gets foods
     * @throws IllegalStateException if it will be when there are less than 5 players
     **/
    void pickFood(String id) throws IllegalStateException;

    /**
     * Allows a controller terminates the game if one player crashed
     * **/
    void handleGameCrashed();

    /**
     * Ends the game and determines the winners.
     * The method selects the players with the highest number of victory points.
     * If multiple players are tied, a tie-break is applied based on the amount
     * of food. Only the players with the highest food among them remain winners.
     * @return map that cointains players winner with points
     */
    EndGameResultView endGame();

    List<ResolveEventView> eventResolve();


    void pickSpecial(int id,String idPlayer);

    void quitGame();

    boolean isInGame(String idPlayer);

    GameState getState();

    List<Player> getPlayers();
}
