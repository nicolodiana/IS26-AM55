package it.polimi.ingsw.am55.MesosModel.Exceptions;

/**
 * Thrown when a player cannot be found on the active bidding trail.
 */
public class PlayerNotOnTrail extends RuntimeException {
    /**
     * Creates an exception with the specified detail message.
     *
     * @param message the detail message identifying the missing player
     */
    public PlayerNotOnTrail(String message) {
        super(message);
    }
}
