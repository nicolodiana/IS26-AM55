package it.polimi.ingsw.am55.network;

import it.polimi.ingsw.am55.controller.GameController;
import it.polimi.ingsw.am55.message.GameCrashedBroadcast;
import it.polimi.ingsw.am55.message.LobbyStatusMessage;
import it.polimi.ingsw.am55.message.MessageDelivery;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.message.PongMessage;
import it.polimi.ingsw.am55.message.QuitLobbyMessage;
import it.polimi.ingsw.am55.message.StartPingMessage;
import it.polimi.ingsw.am55.network.command.PingCommand;
import it.polimi.ingsw.am55.network.command.ServerCommand;
import it.polimi.ingsw.am55.virtualview.VirtualServer;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApplication extends UnicastRemoteObject implements VirtualServer, MessageDelivery {

    private static final long serialVersionUID = 1L;

    private static final long PING_TIMEOUT_MS = 30_000;

    private final GameController controller;

    private final Map<String, VirtualView> gameClients;
    private final Map<String, VirtualView> lobbyClients;
    private final Map<VirtualView, Long> lastPingByClient;
    private final Map<String, String> playerIdBySession;

    private final Object gameLock = new Object();

    /*
     * Executor usato SOLO per RMI.
     *
     * RMI entra da receiveCommand(...), che sottomette il command a questo executor.
     * Socket invece NON entra da receiveCommand(...): il ClientSkeleton legge dallo stream
     * e chiama direttamente executeCommand(...).
     */
    private final ExecutorService rmiExecutor;
    private final ExecutorService pingExecutor;

    private Timer pingTimer;
    private boolean aliveCheckerStarted;

    public ServerApplication() throws RemoteException {
        super();

        this.controller = new GameController();

        this.lobbyClients = new HashMap<>();
        this.gameClients = new HashMap<>();
        this.lastPingByClient = new HashMap<>();

        this.pingTimer = new Timer(true);
        this.aliveCheckerStarted = false;

        this.rmiExecutor = Executors.newSingleThreadExecutor();
        this.pingExecutor = Executors.newSingleThreadExecutor();
        this.playerIdBySession = new HashMap<>();

        System.out.println("[SERVER_APP] ServerApplication creata.");
    }

    /*
     * Entry point RMI.
     *
     * ClientImpl RMI chiama:
     *      server.receiveCommand(command, this)
     *
     * Qui rendiamo asincrona la chiamata RMI, come richiesto dal prof.
     */
    @Override
    public void receiveCommand(ServerCommand command, VirtualView sender) throws RemoteException {
        ExecutorService executor = command instanceof PingCommand ? pingExecutor : rmiExecutor;
        executor.submit(() -> {
            try {
                executeCommand(command, sender);
            } catch (Exception e) {
                System.out.println("[SERVER_APP] Errore executeCommand da RMI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /*
     * Entry point logico comune.
     *
     * RMI:
     *      receiveCommand(...) -> executor -> executeCommand(...)
     *
     * Socket:
     *      ClientSkeleton legge command da input stream -> executeCommand(...)
     */
    public void executeCommand(ServerCommand command, VirtualView sender) throws Exception {
        if (sender != null) {
            synchronized (lastPingByClient) {
                lastPingByClient.put(sender, System.currentTimeMillis());
            }
        }
        System.out.println("[SERVER_APP] Esecuzione command: "
                + command.getClass().getSimpleName()
                + ", sender = "
                + (sender == null ? "null" : sender.getClass().getSimpleName()));
        if (command.requiresLock()) {
            synchronized (gameLock) {
                command.execute(this, sender);
            }
        } else {
            command.execute(this, sender);
        }

        System.out.println("[SERVER_APP] Command completato: "
                + command.getClass().getSimpleName());
    }

    public void registerLobbyClient(String sessionId, VirtualView client) throws Exception {
        if (sessionId == null || client == null) {
            System.out.println("[SERVER_APP] registerLobbyClient ignorato: sessionId/client null.");
            return;
        }

        synchronized (lobbyClients) {
            lobbyClients.put(sessionId, client);
        }

        synchronized (lastPingByClient) {
            lastPingByClient.put(client, System.currentTimeMillis());
        }

        sendToSession(sessionId, new StartPingMessage());
        startAliveChecker();

        System.out.println("[SERVER_APP] Client registrato in lobby. SessionId = " + sessionId);
    }

    public void createGame(String playerId, String totemColor, int numPlayers, String sessionId) throws Exception {
        MessageToClient message =
                controller.createGame(playerId, totemColor, numPlayers);

        completeConnectionSetup(playerId, sessionId, message);
    }

    public void joinGame(String playerId, String totemColor, String sessionId) throws Exception {
        MessageToClient message =
                controller.joinGame(playerId, totemColor);

        completeConnectionSetup(playerId, sessionId, message);
    }

    private void completeConnectionSetup(String playerId, String sessionId, MessageToClient message) throws Exception {
        VirtualView client;

        System.out.println("[PING] ricevuto. sessionId=" + sessionId
                + ", playerId=" + playerId
                + ", lobby=" + lobbyClients.keySet()
                + ", game=" + gameClients.keySet()
                + ", playerIdBySession=" + playerIdBySession);

        synchronized (lobbyClients) {
            client = lobbyClients.get(sessionId);
        }

        if (client == null) {

            System.out.println("[SERVER_APP] Sessione lobby non trovata: " + sessionId);
            return;
        }

        if (!message.isConnectionSetupSuccessful()) {
            sendToSession(sessionId, message);
            return;
        }

        synchronized (lobbyClients) {
            lobbyClients.remove(sessionId);
        }

        synchronized (playerIdBySession) {
            playerIdBySession.put(sessionId, playerId);
            System.out.println("[SERVER_APP] playerIdBySession aggiornato = " + playerIdBySession);
        }
        //client.setPlayerId(playerId);

        synchronized (gameClients) {
            gameClients.put(playerId, client);
        }

        System.out.println("[SERVER_APP] Client spostato da lobby a game: " + playerId);

        message.deliver(playerId, this);

        broadcastLobbyStatus();
    }

    public void placeTotem(String playerId, int index) throws Exception {
        System.out.println("[SERVER_APP] placeTotem chiamato da: "
                + playerId
                + ", index = "
                + index);

        MessageToClient message = controller.placeTotem(playerId, index);

        System.out.println("[SERVER_APP] placeTotem ha prodotto messaggio: "
                + message.getClass().getSimpleName());

        message.deliver(playerId, this);
    }

    public void pickCard(String playerId, int cardId) throws Exception {
        System.out.println("[SERVER_APP] pickCard chiamato da: "
                + playerId
                + ", cardId = "
                + cardId);

        MessageToClient message = controller.pickCard(playerId, cardId);

        System.out.println("[SERVER_APP] pickCard ha prodotto messaggio: "
                + message.getClass().getSimpleName());

        message.deliver(playerId, this);
    }

    public void pickSpecial(String playerId, int cardId) throws Exception {
        System.out.println("[SERVER_APP] pickSpecial chiamato da: "
                + playerId
                + ", cardId = "
                + cardId);

        MessageToClient message = controller.pickSpecial(playerId, cardId);

        System.out.println("[SERVER_APP] pickSpecial ha prodotto messaggio: "
                + message.getClass().getSimpleName());

        message.deliver(playerId, this);
    }

    public void quitGame(String playerId) throws Exception {
        System.out.println("[SERVER_APP] quitGame chiamato da: " + playerId);

        MessageToClient message = controller.quitGame(playerId);

        message.deliver(playerId, this);

        System.out.println("[SERVER_APP] Broadcast di quit completato.");

        stopAliveChecker();

        synchronized (gameClients) {
            for (VirtualView gameClient : gameClients.values()) {
                try {
                    gameClient.close();
                } catch (Exception ignored) {
                }
            }

            gameClients.clear();
            System.out.println("[SERVER_APP] Game client map svuotata " + gameClients);
        }

        synchronized (lobbyClients) {
            for (VirtualView lobbyClient : lobbyClients.values()) {
                try {
                    lobbyClient.close();
                } catch (Exception ignored) {
                }
            }

            lobbyClients.clear();
            System.out.println("[SERVER_APP] Lobby client map svuotata " + lobbyClients);
        }

        synchronized (lastPingByClient) {
            lastPingByClient.clear();
            System.out.println("[SERVER_APP] Ping map svuotata " + lastPingByClient);
        }
    }

    public void quitLobby(String sessionId) throws Exception {
        VirtualView sender;

        MessageToClient message = new QuitLobbyMessage();
        message.deliver(sessionId, this);

        synchronized (lobbyClients) {
            sender = lobbyClients.get(sessionId);
            lobbyClients.remove(sessionId);
        }

        synchronized (lastPingByClient) {
            if (sender != null) {
                lastPingByClient.remove(sender);
            }
        }

        if (sender != null) {
            try {
                sender.close();
            } catch (Exception ignored) {
            }
        }

        System.out.println("[SERVER_APP] Client rimosso dalla lobby. SessionId = " + sessionId);
    }

    public void ping(String sessionId, String playerId, VirtualView sender) throws Exception {
        if (sender == null) {
            return;
        }

        synchronized (lastPingByClient) {
            lastPingByClient.put(sender, System.currentTimeMillis());
        }

        System.out.println("[PING] ricevuto. sessionId=" + sessionId
            + ", playerId=" + playerId
            + ", lobby=" + lobbyClients.keySet()
            + ", game=" + gameClients.keySet()
            + ", playerIdBySession=" + playerIdBySession);

        /**
         * Handles the lobby -> game transition.
         * During this phase the client may still send PingCommand with a null
         * playerId while sessionId is no longer in the lobby.
         * Recovering the playerId from the sessionId allows the server to send
         * the PongMessage correctly and prevents false disconnections
        */
        if (playerId == null && sessionId != null) {
            synchronized (playerIdBySession) {
                playerId = playerIdBySession.get(sessionId);
            }
        }

        System.out.println("[PING] dopo recovery playerId=" + playerId);


        if (playerId != null) {
            synchronized (gameClients) {
                if (gameClients.containsKey(playerId)) {
                    System.out.println("[PING] mando Pong a game playerId=" + playerId);
                    sendTo(playerId, new PongMessage());
                    return;
                }
            }
        }

        if (sessionId != null) {
            synchronized (lobbyClients) {
                if (lobbyClients.containsKey(sessionId)) {
                    System.out.println("[PING] mando Pong a lobby sessionId=" + sessionId);
                    sendToSession(sessionId, new PongMessage());
                    return;
                }
            }
        }


        pong(sender);
    }

    private void pong(VirtualView sender) throws Exception {
        synchronized (gameClients) {
            /*String playerId = sender.getPlayerId();

            if (playerId != null && gameClients.containsKey(playerId)) {
                sendTo(playerId, new PongMessage());
                System.out.println("[SERVER_APP] PongMessage game client " + playerId);
                return;
            }*/
            for (Map.Entry<String, VirtualView> entry : gameClients.entrySet()) {
                if (entry.getValue().equals(sender)) {
                    sendTo(entry.getKey(), new PongMessage());
                    System.out.println("[SERVER_APP] PongMessage game client ");
                    return;
                }
            }

            System.out.println("[SERVER_APP] Pong ignorato: sender non trovato");
        }

        synchronized (lobbyClients) {
            for (Map.Entry<String, VirtualView> entry : lobbyClients.entrySet()) {
                if (entry.getValue().equals(sender)) {
                    sendToSession(entry.getKey(), new PongMessage());
                    System.out.println("[SERVER_APP] PongMessage lobby client "
                            + sender.getClass().getSimpleName());
                    return;
                }
            }
        }
    }

    private synchronized void startAliveChecker() {
        if (aliveCheckerStarted) {
            return;
        }

        aliveCheckerStarted = true;

        pingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    checkPingTimeouts();
                } catch (Exception e) {
                    System.out.println("[PING] Error alive checker: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, 10_000, 5_000);

        System.out.println("[PING] Alive checker AVVIATO");
    }

    private synchronized void stopAliveChecker() {
        try {
            pingTimer.cancel();
        } catch (Exception ignored) {
        }

        pingTimer = new Timer(true);
        aliveCheckerStarted = false;

        System.out.println("[PING] Alive checker FERMATO");
    }

    private void checkPingTimeouts() {
        synchronized (gameLock) {
            List<VirtualView> disconnectedClients = new ArrayList<>();
            long now = System.currentTimeMillis();

            synchronized (lastPingByClient) {
                for (Map.Entry<VirtualView, Long> entry : lastPingByClient.entrySet()) {
                    VirtualView client = entry.getKey();
                    long lastPing = entry.getValue();
                    long elapsed = now - lastPing;

                    if (elapsed > PING_TIMEOUT_MS) {
                        disconnectedClients.add(client);
                    }
                }
            }

            handleClientDisconnection(disconnectedClients);
        }
    }

    private void handleClientDisconnection(List<VirtualView> disconnectedClients) {
        if (disconnectedClients == null || disconnectedClients.isEmpty()) {
            return;
        }

        synchronized (lastPingByClient) {
            for (VirtualView client : disconnectedClients) {
                lastPingByClient.remove(client);
            }
        }

        List<String> sessionIds = new ArrayList<>();

        synchronized (lobbyClients) {
            for (Map.Entry<String, VirtualView> entry : lobbyClients.entrySet()) {
                VirtualView lobbyClient = entry.getValue();

                if (disconnectedClients.contains(lobbyClient)) {
                    sessionIds.add(entry.getKey());
                }
            }
        }

        if (!sessionIds.isEmpty()) {
            System.out.println("[SERVER_APP] Client disconnessi in lobby: " + sessionIds);

            for (String sessionId : sessionIds) {
                try {
                    sendToSession(sessionId, new QuitLobbyMessage());
                } catch (Exception ignored) {
                    System.out.println("[SERVER_APP] Impossibile inviare QuitLobbyMessage al client lobby disconnesso.");
                }
            }

            synchronized (lobbyClients) {
                for (String sessionId : sessionIds) {
                    lobbyClients.remove(sessionId);
                }
            }

            for (VirtualView client : disconnectedClients) {
                try {
                    client.close();
                } catch (Exception ignored) {
                }
            }

            return;
        }

        List<String> disconnectedPlayersId = new ArrayList<>();

        synchronized (gameClients) {
            for (Map.Entry<String, VirtualView> entry : gameClients.entrySet()) {
                VirtualView gameClient = entry.getValue();

                if (disconnectedClients.contains(gameClient)) {
                    disconnectedPlayersId.add(entry.getKey());
                }
            }
        }

        if (!disconnectedPlayersId.isEmpty()) {
            System.out.println("[SERVER_APP] Client disconnessi in game: " + disconnectedPlayersId);

            synchronized (gameClients) {
                for (String playerId : disconnectedPlayersId) {
                    gameClients.remove(playerId);
                }
            }

            for (VirtualView client : disconnectedClients) {
                try {
                    client.close();
                } catch (Exception ignored) {
                }
            }

            MessageToClient message;

            synchronized (gameLock) {
                message = controller.handleGameCrashed();
            }

            if (message != null) {
                message.deliver(null, this);
            }

            stopAliveChecker();

        }
    }

    private void broadcastLobbyStatus() {
        MessageToClient lobbyMessage =
                new LobbyStatusMessage(controller.getLobbyView(), "Lobby aggiornata.");

        broadcastToLobby(lobbyMessage);
    }

    @Override
    public void sendToSession(String sessionId, MessageToClient message) {
        VirtualView client;

        synchronized (lobbyClients) {
            client = lobbyClients.get(sessionId);
        }

        if (client == null) {
            System.out.println("[SERVER_APP] ERRORE: sessione lobby non trovata: " + sessionId);
            return;
        }

        try {
            client.onMessage(message);
        } catch (Exception e) {
            System.out.println("[SERVER_APP] Client lobby non raggiungibile: " + sessionId
                    + ", errore: " + e.getMessage());

            synchronized (lobbyClients) {
                if (lobbyClients.get(sessionId) == client) {
                    lobbyClients.remove(sessionId);
                }
            }

            synchronized (lastPingByClient) {
                lastPingByClient.remove(client);
            }

            try {
                client.close();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void broadcastToLobby(MessageToClient message) {
        Map<String, VirtualView> copy;

        synchronized (lobbyClients) {
            copy = new HashMap<>(lobbyClients);
        }

        for (Map.Entry<String, VirtualView> entry : copy.entrySet()) {
            String sessionId = entry.getKey();
            VirtualView client = entry.getValue();

            try {
                client.onMessage(message);
            } catch (Exception e) {
                System.out.println("[SERVER_APP] Client lobby non raggiungibile durante broadcast: "
                        + sessionId
                        + ", errore: "
                        + e.getMessage());

                synchronized (lobbyClients) {
                    if (lobbyClients.get(sessionId) == client) {
                        lobbyClients.remove(sessionId);
                    }
                }

                synchronized (lastPingByClient) {
                    lastPingByClient.remove(client);
                }

                try {
                    client.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void sendTo(String playerId, MessageToClient message) {
        VirtualView client;

        synchronized (gameClients) {
            client = gameClients.get(playerId);
        }

        System.out.println("[SERVER_APP] sendTo "
                + message.getClass().getSimpleName()
                + " verso: "
                + playerId);

        if (client == null) {
            System.out.println("[SERVER_APP] ERRORE: client non trovato: " + playerId);
            return;
        }

        try {
            client.onMessage(message);

            System.out.println("[SERVER_APP] sendTo completato verso: " + playerId);

        } catch (Exception e) {
            System.out.println("[SERVER_APP] Client " + playerId
                    + " non raggiungibile in sendTo: "
                    + e.getMessage());

            synchronized (gameClients) {
                if (gameClients.get(playerId) == client) {
                    gameClients.remove(playerId);
                }
            }

            synchronized (lastPingByClient) {
                lastPingByClient.remove(client);
            }

            try {
                client.close();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void broadcast(MessageToClient message) {
        Map<String, VirtualView> copy;

        synchronized (gameClients) {
            copy = new HashMap<>(gameClients);
        }

        System.out.println("[SERVER_APP] broadcast "
                + message.getClass().getSimpleName()
                + " verso client: "
                + copy.keySet());

        for (Map.Entry<String, VirtualView> entry : copy.entrySet()) {
            String playerId = entry.getKey();
            VirtualView client = entry.getValue();

            try {
                System.out.println("[SERVER_APP] Invio broadcast a: " + playerId);

                client.onMessage(message);

                System.out.println("[SERVER_APP] Broadcast completato verso: " + playerId);

            } catch (Exception e) {
                System.out.println("[SERVER_APP] Client " + playerId
                        + " non raggiungibile durante broadcast: "
                        + e.getMessage());

                synchronized (gameClients) {
                    if (gameClients.get(playerId) == client) {
                        gameClients.remove(playerId);
                    }
                }

                synchronized (lastPingByClient) {
                    lastPingByClient.remove(client);
                }

                try {
                    client.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void shutdown() {
        try {
            rmiExecutor.shutdownNow();
            pingExecutor.shutdownNow();
        } catch (Exception ignored) {
        }

        stopAliveChecker();
    }
}