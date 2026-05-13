package it.polimi.ingsw.am55.network.rmi.server;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.network.command.CreateGameCommand;
import it.polimi.ingsw.am55.network.command.JoinGameCommand;
import it.polimi.ingsw.am55.network.command.PickCardCommand;
import it.polimi.ingsw.am55.network.command.PickSpecialCommand;
import it.polimi.ingsw.am55.network.command.PlaceTotemCommand;
import it.polimi.ingsw.am55.network.rmi.client.VirtualViewRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi {

    private final ServerApplication serverApplication;

    // L'EXECUTOR SERVICE MI SERVE PER RENDERE RMI ASINCRONO COME SOCKET ED EVITARE LENTEZZA NEL BROADCAST
    private final ExecutorService rmiExecutor = Executors.newSingleThreadExecutor();

    public RmiServer(ServerApplication serverApplication) throws RemoteException {
        super();
        this.serverApplication = serverApplication;
    }

    @Override
    public void connect(String playerId, VirtualViewRmi client) throws RemoteException {
        // connect rimane SINCRONA — deve registrare il client subito
        serverApplication.registerClient(playerId, client);
    }

    @Override
    public void createGame(String playerId, String totemColor, int numPlayers) throws RemoteException {
        rmiExecutor.submit(() -> {
            try { serverApplication.executeCommand(new CreateGameCommand(playerId, totemColor, numPlayers), null); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    @Override
    public void joinGame(String playerId, String totemColor) throws RemoteException {
        rmiExecutor.submit(() -> {
            try { serverApplication.executeCommand(new JoinGameCommand(playerId, totemColor), null); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    @Override
    public void placeTotem(String playerId, int index) throws RemoteException {
        rmiExecutor.submit(() -> {
            try { serverApplication.executeCommand(new PlaceTotemCommand(playerId, index), null); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    @Override
    public void pickCard(String playerId, int cardId) throws RemoteException {
        rmiExecutor.submit(() -> {
            try { serverApplication.executeCommand(new PickCardCommand(playerId, cardId), null); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    @Override
    public void pickSpecial(String playerId, int cardId) throws RemoteException {
        rmiExecutor.submit(() -> {
            try { serverApplication.executeCommand(new PickSpecialCommand(playerId, cardId), null); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    public void shutdown() {
        rmiExecutor.shutdown();
    }
}