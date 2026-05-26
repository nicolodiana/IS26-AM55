package it.polimi.ingsw.am55.network;

import it.polimi.ingsw.am55.controller.GameController;
import it.polimi.ingsw.am55.message.*;
import it.polimi.ingsw.am55.network.command.ServerCommand;
import it.polimi.ingsw.am55.virtualview.VirtualServer;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.util.*;

public class ServerApplication implements VirtualServer, MessageDelivery {

    private final GameController controller;
    //mappa per gestire i client uniti in partita a seguito di una create o join game
    private final Map<String, VirtualView> gameClients;
    //mappa per gestire i client connessi con il server e ai quali viene mostrata lobby (pre create o join)
    private final Map<String, VirtualView> lobbyClients;
    private final Map<VirtualView,Long> lastPingByClient;
    private Timer pingTimer;
    private static final long PING_TIMEOUT_MS = 6_000;

    private boolean aliveCheckerStarted=false;
    //private final ScheduledExecutorService aliveChecker = Executors.newSingleThreadScheduledExecutor();
    /*
     * Lock dedicato alla logica di gioco.
     * Così evitiamo che due thread RMI/socket entrino insieme nel GameController.
     */
    private final Object gameLock = new Object();

    public ServerApplication() {
        this.controller = new GameController();
        this.lobbyClients = new HashMap<>();
        this.gameClients = new HashMap<>();
        this.lastPingByClient = new HashMap<>();
        this.pingTimer = new Timer(true);
        System.out.println("[SERVER_APP] ServerApplication creata.");
    }
    public void registerLobbyClient(String sessionId, VirtualView client) throws Exception {
        synchronized (lobbyClients) {
            lobbyClients.put(sessionId, client);
        }
        //Appena il client si collega devo salvarmi l' istante in cui si è connesso
        synchronized (lastPingByClient) {
            lastPingByClient.put(client, System.currentTimeMillis());
        }
        //Una volta che il client si è collegato gli notifico di iniziare a mandare il ping verso il server
        sendToSession(sessionId,new StartPingMessage());
        startAliveChecker();

        //client.onMessage(new LobbyStatusMessage(controller.getLobbyView()));
    }
//    public void registerClient(String playerId,VirtualView client) { ??FORSE IL PING VA SPOSTATO IN REGISTER LOBBY??
//        synchronized (gameClients) {
//            gameClients.put(playerId, client);
//            System.out.println("[SERVER_APP] Registrato client: " + playerId);
//            System.out.println("[SERVER_APP] Client registrati: " + gameClients.keySet());
//        }
//        //Perché devo registrare l'istante in cui il client si collega al server per la prima volta, per ragioni di ping
//        synchronized (lastPingByClient) {
//          lastPingByClient.put(client, System.currentTimeMillis());
//        }
//        startAliveChecker();
//    }

    public void executeCommand(ServerCommand command, VirtualView sender) throws Exception {
        System.out.println("[SERVER_APP] Esecuzione command: "
                + command.getClass().getSimpleName()
                + ", sender = "
                + (sender == null ? "null" : sender.getClass().getSimpleName()));

        /*
         * Tutti i command passano da qui.
         * Il lock garantisce che il GameController venga modificato da un solo thread alla volta.
         */
        if(command.requiresLock()){
            synchronized (gameLock) {
                command.execute(this, sender);
            }
        }else{
            command.execute(this, sender);
        }

        System.out.println("[SERVER_APP] Command completato: "
                + command.getClass().getSimpleName());
    }
    private void pong(VirtualView sender) throws Exception {
        synchronized (gameClients) {
            if (sender.getPlayerId() != null && gameClients.containsKey(sender.getPlayerId())) {
                sendTo(sender.getPlayerId(), new PongMessage());
                System.out.println("[SERVER_APP] PongMessage game client "+ sender.getPlayerId());
                return;
            }
        }
        synchronized (lobbyClients) {
            for (Map.Entry<String, VirtualView> entry : lobbyClients.entrySet()) {
                if (entry.getValue().equals(sender)) {
                    sendToSession(entry.getKey(), new PongMessage());
                    System.out.println("[SERVER_APP] PongMessage lobby client "+sender.getPlayerId());
                    return;
                }
            }
        }
    }
    @Override
    public void createGame(String playerId, String totemColor, int numPlayers, String sessionId) throws Exception {
        MessageToClient message =
                controller.createGame(playerId, totemColor, numPlayers);

        completeConnectionSetup(playerId, sessionId, message);
    }

    @Override
    public void joinGame(String playerId, String totemColor, String sessionId) throws Exception {
        MessageToClient message =
                controller.joinGame(playerId, totemColor);

        completeConnectionSetup(playerId, sessionId, message);
    }
    private void completeConnectionSetup(String playerId, String sessionId, MessageToClient message) throws Exception {
        VirtualView client;

        synchronized (lobbyClients) {
            client = lobbyClients.get(sessionId);
        }

        if (client == null) {
            System.out.println("[SERVER_APP] Sessione lobby non trovata: " + sessionId);
            return;
        }

        // QUI gestisci nickname/totem duplicato , connectionsetupsuccesful è false nel caso in cui si sia generata un eccezione lato model e catturata dal game controller
        if (!message.isConnectionSetupSuccessful()) {
            sendToSession(sessionId, message); // mando errore al client tramite sessionId
            return;                    // NON registro in game
        }
        //altrimenti lo tolgo dai lobby e gli mando il waiting message

        synchronized (lobbyClients) {
            lobbyClients.remove(sessionId);
        }

        client.setPlayerId(playerId);
//in alternativa usare direttamente Register Client se si vogliono inserire anche metodi di ping
        synchronized (gameClients) {
            gameClients.put(playerId, client);
        }

        message.deliver(playerId, this);
        //però comunque notifico tutti gli altri in lobby delle scelte rimanenti disponibili (creategame o totem rimasti )
        /*
         * La lobby è passiva: non modifica il model e non esegue azioni di gioco.
         * Riceve solo uno snapshot leggero dello stato attuale tramite LobbyView.
         *
         * Per questo motivo la LobbyStatusMessage viene creata qui nella
         * ServerApplication: è un messaggio di routing/networking destinato
         * ai client connessi ma non ancora entrati in partita.
         */
        broadcastLobbyStatus();
    }


    @Override
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

    @Override
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

    @Override
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

    @Override
    public void quitGame(String playerId) throws Exception {
        System.out.println("[SERVER_APP] quitGame chiamato da: " + playerId);
        //Dichiaro la partita in stato di ENDED
        MessageToClient message = controller.quitGame(playerId);

        //Tutti i players sono notificati del cambiamento di stato del model
        message.deliver(playerId, this);
        System.out.println("[SERVER_APP] Broadcast di quit completato.");

        //Smetto di tenere attivo l' aliverchecker
        pingTimer.cancel();
        aliveCheckerStarted=false;

        //Tutti i player devono uscire dalla partita
        synchronized (gameClients){
            for (Map.Entry<String, VirtualView> entry : gameClients.entrySet()) {
                VirtualView gameClient = entry.getValue();
                gameClient.close();
            }
            gameClients.clear();
            System.out.println("[SERVER_APP] Game client map svuotata "+ gameClients);
        }
        synchronized (lastPingByClient){
            lastPingByClient.clear();
            System.out.println("[SERVER_APP] Ping  map svuotata "+ lastPingByClient);
        }
    }


//    @Override
//    public void closeConnection(String playerId) throws Exception {
//        System.out.println("[SERVER_APP] Sto scollegando "+ playerId);
//        VirtualView sender;
//        synchronized (gameClients) {
//            sender = gameClients.get(playerId);
//            gameClients.remove(playerId);
//        }
//        synchronized (lastPingByClient) {
//            lastPingByClient.remove(sender);
//        }
//        if (sender != null) {
//            sender.close();
//        }
//    }

    @Override
    public void quitLobby(String sessionId) throws Exception {
        VirtualView sender;
        //Non bisogna fare operazioni sul model, perché la partita non è stata ancora avviata
        MessageToClient message = new QuitLobbyMessage();
        message.deliver(sessionId, this);

        //Elimino solo il client che ha richiesto di disconnettersi e chiudo direttamente la
        synchronized (lobbyClients){
            sender = lobbyClients.get(sessionId);
            lobbyClients.remove(sessionId);
            if(sender != null){
                sender.close();
            }
        }
        synchronized (lastPingByClient){
            lastPingByClient.remove(sender);
        }
    }

    //Il metodo ha la responsabilità di salvare l' u
    @Override
    public void ping(VirtualView sender) throws Exception {
        if (sender == null) {
            return;
        }
        //Aggiorno l' ultimo ping del client
        synchronized (lastPingByClient) {
            lastPingByClient.put(sender, System.currentTimeMillis());
        }
        pong(sender);
    }
    private synchronized void startAliveChecker() {
        if (aliveCheckerStarted) { //Deve avviarsi una sola volta il checker
            return;
        }
        aliveCheckerStarted = true;

        pingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkPingTimeouts();
            }
        }, 0, 10000);

        System.out.println("[PING] Alive checker AVVIATO");
    }

    private void checkPingTimeouts() {
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

//        for (VirtualView disconnectedClient : disconnectedClients) {
//            handleClientDisconnection(disconnectedClient);
//        }
        handleClientDisconnection(disconnectedClients);
    }
    private void handleClientDisconnection(List<VirtualView> disconnectedClients) {
        if (disconnectedClients == null) {
            return;
        }

        synchronized (lastPingByClient) {
            for(VirtualView client : disconnectedClients){
                lastPingByClient.remove(client);
            }
        }


        List<String> sessionIds = new ArrayList<>();

        synchronized (lobbyClients) {
            for (Map.Entry<String, VirtualView> entry : lobbyClients.entrySet()) {
                VirtualView lobbyClient = entry.getValue();

                if (disconnectedClients.contains(lobbyClient)) {
                    sessionIds.add( entry.getKey());
                }
            }
        }

        if (!sessionIds.isEmpty()) {
            System.out.println("[SERVER_APP] Client disconnesso in lobby: ");

            try {
                for (String sessionId : sessionIds) {
                    sendToSession(sessionId,new QuitLobbyMessage());
                }
            } catch (Exception ignored) {
                System.out.println("[SERVER_APP] Impossibile inviare QuitLobbyMessage al client lobby disconnesso.");
            }
            synchronized (lobbyClients){
                for(String sessionId : sessionIds){
                    lobbyClients.remove(sessionId);
                }
            }
            try {
                for(VirtualView client : disconnectedClients){
                    client.close();
                }
            } catch (Exception ignored) {
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

//        if (disconnectedPlayerId == null) {
//            System.out.println("[SERVER_APP] Client disconnesso ma non presente né in lobby né in game.");
//
//            try {
//                disconnectedClient.close();
//            } catch (Exception ignored) {
//            }
//
//            return;
//        }
        if(!disconnectedPlayersId.isEmpty()){
            System.out.println("[SERVER_APP] Client disconnesso in game: " + disconnectedPlayersId);

            MessageToClient message;

            synchronized (gameLock) {
                message = controller.handleGameCrashed();
            }

            if (message != null) {
                message.deliver(null, this);
            }

            pingTimer.cancel();
            aliveCheckerStarted = false;

            synchronized (gameClients) {
                for(String playerId :  disconnectedPlayersId){
                    gameClients.remove(playerId);
                }
            }

            try {
                for(VirtualView client : disconnectedClients){
                    client.close();
                }
            } catch (Exception ignored) {
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
            synchronized (lobbyClients) {
                if (lobbyClients.get(sessionId) == client) {
                    lobbyClients.remove(sessionId);
                }
            }

            try {
                client.close();
            } catch (Exception ignored) {
            }
        }
    }
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
                synchronized (lobbyClients) {
                    if (lobbyClients.get(sessionId) == client) {
                        lobbyClients.remove(sessionId);
                    }
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
//        synchronized (controller){
//            copy.entrySet().removeIf(entry -> !controller.isInGame(entry.getKey()));
//        }

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
                //Il client non è raggiungibile perché già stato chiuso
                //Il metodo solleva l' eccezione ma è ignorata perché già chiuso
                try {
                    client.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}