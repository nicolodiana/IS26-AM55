package it.polimi.ingsw.am55.dto.endgame;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Serializable DTO for one leaderboard row.
 * <p>It stores the ranking position, player nickname, prestige points, food points, and the date of the recorded match.
 */
public class LeaderBoardEntryView implements Serializable {

    /**
     * DTO field carrying the position value for client-side rendering.
     */
    private int position;
    /**
     * DTO field carrying the player nickname value for client-side rendering.
     */
    private String playerNickname;
    /**
     * DTO field carrying the pp point value for client-side rendering.
     */
    private int ppPoint;
    /**
     * DTO field carrying the food value for client-side rendering.
     */
    private int foodPoint;
    /**
     * DTO field carrying the date value for client-side rendering.
     */
    private Timestamp date;

    /**
     * Creates a leader board entry view from model data that can be sent to the client.
     *
     * @param position the position value
     * @param playerNickname the player nickname
     * @param ppPoint the prestige-point value involved in the operation
     * @param foodPoint the food value involved in the operation
     * @param date the date value
     */
    public LeaderBoardEntryView(int position, String playerNickname, int ppPoint, int foodPoint, Timestamp date) {
        this.position = position;
        this.playerNickname = playerNickname;
        this.ppPoint = ppPoint;
        this.foodPoint = foodPoint;
        this.date = date;
    }

    public int getPosition() {return this.position;}
    public String getPlayerNickname() {
        return playerNickname;
    }
    public int getPrestigePoint() {
        return ppPoint;
    }
    public int getFoodPoint() {
        return foodPoint;
    }
    public Timestamp getDate() {return this.date;}


}