package it.polimi.ingsw.am55.network.rmi.server;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.network.command.*;
import it.polimi.ingsw.am55.network.rmi.client.VirtualViewRmi;
import it.polimi.ingsw.am55.virtualview.VirtualView;

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
//appena mi connetto mi registro nella lobbymap della server application

    public void connect(String sessionId, VirtualViewRmi client) throws RemoteException {
        rmiExecutor.submit(() -> {
            try {
                serverApplication.executeCommand(
                        new RegisterLobbyCommand(sessionId),
                        client
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
//    come parametro dell'esecute command per create game e joingame la virtualview sender è null, perche non mi serve, gestisco
//    tutto tramite il session Id, tuttavia la firma di executecommand non posso modificarla perche poi nel registerLobbyClient
//    ho bisogno della virtual view per fare mapping view id nella mappa

    public void createGame(String playerId, String totemColor, int numPlayers, String sessionId) throws RemoteException {
        rmiExecutor.submit(() -> {
            try {
                serverApplication.executeCommand(
                        new CreateGameCommand(playerId, totemColor, numPlayers, sessionId),
                        null
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void joinGame(String playerId, String totemColor, String sessionId) throws RemoteException {
        rmiExecutor.submit(() -> {
            try {
                serverApplication.executeCommand(
                        new JoinGameCommand(playerId, totemColor, sessionId),
                        null
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



    public void placeTotem(String playerId, int index) throws RemoteException {
        rmiExecutor.submit(() -> {
            try { serverApplication.executeCommand(new PlaceTotemCommand(playerId, index), null); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }


    public void pickCard(String playerId, int cardId) throws RemoteException {
        rmiExecutor.submit(() -> {
            try { serverApplication.executeCommand(new PickCardCommand(playerId, cardId), null); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }


    public void pickSpecial(String playerId, int cardId) throws RemoteException {
        rmiExecutor.submit(() -> {
            try { serverApplication.executeCommand(new PickSpecialCommand(playerId, cardId), null); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    public void shutdown() {
        rmiExecutor.shutdown();
    }

    /*
    Da aggiungere l' implementazione per la gestione del ping
    periodico inviato verso il server dal client.
     */

    public void ping(VirtualView client) throws RemoteException {
        rmiExecutor.submit(()->{
            try {
                serverApplication.executeCommand(new PingCommand(),client);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public void quitGame(String id) throws RemoteException {
        rmiExecutor.submit(() -> {
            try {
                serverApplication.executeCommand(new QuitGameCommand(id), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void closeConnection(String playerId) throws RemoteException {
        rmiExecutor.submit(() -> {
            try {
                serverApplication.executeCommand(new CloseConnectionCommand(playerId), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



}