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
 * Include anche il metodo connect(...) per registrare la callback remota del client.
 */
public interface VirtualServerRmi extends Remote, VirtualServer {

    /**
     * Metodo usato dal client per registrarsi presso il server,
     * così il server può effettuare callback su di lui tramite onMessage(...).
     */
    void connect(String playerId, VirtualViewRmi client) throws RemoteException;

    void createGame(String playerId, String totemColor, int numPlayers) throws RemoteException;

    void joinGame(String playerId, String totemColor) throws RemoteException;

    void placeTotem(String playerId, int index) throws RemoteException;

    void pickCard(String playerId, int cardId) throws RemoteException;

    void pickSpecial(String playerId, int cardId) throws RemoteException;

    void ping(VirtualView client) throws Exception;

    void quitGame(String id) throws RemoteException;

    void closeConnections(VirtualView sender) throws RemoteException;

    /*
    altri metodi da aggiungere: dovranno poi anche aggiungersi dentro RMIServer che

    @Override
    void endTurn(String playerId) throws RemoteException;
    */
}