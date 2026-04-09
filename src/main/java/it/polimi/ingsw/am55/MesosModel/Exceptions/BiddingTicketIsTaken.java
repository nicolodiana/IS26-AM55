package it.polimi.ingsw.am55.MesosModel.Exceptions;

public class BiddingTicketIsTaken extends RuntimeException {
    public BiddingTicketIsTaken(String message) {
        super(message);
    }
}
