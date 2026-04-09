package it.polimi.ingsw.am55.MesosModel.Exceptions;

public class NicknameAlreadyUsed extends RuntimeException {
    public NicknameAlreadyUsed(String message) {
        super(message);
    }
}
