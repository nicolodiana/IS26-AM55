package it.polimi.ingsw.am55.MesosModel.Exceptions;

public class GameNotFinished extends RuntimeException {
    public GameNotFinished(String message) {
        super(message);
    }
}
