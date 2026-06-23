package it.polimi.ingsw.am55.MesosModel.Exceptions;

/**
 * Thrown when a player attempts to obtain a building card they cannot afford.
 */
public class CannotAffordBuildingException extends RuntimeException {
    /**
     * Creates an exception with the specified detail message.
     *
     * @param message the detail message describing the affordability error
     */
    public CannotAffordBuildingException(String message) {
        super(message);
    }
}
