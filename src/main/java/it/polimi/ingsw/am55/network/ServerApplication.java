package it.polimi.ingsw.am55.network;

import it.polimi.ingsw.am55.controller.GameController;
import it.polimi.ingsw.am55.message.MessageDelivery;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.command.ServerCommand;
import it.polimi.ingsw.am55.virtualview.VirtualServer;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.util.HashMap;
import java.util.Map;

public class ServerApplication implements VirtualServer, MessageDelivery {

    private final GameController controller;
    private final Map<String, VirtualView> clients;

    /*
     * Lock dedicato alla logica di gioco.
     * Così evitiamo che due thread RMI/socket entrino insieme nel GameController.
     */
    private final Object gameLock = new Object();

    public ServerApplication() {
        this.controller = new GameController();
        this.clients = new HashMap<>();

        System.out.println("[SERVER_APP] ServerApplication creata.");
    }

    public void registerClient(String playerId, VirtualView client) {
        synchronized (clients) {
            clients.put(playerId, client);

            System.out.println("[SERVER_APP] Registrato client: " + playerId);
            System.out.println("[SERVER_APP] Client registrati: " + clients.keySet());
        }
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
        synchronized (gameLock) {
            command.execute(this, sender);
        }

        System.out.println("[SERVER_APP] Command completato: "
                + command.getClass().getSimpleName());
    }

    @Override
    public void createGame(String playerId, String totemColor, int numPlayers) throws Exception {
        System.out.println("[SERVER_APP] createGame chiamato da: "
                + playerId
                + ", colore = "
                + totemColor
                + ", numPlayers = "
                + numPlayers);

        MessageToClient message = controller.createGame(playerId, totemColor, numPlayers);

        System.out.println("[SERVER_APP] createGame ha prodotto messaggio: "
                + message.getClass().getSimpleName());

        message.deliver(playerId, this);
    }

    @Override
    public void joinGame(String playerId, String totemColor) throws Exception {
        System.out.println("[SERVER_APP] joinGame chiamato da: "
                + playerId
                + ", colore = "
                + totemColor);

        MessageToClient message = controller.joinGame(playerId, totemColor);

        System.out.println("[SERVER_APP] joinGame ha prodotto messaggio: "
                + message.getClass().getSimpleName());

        message.deliver(playerId, this);
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
            System.out.println("[SERVER_APP] ERRORE sendTo verso "
                    + playerId
                    + ": "
                    + e.getMessage());

            e.printStackTrace();
        }
    }

    @Override
    public void broadcast(MessageToClient message) {
        Map<String, VirtualView> copy;

        synchronized (clients) {
            copy = new HashMap<>(clients);
        }

        System.out.println("[SERVER_APP] broadcast "
                + message.getClass().getSimpleName()
                + " verso client: "
                + copy.keySet());

        if (copy.isEmpty()) {
            System.out.println("[SERVER_APP] ATTENZIONE: broadcast senza client registrati.");
            return;
        }

        for (Map.Entry<String, VirtualView> entry : copy.entrySet()) {
            String playerId = entry.getKey();
            VirtualView client = entry.getValue();

            try {
                System.out.println("[SERVER_APP] Invio broadcast a: " + playerId);

                client.onMessage(message);

                System.out.println("[SERVER_APP] Broadcast completato verso: " + playerId);

            } catch (Exception e) {
                System.out.println("[SERVER_APP] ERRORE broadcast verso "
                        + playerId
                        + ": "
                        + e.getMessage());

                e.printStackTrace();
            }
        }
    }
}