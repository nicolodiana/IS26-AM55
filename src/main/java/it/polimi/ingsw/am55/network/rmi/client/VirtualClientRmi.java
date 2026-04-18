package it.polimi.ingsw.am55.network.rmi.client;

import it.polimi.ingsw.am55.controller.ClientController;
import it.polimi.ingsw.am55.network.rmi.VirtualClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualClientRmi extends Remote, VirtualClient {
    void setClientController(ClientController cc) throws RemoteException;
    void updateStateLobby(String message) throws RemoteException;
    String getNickname() throws RemoteException;
    String getTotem() throws RemoteException;

}
