package it.polimi.ingsw.am55.network.rmi.server;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.network.command.*;
import it.polimi.ingsw.am55.network.rmi.client.VirtualViewRmi;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi {

    private final ServerApplication serverApplication;

    public RmiServer(ServerApplication serverApplication) throws RemoteException {
        super();
        this.serverApplication = serverApplication;
    }

    @Override
    public void connect(String playerId, VirtualViewRmi client) throws RemoteException {
        serverApplication.registerClient(playerId, client);
    }

    @Override
    public void createGame(String playerId, String totemColor, int numPlayers) throws RemoteException {
        try {
            serverApplication.executeCommand(
                    new CreateGameCommand(playerId, totemColor, numPlayers),
                    null
            );
        } catch (Exception e) {
            throw new RemoteException("Errore durante createGame", e);
        }
    }

    @Override
    public void joinGame(String playerId, String totemColor) throws RemoteException {
        try {
            serverApplication.executeCommand(
                    new JoinGameCommand(playerId, totemColor),
                    null
            );
        } catch (Exception e) {
            throw new RemoteException("Errore durante joinGame", e);
        }
    }

    @Override
    public void placeTotem(String playerId, int index) throws RemoteException {
        try {
            serverApplication.executeCommand(
                    new PlaceTotemCommand(playerId, index),
                    null
            );
        } catch (Exception e) {
            throw new RemoteException("Errore durante placeTotem", e);
        }
    }

    @Override
    public void pickCard(String playerId, int cardId) throws RemoteException {
        try {
            serverApplication.executeCommand(
                    new PickCardCommand(playerId, cardId),
                    null
            );
        } catch (Exception e) {
            throw new RemoteException("Errore durante pickCard", e);
        }
    }

    @Override
    public void pickSpecial(String playerId, int cardId) throws RemoteException {
        try {
            serverApplication.executeCommand(
                    new PickSpecialCommand(playerId, cardId),
                    null
            );
        } catch (Exception e) {
            throw new RemoteException("Errore durante pickSpecial", e);
        }
    }

    /*
    Da aggiungere l' implementazione per la gestione del ping
    periodico inviato verso il server dal client.
     */
    @Override
    public void ping(VirtualView client) throws Exception {
        try {
            serverApplication.executeCommand(new PingCommand(),client);
        } catch (Exception e) {
            throw new RemoteException("Errore durante pickSpecial", e);
        }
    }

    @Override
    public void quitGame(String id) throws RemoteException {
        try {
            serverApplication.executeCommand(new QuitGameCommand(id), null);
        } catch (Exception e) {
            throw new RemoteException("Errore durante pickSpecial", e);
        }
    }

    @Override
    public void closeConnections(VirtualView sender) throws RemoteException {
        try {
            serverApplication.executeCommand(new CloseConnectionsCommand(), sender);
        } catch (Exception e) {
            throw new RemoteException("Errore durante pickSpecial", e);
        }
    }
}