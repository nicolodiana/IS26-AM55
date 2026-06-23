package it.polimi.ingsw.am55.dto.endgame;

import java.io.Serializable;
/**
 * Serializable DTO describing one end-game effect applied to a player.
 * <p>It records the target player, a description, and the resulting prestige-point delta.
 */
public class EndGameEffectView implements Serializable {
    /**
     * DTO field carrying the player nickname value for client-side rendering.
     */
    private final String playerNickname;
    /**
     * Textual description shown for the represented effect or result.
     */
    private final String description;
    /**
     * Prestige-point delta assigned by this end-game effect.
     */
    private final int pointDelta;

    /**
     * Creates an end game effect view from model data that can be sent to the client.
     *
     * @param playerNickname the player nickname
     * @param description the description value
     * @param pointDelta the prestige-point value
     */
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