package it.polimi.ingsw.am55.network;

import it.polimi.ingsw.am55.controller.GameController;
import it.polimi.ingsw.am55.message.GameCrashedBroadcast;
import it.polimi.ingsw.am55.message.LobbyStatusMessage;
import it.polimi.ingsw.am55.message.MessageDelivery;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.message.PongMessage;
import it.polimi.ingsw.am55.message.QuitLobbyMessage;
import it.polimi.ingsw.am55.message.StartPingMessage;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerApplication extends UnicastRemoteObject implements VirtualServer, MessageDelivery {

    private static final long serialVersionUID = 1L;

    private static final long PING_TIMEOUT_MS = 6_000;

    private final GameController controller;

    private final Map<String, VirtualView> gameClients;
    private final Map<String, VirtualView> lobbyClients;
    private final Map<VirtualView, Long> lastPingByClient;
    private AtomicBoolean isGameCrashed;
    private final Object gameLock = new Object();

    /*
     * Executor usato SOLO per RMI.
     *
     * RMI entra da receiveCommand(...), che sottomette il command a questo executor.
     * Socket invece NON entra da receiveCommand(...): il ClientSkeleton legge dallo stream
     * e chiama direttamente executeCommand(...).
     */
    private final ExecutorService rmiExecutor;
    private final ExecutorService disconnectionExecutor;

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
        this.isGameCrashed = new AtomicBoolean(false);
        this.rmiExecutor = Executors.newCachedThreadPool();
        this.disconnectionExecutor = Executors.newSingleThreadExecutor();
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
        rmiExecutor.submit(() -> {
            try {
                executeCommand(command, sender);
            } catch (Exception e) {
                System.out.println("[SERVER_APP] Errore executeCommand da RMI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public void close() throws RemoteException {

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
//        if (sender != null) {
//            synchronized (lastPingByClient) {
//                lastPingByClient.put(sender, System.currentTimeMillis());
//            }
//        }
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

        sendToSession(
                sessionId,
                new LobbyStatusMessage(
                        controller.getLobbyView(),
                        "Stato lobby sincronizzato."
                )
        );

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

        synchronized (gameLock) {
            client = lobbyClients.get(sessionId);

            if (client == null) {
                System.out.println("[SERVER_APP] Sessione lobby non trovata: " + sessionId);
                return;
            }

            if (!message.isConnectionSetupSuccessful()) {
                sendToSession(sessionId, message);
                return;
            }

            lobbyClients.remove(sessionId);
            client.setPlayerId(playerId);
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

        MessageToClient message = new QuitLobbyMessage("Uscito correttamente dal gioco");
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

    public void ping(VirtualView sender) throws Exception {
        if (sender == null) {
            return;
        }

        synchronized (lastPingByClient) {
            lastPingByClient.put(sender, System.currentTimeMillis());
        }

        pong(sender);
    }

    private void pong(VirtualView sender) throws Exception {
        synchronized (gameClients) {
            String playerId = sender.getPlayerId();

            if (playerId != null && gameClients.containsKey(playerId)) {
                sendTo(playerId, new PongMessage());
                System.out.println("[SERVER_APP] PongMessage game client " + playerId);
                return;
            }
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

    private void startAliveChecker() {
        if (aliveCheckerStarted) {
            return;
        }

        aliveCheckerStarted = true;

        pingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    List<VirtualView> disconnectedClients = checkPingTimeouts();

                    if (!disconnectedClients.isEmpty()) {
                        System.out.println("[SERVER_APP] Client in timeout: "
                                + disconnectedClients.size());

                        List<VirtualView> snapshot = List.copyOf(disconnectedClients);
                        disconnectionExecutor.submit(() -> handleClientDisconnection(snapshot));
                    }

                } catch (Exception e) {
                    System.out.println("[PING] Error alive checker: " + e.getMessage());
                }
            }
        }, 0, 1500);

        System.out.println("[PING] Alive checker AVVIATO");
    }

    private void stopAliveChecker() {
        try {
            pingTimer.cancel();
        } catch (Exception ignored) {}

        pingTimer = new Timer(true); // FIX: ricrea il timer per uso futuro
        aliveCheckerStarted = false;

        System.out.println("[PING] Alive checker FERMATO");
    }

    private List<VirtualView> checkPingTimeouts() {
        List<VirtualView> disconnectedClients = new ArrayList<>();
        long now = System.currentTimeMillis();

        synchronized (lastPingByClient) {
            var iterator = lastPingByClient.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<VirtualView, Long> entry = iterator.next();

                VirtualView client = entry.getKey();
                long lastPing = entry.getValue();
                long elapsed = now - lastPing;

                if (elapsed > PING_TIMEOUT_MS) {
                    disconnectedClients.add(client);

                    // FONDAMENTALE:
                    // lo togliamo SUBITO, così il timer non lo ritrova
                    // altre 200 volte prima che l'executor lo processi.
                    iterator.remove();
                }
            }
        }
        return disconnectedClients;
    }


    private void handleClientDisconnection(List<VirtualView> disconnectedClients) {
        if (disconnectedClients == null || disconnectedClients.isEmpty()) {
            return;
        }

//        synchronized (lastPingByClient) {
//            for (VirtualView client : disconnectedClients) {
//                lastPingByClient.remove(client);
//            }
//        }

        List<String> disconnectedPlayersId = new ArrayList<>();
        List<String> sessionDisconnectedIds = new ArrayList<>();

        synchronized (gameClients) {
            for (Map.Entry<String, VirtualView> entry : gameClients.entrySet()) {
                if (disconnectedClients.contains(entry.getValue())) {
                    disconnectedPlayersId.add(entry.getKey());
                }
            }
        }

        synchronized (lobbyClients) {
            for (Map.Entry<String, VirtualView> entry : lobbyClients.entrySet()) {
                if (disconnectedClients.contains(entry.getValue())) {
                    sessionDisconnectedIds.add(entry.getKey());
                }
            }
        }

        if (!disconnectedPlayersId.isEmpty()) {
            System.out.println("[SERVER_APP] Client disconnessi in game: " + disconnectedPlayersId);

            synchronized (gameClients) {
                for (String id : disconnectedPlayersId) {
                    gameClients.remove(id);
                }
            }

            synchronized (lobbyClients) {
                for (String sid : sessionDisconnectedIds) {
                    lobbyClients.remove(sid);
                }
            }

            if (!this.isGameCrashed.get()) {
                MessageToClient message;
                synchronized (gameLock) {
                    message = controller.handleGameCrashed();
                }

                if (message != null) {
                    this.isGameCrashed.set(true);
                    message.deliver(null, this);
                    broadcastToLobby(new QuitLobbyMessage("Un player si è disconnesso da game"));
                }
            }

            synchronized (gameClients) {
                for (VirtualView v : gameClients.values()) {
                    try {
                        v.close();
                    } catch (Exception ignored) {}
                }
                gameClients.clear();
            }

            synchronized (lobbyClients) {
                for (VirtualView v : lobbyClients.values()) {
                    try {
                        v.close();
                    } catch (Exception ignored) {}
                }
                lobbyClients.clear();
            }

            for (VirtualView crashed : disconnectedClients) {
                try {
                    crashed.close();
                } catch (Exception ignored) {}
            }

            synchronized (lastPingByClient) {
                lastPingByClient.clear();
            }

            stopAliveChecker();
            return;
        }

        if (!sessionDisconnectedIds.isEmpty()) {
            System.out.println("[SERVER_APP] Client disconnessi in lobby: " + sessionDisconnectedIds);

            synchronized (lobbyClients) {
                for (String sid : sessionDisconnectedIds) {
                    lobbyClients.remove(sid);
                }
            }
            synchronized (lastPingByClient) {
                System.out.println("[SERVER_APP] Client disconnessi in last ping by clients: " + lastPingByClient);
                if(lastPingByClient.isEmpty()) {
                    stopAliveChecker();
                }
            }
            for (VirtualView client : disconnectedClients) {
                try {
                    client.close();
                } catch (Exception ignored) {}
            }
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

//            synchronized (lobbyClients) {
//                if (lobbyClients.get(sessionId) == client) {
//                    lobbyClients.remove(sessionId);
//                }
//            }
//
//            synchronized (lastPingByClient) {
//                lastPingByClient.remove(client);
//            }
//
//            try {
//                client.close();
//            } catch (Exception ignored) {
//            }
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

//                synchronized (lobbyClients) {
//                    if (lobbyClients.get(sessionId) == client) {
//                        lobbyClients.remove(sessionId);
//                    }
//                }
//
//                synchronized (lastPingByClient) {
//                    lastPingByClient.remove(client);
//                }
//
//                try {
//                    client.close();
//                } catch (Exception ignored) {
//                }
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

//            synchronized (gameClients) {
//                if (gameClients.get(playerId) == client) {
//                    gameClients.remove(playerId);
//                }
//            }
//
//            synchronized (lastPingByClient) {
//                lastPingByClient.remove(client);
//            }
//
//            try {
//                client.close();
//            } catch (Exception ignored) {
//            }
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
                        + " non raggiungibile durante broadcast ");

//                synchronized (gameClients) {
//                    if (gameClients.get(playerId) == client) {
//                        gameClients.remove(playerId);
//                    }
//                }
//
//                synchronized (lastPingByClient) {
//                    lastPingByClient.remove(client);
//                }
//
//                try {
//                    client.close();
//                } catch (Exception ignored) {
//                }
            }
        }
    }

}