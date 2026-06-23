package it.polimi.ingsw.am55.MesosModel.Exceptions;

/**
 * Thrown when a player selects a totem color that is not permitted by the game.
 */
public class WrongTotemColor extends RuntimeException {
    /**
     * Creates an exception with the specified detail message.
     *
     * @param message the detail message describing the invalid totem color
     */
    public WrongTotemColor(String message) {
        super(message);
    }
}
