package it.polimi.ingsw.am55.database;

import it.polimi.ingsw.am55.dto.endgame.LeaderBoardEntryView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-based implementation of {@link GameRepository} for the Mesos database.
 *
 * This class is responsible for opening a connection to the MySQL database and
 * persisting game and player results. It also retrieves leaderboard data used by
 * the end-game view.
 *
 */
public class DatabaseManger implements GameRepository {

    /**
     * Active JDBC connection used to execute database queries.
     */
    private Connection conn;

    /**
     * Creates a new database manager and opens a connection to the local Mesos database.
     *
     * If the connection cannot be established, the error is printed and the manager
     * remains without a valid connection.
     *
     */
    public DatabaseManger() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbmesos", "root", "root");
        } catch (SQLException e) {
            System.err.println("[DATABASE] Impossibile collegarsi alla base di dati: " + e.getMessage());
            System.out.println("[ERRORE CONNESSIONE MYSQL]");
            e.printStackTrace();
        }
    }

    /**
     * Registers the final result of a player for a specific game.
     *
     * The method stores the player's nickname, final prestige points, remaining food
     * points, and the identifier of the game the player participated in.
     *
     *
     * @param gameId         identifier of the game associated with the player result
     * @param playerNickname nickname of the player to register
     * @param ppPoint        final prestige points obtained by the player
     * @param foodPoint      remaining food points owned by the player
     */
    @Override
    public void registerPlayer(String gameId, String playerNickname, int ppPoint, int foodPoint) {
        String query = "INSERT INTO Player(nickname, ppPoints, foodPoints, gameId) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, playerNickname);
            pstmt.setInt(2, ppPoint);
            pstmt.setInt(3, foodPoint);
            pstmt.setString(4, gameId);

            pstmt.executeUpdate();

            System.out.println("[DATABASE] Player successfully registered in the database");
        } catch (SQLException e) {
            System.err.println("[DATABASE] Error saving player to database: " + e.getMessage());
        }
    }

    /**
     * Registers a new completed game in the database.
     *
     * The game is saved with its identifier, the current database timestamp, and the
     * number of players that participated in it.
     *
     *
     * @param gameID     unique identifier of the game to register
     * @param numPlayers number of players that participated in the game
     */
    @Override
    public void registerGame(String gameID, int numPlayers) {
        String query = "INSERT INTO Game (gameId,date, numPlayers) VALUES (?, NOW(), ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, gameID);
            pstmt.setInt(2, numPlayers);
            pstmt.executeUpdate();
            System.out.println("[DATABASE] Game successfully registered in the database");
        } catch (SQLException e) {
            System.err.println("[DATABASE] Error saving game to database " + e.getMessage());
        }
    }

    /**
     * Retrieves the general leaderboard for games played with the specified number of players.
     *
     * Players are ranked by prestige points in descending order and, in case of ties,
     * by remaining food points in descending order. The method returns a list of
     * {@link LeaderBoardEntryView} objects ready to be shown by the client-side view.
     *
     *
     * @param numPlayers number of players used to filter the games included in the ranking
     * @return leaderboard entries matching the requested game size; an empty list is returned if no data is found or a database error occurs
     */
    @Override
    public List<LeaderBoardEntryView> getGeneralClassification(int numPlayers) {
        List<LeaderBoardEntryView> leaderBoard = new ArrayList<>();

        String query = """
            SELECT
                DENSE_RANK() OVER (
                    ORDER BY p.ppPoints DESC,
                             p.foodPoints DESC
                ) AS rank_position,
                p.nickname,
                p.ppPoints AS final_prestige_points,
                p.foodPoints AS remaining_food,
                g.date AS game_date
            FROM Player p
            JOIN Game g ON g.gameId = p.gameId
            WHERE g.numPlayers = ?
            ORDER BY rank_position, p.nickname
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, numPlayers);

            try (ResultSet result = pstmt.executeQuery()) {
                while (result.next()) {
                    int position = result.getInt("rank_position");
                    String nickname = result.getString("nickname");
                    int prestigePoints = result.getInt("final_prestige_points");
                    int foodPoints = result.getInt("remaining_food");
                    Timestamp date = result.getTimestamp("game_date");

                    LeaderBoardEntryView entry = new LeaderBoardEntryView(
                            position,
                            nickname,
                            prestigePoints,
                            foodPoints,
                            date
                    );

                    leaderBoard.add(entry);
                }
            }

        } catch (SQLException e) {
            System.err.println("[DATABASE] Error retrieving ranking: " + e.getMessage());
        }

        return leaderBoard;
    }
}
