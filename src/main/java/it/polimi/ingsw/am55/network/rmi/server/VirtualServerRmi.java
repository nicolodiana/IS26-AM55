package it.polimi.ingsw.am55.network.rmi.server;



import it.polimi.ingsw.am55.network.rmi.client.VirtualViewRmi;
import it.polimi.ingsw.am55.virtualview.VirtualServer;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Questa interfaccia specializza VirtualServer per la tecnologia RMI.
 *
 * Definisce i metodi remoti esposti dal server ai client.
 */
public interface VirtualServerRmi extends Remote  {

    /**
     * Mantenuto per compatibilità con versioni precedenti.
     * La registrazione ora avviene solo dopo una create/join valida.
     */
    void connect(String playerId, VirtualViewRmi client) throws RemoteException;

    void createGame(String playerId, String totemColor, int numPlayers, String sessionId) throws RemoteException;

    void joinGame(String playerId, String totemColor, String sessionId) throws RemoteException;


    void placeTotem(String playerId, int index) throws RemoteException;

    void pickCard(String playerId, int cardId) throws RemoteException;

    void pickSpecial(String playerId, int cardId) throws RemoteException;

    void ping(VirtualView client) throws Exception;

    void quitGame(String id) throws RemoteException;

    void closeConnection(String playerId) throws RemoteException;

    /*
    altri metodi da aggiungere: dovranno poi anche aggiungersi dentro RMIServer che

    @Override
    void endTurn(String playerId) throws RemoteException;
    */
}
