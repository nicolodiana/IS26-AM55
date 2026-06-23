package it.polimi.ingsw.am55.MesosModel.Exceptions;

/**
 * Thrown when a player selects a totem color already assigned to another player.
 */
public class TotemAlreadyUsed extends RuntimeException {
    /**
     * Creates an exception with the specified detail message.
     *
     * @param message the detail message describing the duplicate totem
     */
    public TotemAlreadyUsed(String message) {
        super(message);
    }
}
