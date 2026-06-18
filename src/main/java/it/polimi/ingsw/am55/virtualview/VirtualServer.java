package it.polimi.ingsw.am55.virtualview;

import it.polimi.ingsw.am55.network.command.ServerCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote server interface exposed to clients.
 * <p>
 * It defines the logical client-server contract and remains independent from
 * the concrete communication technology used by the application.
 */
public interface VirtualServer extends Remote {

    /**
     * Receives a command sent by a client and forwards it to the server-side
     * command execution flow.
     *
     * @param command command requested by the client
     * @param sender client callback endpoint that sent the command
     * @throws RemoteException if the remote invocation fails
     */
    void receiveCommand(ServerCommand command, VirtualView sender) throws RemoteException;

    /**
     * Closes the remote server abstraction.
     *
     * @throws RemoteException if the remote close operation fails
     */
    void close() throws RemoteException;
}
