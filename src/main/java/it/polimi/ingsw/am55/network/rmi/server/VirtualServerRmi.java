package it.polimi.ingsw.am55.network.rmi.server;

import it.polimi.ingsw.am55.network.rmi.VirtualClient;
import it.polimi.ingsw.am55.network.rmi.VirtualServer;
import it.polimi.ingsw.am55.network.rmi.client.VirtualClientRmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerRmi extends Remote, VirtualServer {
    void updateLobbyState(String message, VirtualClientRmi vcr) throws RemoteException;
    void connectClient(int playerId, VirtualClientRmi virtualClientRmi) throws RemoteException;
    void endMatchConnection(int matchId) throws RemoteException;
    void createGame(int matchId, int playerId, int numPlayers, VirtualClientRmi vcr) throws RemoteException;
    void joinGame(int matchId, int playerId, VirtualClientRmi vcr) throws RemoteException;
}
