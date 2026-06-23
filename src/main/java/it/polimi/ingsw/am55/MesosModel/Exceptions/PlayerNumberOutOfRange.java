package it.polimi.ingsw.am55.MesosModel.Exceptions;

/**
 * Thrown when a game player count is outside the permitted range or capacity.
 */
public class PlayerNumberOutOfRange extends Exception {


    /**
     * Creates an exception with the specified detail message.
     *
     * @param msg the detail message describing the invalid player count
     */
    public PlayerNumberOutOfRange(String msg){
        super(msg);
    }
}
