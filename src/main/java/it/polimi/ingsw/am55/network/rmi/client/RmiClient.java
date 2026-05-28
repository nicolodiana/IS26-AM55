package it.polimi.ingsw.am55.network.rmi.client;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.ClientCommands;
import it.polimi.ingsw.am55.network.ClientConnectionControl;
import it.polimi.ingsw.am55.network.rmi.server.VirtualServerRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Questa classe rappresenta la logica di rete del client implementata con tecnologia RMI.
 *
 * Si occupa di:
 * - mantenere il riferimento remoto al server
 * - registrarsi al server quando il playerId è noto
 * - ricevere i messaggi dal server tramite onMessage(...)
 * - inoltrare i messaggi ricevuti al ClientModel
 * - offrire metodi di rete chiamabili dal ClientController
 */
public class RmiClient extends UnicastRemoteObject implements VirtualViewRmi, ClientCommands, ClientConnectionControl {

    private static final long PING_INTERVAL_MS = 1500;
    private static final long SERVER_TIMEOUT_MS = 10000;

    private final VirtualServerRmi server;
    private final ClientModel model;
    private final String sessionId;

    private volatile String playerId;
    private Long lastPingFromServer;
    private volatile boolean checkerAliverActive = false;
    private boolean pingStarted;
    private final Object pingLock;

    private Timer timer;
    private Timer timerChekerAliver;

    public RmiClient(VirtualServerRmi server, ClientModel model) throws RemoteException {
        super();
        this.server = server;
        this.model = model;
        this.playerId = null;
        this.timer = new Timer(true);
        this.timerChekerAliver = new Timer(true);
        this.pingStarted = false;
        this.pingLock = new Object();
        this.sessionId = UUID.randomUUID().toString();// genero id session randomico e univoco per ogni client
        server.connect(this.sessionId, this);
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
            System.out.println("[RMI_CLIENT] Errore gestione messaggio: " + e.getMessage());
        }
    }

    @Override
    public void close() throws RemoteException {
        pingStarted = false;
        checkerAliverActive = false;
        timer.cancel();
        timerChekerAliver.cancel();
        System.out.println("[RMI_CLIENT] Chiusura avvenuta.");
    }

    @Override
    public String getPlayerId() {
        return this.playerId;
    }

    @Override
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

//    @Override
//    public synchronized void closeConnection() {
//        if (closed) {
//            return;
//        }
//
//        closed = true;
//        pingStarted = false;
//        checkerAliverActive = false;
//
//        try {
//            timer.cancel();
//        } catch (Exception ignored) {
//        }
//
//        try {
//            timerChekerAliver.cancel();
//        } catch (Exception ignored) {
//        }
//
//        try {
//            UnicastRemoteObject.unexportObject(this, true);
//        } catch (Exception ignored) {
//        }
//
//        System.out.println("[RMI_CLIENT] Chiusura avvenuta.");
//    }

    @Override
    public void createGame(String playerId, String totemColor, int numPlayers) throws RemoteException {
        server.createGame(playerId, totemColor, numPlayers, sessionId);
    }

    @Override
    public void joinGame(String playerId, String totemColor) throws RemoteException {
        server.joinGame(playerId, totemColor, sessionId);
    }

    @Override
    public void placeTotem(String playerId, int index) throws RemoteException {
        if (playerId == null) {
            throw new RemoteException("Player non ancora registrato tramite createGame/joinGame.");
        }

        server.placeTotem(playerId, index);
    }

    @Override
    public void pickCard(String playerId, int cardId) throws RemoteException {
        if (this.playerId == null) {
            throw new RemoteException("Player non ancora registrato tramite createGame/joinGame.");
        }

        server.pickCard(playerId, cardId);
    }

    @Override
    public void pickSpecial(String playerId, int cardId) throws RemoteException {
        if (this.playerId == null) {
            throw new RemoteException("Player non ancora registrato tramite createGame/joinGame.");
        }

        server.pickSpecial(playerId, cardId);
    }

    @Override
    public void quitGame(String playerId) throws Exception {
        server.quitGame(playerId);
    }

    @Override
    public void quitLobby() throws Exception {
        server.quitLobby(this.sessionId);
    }

    /*
     * Avvia il ping periodico verso il server.
     * synchronized serve per non schedulare due TimerTask se arrivano due StartPingMessage quasi insieme.
     */
    @Override
    public void startPing() {
        if (pingStarted) {
            return;
        }
        pingStarted = true;



//        timer = new Timer(true);
//        timerChekerAliver = new Timer(true);
//
//        startAliveChecker();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if(!checkerAliverActive){
//                        synchronized (pingLock) {
//                            lastPingFromServer = System.currentTimeMillis();
//                        }
                        checkerAliverActive = true;
                        checkerAliver();
                    }
                    pingToServer();
                } catch (Exception e) {
                    /*
                     * Non chiudo qui basandomi sull'eccezione.
                     * Se il server non risponde davvero, sarà startAliveChecker() a chiudere
                     * dopo SERVER_TIMEOUT_MS senza PongMessage.
                     */
                    System.out.println("[RMI_CLIENT] Invio ping fallito");
                }
            }
        }, 0, 1500);
    }

    private void checkerAliver() {
        if (!checkerAliverActive) {
            return;
        }

        timerChekerAliver.schedule(new TimerTask() {
            @Override
            public void run() {
                long elapsed;
                synchronized (pingLock) {
                    elapsed = System.currentTimeMillis() - lastPingFromServer;
                    //System.out.println(lastPingFromServer);
                }
                if (elapsed > 8000) {
                    try {
                        System.out.println("[RMI_CLIENT] Server non raggiungibile: chiudo il client.");
                        close();
                    } catch (RemoteException e) {
                        System.out.println("[RMI_CLIENT] Impossibile chiudere il client: " + e.getMessage());
                    }
                }
            }
        }, 1500,1500);
    }

    @Override
    public void stopPing() {
        pingStarted = false;
        checkerAliverActive = false;

        timer.cancel();
        timerChekerAliver.cancel();

    }

    @Override
    public void pongFromSever() {
        synchronized (pingLock){
            lastPingFromServer = System.currentTimeMillis();
        }
    }

    @Override
    public void closeConnection() {
//        try {
//            close();
//        } catch (Exception e) {
//            System.out.println("[RMI_CLIENT] Errore chiusura client: " + e.getMessage());
//        }
    }

    private void pingToServer() throws Exception {
        server.ping(this);
    }
}
