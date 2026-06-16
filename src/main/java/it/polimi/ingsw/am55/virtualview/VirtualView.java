package it.polimi.ingsw.am55.virtualview;

import it.polimi.ingsw.am55.message.MessageToClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualView extends Remote {

    /**
     * Metodo di callback usato dal server per inviare al client
     * un messaggio generico contenente un aggiornamento di stato.
     */
    void onMessage(MessageToClient message) throws RemoteException;

    String getPlayerId() throws RemoteException;

    void setPlayerId(String playerId) throws RemoteException;

    void close() throws RemoteException;
}