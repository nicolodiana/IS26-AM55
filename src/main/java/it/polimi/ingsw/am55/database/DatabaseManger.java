package it.polimi.ingsw.am55.database;
import it.polimi.ingsw.am55.dto.endgame.LeaderBoardEntryView;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseManger implements GameRepository {

    private Connection conn;

    public DatabaseManger() {
        try{
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mesosdb","root","root");
        }catch(SQLException e){
            System.err.println("[DATABASE] Impossibile collegarsi alla base di dati: "+e.getMessage());
        }
    }


    @Override
    public void registerPlayer(String gameId, String playerNickname, int ppPoint, int foodPoint) {
        String query = "INSERT INTO Players(nickname, ppPoints, foodPoints, gameId) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, playerNickname);
            pstmt.setInt(2, ppPoint);
            pstmt.setInt(3, foodPoint);
            pstmt.setString(4, gameId);

            pstmt.executeUpdate();

            System.out.println("[DATABASE] Giocatore registrato con successo nella base di dati");
        } catch (SQLException e) {
            System.err.println("[DATABASE] Errore durante il salvataggio del player nella base di dati: " + e.getMessage());
        }
    }


    /*
    vantaggi principali dell' uso di preparedStatement:

    evita SQL injection;
    gestisce bene stringhe, numeri e date;
    rende il codice più pulito;
    il database può ottimizzare meglio la query.
    * */
    @Override
    public void registerGame(String gameID, int numPlayers)  {
        String query = "INSERT INTO Game (gameId,date, numPlayers) VALUES (?, NOW(), ?)";
        //“uso questa query preparata e poi la chiudo automaticamente”.
        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setString(1, gameID);
            pstmt.setInt(2, numPlayers);
            pstmt.executeUpdate();
            System.out.println("[DATABASE] Game registrato con successo nella base di dati");
        }catch(SQLException e){
            System.err.println("[DATABASE] Errore durante il salvataggio del game nella base di dati "+e.getMessage());
        }
    }


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
                p.foodPoints AS remaining_food
            FROM Players p
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

                    LeaderBoardEntryView entry = new LeaderBoardEntryView(
                            position,
                            nickname,
                            prestigePoints,
                            foodPoints
                    );

                    leaderBoard.add(entry);
                }
            }

        } catch (SQLException e) {
            System.err.println("[DATABASE] Errore durante il recupero della classifica: " + e.getMessage());
        }

        return leaderBoard;
    }



}
