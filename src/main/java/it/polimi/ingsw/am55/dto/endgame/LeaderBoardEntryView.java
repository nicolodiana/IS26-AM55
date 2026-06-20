package it.polimi.ingsw.am55.dto.endgame;

import java.io.Serializable;
import java.sql.Timestamp;

public class LeaderBoardEntryView implements Serializable {


    private int position;
    private String playerNickname;
    private int ppPoint;
    private int foodPoint;
    private Timestamp date;

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