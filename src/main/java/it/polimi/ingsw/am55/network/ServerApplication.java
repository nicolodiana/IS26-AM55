package it.polimi.ingsw.am55.network;

import it.polimi.ingsw.am55.Client;
import it.polimi.ingsw.am55.controller.GameController;
import it.polimi.ingsw.am55.message.*;
import it.polimi.ingsw.am55.network.command.PingCommand;
import it.polimi.ingsw.am55.network.command.ServerCommand;
import it.polimi.ingsw.am55.virtualview.VirtualServer;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerApplication implements VirtualServer, MessageDelivery {

    private final GameController controller;

    private final Map<String, VirtualView> clients;
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
        this.clients = new HashMap<>();
        this.lastPingByClient = new HashMap<>();
        this.pingTimer = new Timer(true);
        System.out.println("[SERVER_APP] ServerApplication creata.");
    }

    public void registerClient(String playerId,VirtualView client) {
        synchronized (clients) {
            clients.put(playerId, client);
            System.out.println("[SERVER_APP] Registrato client: " + playerId);
            System.out.println("[SERVER_APP] Client registrati: " + clients.keySet());
        }
        //Perché devo registrare l'istante in cui il client si collega al server per la prima volta, per ragioni di ping
        synchronized (lastPingByClient) {
            lastPingByClient.put(client, System.currentTimeMillis());
        }
        startAliveChecker();
    }

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
        }else{//Esecuzione del comando di ping
            command.execute(this, sender);
        }

        System.out.println("[SERVER_APP] Command completato: "
                + command.getClass().getSimpleName());
    }

    @Override
    public void createGame(String playerId, String totemColor, int numPlayers, VirtualView sender) throws Exception {
        System.out.println("[SERVER_APP] createGame chiamato da: "
                + playerId
                + ", colore = "
                + totemColor
                + ", numPlayers = "
                + numPlayers);

        MessageToClient message = controller.createGame(playerId, totemColor, numPlayers);

        System.out.println("[SERVER_APP] createGame ha prodotto messaggio: "
                + message.getClass().getSimpleName());

        completeConnectionSetup(playerId, sender, message);
    }

    @Override
    public void joinGame(String playerId, String totemColor, VirtualView sender) throws Exception {
        System.out.println("[SERVER_APP] joinGame chiamato da: "
                + playerId
                + ", colore = "
                + totemColor);

        MessageToClient message = controller.joinGame(playerId, totemColor);

        System.out.println("[SERVER_APP] joinGame ha prodotto messaggio: "
                + message.getClass().getSimpleName());


        completeConnectionSetup(playerId, sender, message);
    }

    private void completeConnectionSetup(String playerId, VirtualView sender, MessageToClient message) throws Exception {

        // Se create/join fallisce, NON registro il sender nella mappa clients.
        // Inoltre invio l'errore direttamente al richiedente, altrimenti con un nickname duplicato
        // l'ErrorMessage finirebbe al vecchio client già registrato con quel nickname.
        if (!message.isConnectionSetupSuccessful()) {
            sender.onMessage(message); //Perché se non lo registra nella mappa bisogna chiamarlo singolarmente
        }else{
            sender.setPlayerId(playerId);
            registerClient(playerId, sender);

            //Se la join oppure la create sono andare a buon fine allora invio il messaggio al singolo client
            //Inoltre a seguito di questa istruzione il server avvisa il client che può iniziare il suo ping
            message.deliver(playerId,this);
        }
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
        if (!controller.isInGame(playerId)){
            VirtualView client;
            synchronized (clients){
                client = clients.get(playerId);
                clients.get(playerId).close();
                clients.remove(playerId);
            }
            synchronized (lastPingByClient){
                lastPingByClient.remove(client);
            }
            return;
        }

        MessageToClient message = controller.quitGame(playerId);

        message.deliver(playerId, this);

        VirtualView client;
        synchronized (clients){
            client = clients.get(playerId);
            clients.get(playerId).close();
            clients.remove(playerId);
        }
        synchronized (lastPingByClient){
            lastPingByClient.remove(client);
        }
        pingTimer.cancel();
        aliveCheckerStarted=false;

        System.out.println("[SERVER_APP] Broadcast di quit completato.");
    }


    @Override
    public void closeConnection(String playerId) throws Exception {
        System.out.println("[SERVER_APP] Sto scollegando "+ playerId);
        VirtualView sender;
        synchronized (clients) {
            sender = clients.get(playerId);
            clients.remove(playerId);
        }
        synchronized (lastPingByClient) {
            lastPingByClient.remove(sender);
        }
        if (sender != null) {
            sender.close();
        }
    }

    //Il metodo ha la responsabilità di salvare l' u
    @Override
    public void ping(VirtualView client) throws Exception {
        if (client == null) {
            return;
        }
        synchronized (lastPingByClient) {
            lastPingByClient.put(client, System.currentTimeMillis());
        }
        client.pong();
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
        }, 0, 1000);

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

        for (VirtualView disconnectedClient : disconnectedClients) {
            handleClientDisconnection(disconnectedClient);
        }
    }
    private  void handleClientDisconnection(VirtualView disconnectedClient) {
        String disconnectedPlayer = null;
        synchronized (clients){
            for (Map.Entry<String, VirtualView> entry : clients.entrySet()) {
                if (entry.getValue().equals(disconnectedClient)) {
                    disconnectedPlayer = entry.getKey();
                    break;
                }
            }
        }
        if(disconnectedPlayer == null){
            return;
        }
        System.out.println("[SERVER_APP] Un client si è disconnesso: "+ disconnectedPlayer);
        synchronized (lastPingByClient) {
            lastPingByClient.remove(disconnectedClient);
        }
        synchronized (clients) {
            clients.remove(disconnectedPlayer);
        }
//        stopAliveCheckerIfNoClients();
        //notifyAllClients("Il player si è disconnesso: "+disconnectedPlayer+ " la partita termina.");
        //Quando il client si disconnette: si dichiara il game in stato crashed
        MessageToClient message= null;
        synchronized (gameLock) {
            message = controller.handleGameCrashed();
        }
        if(message!=null){
            message.deliver(disconnectedPlayer, this);
            pingTimer.cancel();
            aliveCheckerStarted=false;
        }
    }

    @Override
    public void sendTo(String playerId, MessageToClient message) {
        VirtualView client;

        synchronized (clients) {
            client = clients.get(playerId);
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

            synchronized (clients) {
                if (clients.get(playerId) == client) {
                    clients.remove(playerId);
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

        synchronized (clients) {
            copy = new HashMap<>(clients);
        }
        synchronized (controller){
            copy.entrySet().removeIf(entry -> !controller.isInGame(entry.getKey()));
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

                synchronized (clients) {
                    if (clients.get(playerId) == client) {
                        clients.remove(playerId);
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