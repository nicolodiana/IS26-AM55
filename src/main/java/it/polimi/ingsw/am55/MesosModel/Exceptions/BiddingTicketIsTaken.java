package it.polimi.ingsw.am55.MesosModel.Exceptions;

/**
 * Thrown when a player is assigned to an occupied bidding ticket.
 */
public class BiddingTicketIsTaken extends RuntimeException {
    /**
     * Creates an exception with the specified detail message.
     *
     * @param message the detail message describing the occupied ticket
     */
    public BiddingTicketIsTaken(String message) {
        super(message);
    }
}
