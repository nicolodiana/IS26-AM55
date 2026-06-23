package it.polimi.ingsw.am55.MesosModel.Exceptions;

/**
 * Thrown when a player attempts to select an event card from a board row.
 */
public class CannotPickEventCard extends RuntimeException {
    /**
     * Creates an exception with the specified detail message.
     *
     * @param message the detail message describing the invalid selection
     */
    public CannotPickEventCard(String message) {
        super(message);
    }
}
