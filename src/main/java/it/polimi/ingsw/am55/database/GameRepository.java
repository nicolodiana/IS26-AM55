package it.polimi.ingsw.am55.database;

import it.polimi.ingsw.am55.dto.endgame.LeaderBoardEntryView;

import java.util.List;

/**
 * Repository interface for persisting completed games and retrieving leaderboard data.
 *
 * Implementations hide the concrete storage mechanism from the rest of the application,
 * allowing the game logic to depend on a stable persistence abstraction.
 *
 */
public interface GameRepository {

    /**
     * Stores the final result of a player for a completed game.
     *
     * @param gameId         identifier of the game associated with the result
     * @param playerNickname nickname of the player to register
     * @param ppPoint        final prestige points obtained by the player
     * @param foodPoint      remaining food points owned by the player
     */
    void registerPlayer(String gameId, String playerNickname, int ppPoint, int foodPoint);

    /**
     * Stores metadata about a completed game.
     *
     * @param gameID     unique identifier of the game to register
     * @param numPlayers number of players that participated in the game
     */
    void registerGame(String gameID, int numPlayers);

    /**
     * Retrieves the general leaderboard for games with the specified number of players.
     *
     * @param numPlayers number of players used to filter the games included in the ranking
     * @return leaderboard entries for the requested game size
     */
    List<LeaderBoardEntryView> getGeneralClassification(int numPlayers);
}
