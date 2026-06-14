package it.polimi.ingsw.am55.virtualview;

import it.polimi.ingsw.am55.network.command.ServerCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaccia che definisce i metodi che il client
 * può invocare sul server.
 * Rappresenta il contratto logico client -> server,
 * indipendente dalla tecnologia di comunicazione usata.
 */
public interface VirtualServer extends Remote {

    void receiveCommand(ServerCommand command, VirtualView sender) throws RemoteException;

    void close() throws RemoteException;
}