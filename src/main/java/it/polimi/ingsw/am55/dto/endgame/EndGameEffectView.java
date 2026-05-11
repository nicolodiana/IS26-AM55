package it.polimi.ingsw.am55.dto.endgame;
//questo è il dto che prende esclusivamente gli effetti di fine partita applicati ad ogni player

import java.io.Serializable;

public class EndGameEffectView implements Serializable {

    private final String playerNickname;
    private final String description;
    private final int pointDelta;

    public EndGameEffectView(String playerNickname, String description, int pointDelta) {
        this.playerNickname = playerNickname;
        this.description = description;
        this.pointDelta = pointDelta;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public String getDescription() {
        return description;
    }

    public int getPointDelta() {
        return pointDelta;
    }
}