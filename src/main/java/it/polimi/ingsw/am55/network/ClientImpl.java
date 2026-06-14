package it.polimi.ingsw.am55.network;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.ClientConnectionControl;
import it.polimi.ingsw.am55.network.command.PingCommand;
import it.polimi.ingsw.am55.network.command.RegisterLobbyCommand;
import it.polimi.ingsw.am55.network.command.ServerCommand;
import it.polimi.ingsw.am55.virtualview.VirtualServer;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class ClientImpl extends UnicastRemoteObject implements VirtualView, ClientConnectionControl {

    private static final long serialVersionUID = 1L;

    private static final long PING_INTERVAL_MS = 1500;
    private static final long SERVER_TIMEOUT_MS = 8000;

    private final VirtualServer server;
    private final ClientModel model;
    private final String sessionId;

    private volatile String playerId;

    private final Object pingLock;
    private long lastPingFromServer;

    private Timer pingTimer;
    private Timer aliveCheckerTimer;
    private int missedPongs = 0;

    private volatile boolean pingStarted;
    private volatile boolean aliveCheckerStarted;

    public ClientImpl(VirtualServer server, ClientModel model) throws RemoteException {
        super();

        this.server = server;
        this.model = model;
        this.sessionId = UUID.randomUUID().toString();

        this.playerId = null;

        this.pingLock = new Object();
//        this.lastPingFromServer = System.currentTimeMillis();

        this.pingTimer = new Timer(true); //Deamon perché sono thread di supporto che permettono alla JVM di terminare anche se essi stanno
        //ancora in esecuzione
        this.aliveCheckerTimer = new Timer(true);

        this.pingStarted = false;
        this.aliveCheckerStarted = false;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void connect() throws RemoteException {
        sendCommand(new RegisterLobbyCommand(sessionId));
    }

    public void sendCommand(ServerCommand command) throws RemoteException {
        server.receiveCommand(command, this);
    }

    @Override
    public void onMessage(MessageToClient message) throws RemoteException {
        try {
            if (message.shouldUpdateModel()) {
                synchronized (model) {
                    model.update(message);
                }
            }

            message.executeClientNetworkAction(this);

        } catch (Exception e) {
            System.out.println("[CLIENT_IMPL] Errore gestione messaggio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getPlayerId() throws RemoteException {
        return playerId;
    }

    @Override
    public void setPlayerId(String playerId) throws RemoteException {
        this.playerId = playerId;
    }

    @Override
    public void startPing() {
        System.out.println("[CLIENT_IMPL] Ping inviato da " + sessionId + " thread=" + Thread.currentThread().getName());
        if (pingStarted) {
            return;
        }

        pingStarted = true;

        startAliveChecker();
        aliveCheckerStarted = true;

        pingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!aliveCheckerStarted) {
                        aliveCheckerStarted = true;
                        startAliveChecker();
                    }

                    sendCommand(new PingCommand());

                } catch (Exception e) {
//                    System.out.println("[CLIENT_IMPL] Invio ping fallito: " + e.getMessage());
                }
            }
        }, 0, PING_INTERVAL_MS);
    }

    private void startAliveChecker() {
        aliveCheckerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                long elapsed;

                synchronized (pingLock) {
                    elapsed = System.currentTimeMillis() - lastPingFromServer;
                }

                if (elapsed > SERVER_TIMEOUT_MS) {
                    System.out.println("[CLIENT_IMPL] Server non raggiungibile: chiudo il client.");
                    closeConnection();
//                    missedPongs++;
//                    System.out.println("[CLIENT_IMPL] pong mancato " + missedPongs + "/3, elapsed " + elapsed);
//
//                    if (missedPongs >= 3) {
//                        System.out.println("[CLIENT_IMPL] Server non raggiungibile: chiudo il client.");
//                        closeConnection();
//                    }
                }
            }
        }, PING_INTERVAL_MS, PING_INTERVAL_MS);
    }

    @Override
    public void stopPing() {
        pingStarted = false;
        aliveCheckerStarted = false;

        try {
            pingTimer.cancel();
        } catch (Exception ignored) {
        }

        try {
            aliveCheckerTimer.cancel();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void pongFromSever() {
        synchronized (pingLock) {
            lastPingFromServer = System.currentTimeMillis();
            //this.missedPongs = 0;
        }
    }

    @Override
    public void closeConnection() {
        try {
            close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void close() throws RemoteException {
        stopPing();
        if (server instanceof AutoCloseable) {
            try {
                ((AutoCloseable) server).close();
            } catch (Exception ignored) {
            }
        }
        //Io, oggetto remoto client, non voglio più ricevere chiamate RMI.
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (Exception ignored) {
        }

        System.out.println("[CLIENT_IMPL] Client chiuso per " + sessionId);
    }
}