package it.polimi.ingsw.am55.virtualview;

import it.polimi.ingsw.am55.message.MessageToClient;

import java.rmi.RemoteException;

public interface VirtualView {

    /**
     * Metodo di callback usato dal server per inviare al client
     * un messaggio generico contenente un aggiornamento di stato.
     */
    void onMessage(MessageToClient message) throws Exception;
    String getPlayerId() throws Exception;
    void setPlayerId(String playerId) throws Exception;
    //void pong()throws Exception;
    void close() throws Exception;
}