package it.polimi.ingsw.am55.MesosModel.Exceptions;

/**
 * Thrown when a player attempts to exceed the selection limit of a board row.
 */
public class CantPickFromRow extends RuntimeException {
    /**
     * Creates an exception with the specified detail message.
     *
     * @param message the detail message describing the row selection error
     */
    public CantPickFromRow(String message) {
        super(message);
    }
}
