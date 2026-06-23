package it.polimi.ingsw.am55.MesosModel.Exceptions;

/**
 * Thrown when a player joins a game with a nickname already in use.
 */
public class NicknameAlreadyUsed extends RuntimeException {
    /**
     * Creates an exception with the specified detail message.
     *
     * @param message the detail message describing the duplicate nickname
     */
    public NicknameAlreadyUsed(String message) {
        super(message);
    }
}
