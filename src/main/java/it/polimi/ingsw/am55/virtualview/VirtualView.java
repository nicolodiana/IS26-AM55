package it.polimi.ingsw.am55.virtualview;

import it.polimi.ingsw.am55.message.MessageToClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote callback interface implemented by the client-side virtual view.
 * <p>
 * The server uses this abstraction to deliver messages to a connected client
 * without depending on the underlying transport implementation.
 */
public interface VirtualView extends Remote {

    /**
     * Callback used by the server to send a generic message containing
     * a state update or a technical notification to the client.
     *
     * @param message message to deliver to the client
     * @throws RemoteException if the remote callback fails
     */
    void onMessage(MessageToClient message) throws RemoteException;

    /**
     * Returns the permanent player identifier associated with this view.
     *
     * @return player identifier, or {@code null} if the client is still in the lobby
     * @throws RemoteException if the remote callback fails
     */
    String getPlayerId() throws RemoteException;

    /**
     * Associates the permanent player identifier with this view after a successful
     * create-game or join-game operation.
     *
     * @param playerId player identifier to store in the view
     * @throws RemoteException if the remote callback fails
     */
    void setPlayerId(String playerId) throws RemoteException;

    /**
     * Closes the client-side endpoint and releases the resources owned by the view.
     *
     * @throws RemoteException if the remote close operation fails
     */
    void close() throws RemoteException;
}
