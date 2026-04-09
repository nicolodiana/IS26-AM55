package it.polimi.ingsw.am55.MesosModel.Exceptions;

public class GameAlreadyStarted extends RuntimeException {
    public GameAlreadyStarted(String message) {
        super(message);
    }
}
