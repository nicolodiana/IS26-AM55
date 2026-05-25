package it.polimi.ingsw.am55.network.rmi.client;

import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualViewRmi extends Remote, VirtualView {

    @Override
    void onMessage(MessageToClient message) throws RemoteException;
    void close() throws RemoteException;
    void setPlayerId(String playerId) throws RemoteException;
    void pong() throws  RemoteException;
}