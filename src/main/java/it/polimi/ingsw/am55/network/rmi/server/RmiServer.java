package it.polimi.ingsw.am55.network.rmi.server;

import it.polimi.ingsw.am55.controller.*;
import it.polimi.ingsw.am55.message.MessageDelivery;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.rmi.client.VirtualViewRmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Server RMI.
 *
 * Si occupa di:
 * - esportare l'oggetto remoto server-side
 * - registrare il server nel registry
 * - ricevere le richieste remote dei client
 * - delegare la logica applicativa al GameController
 * - consegnare i MessageToClient ai client registrati
 *
 * Implementa anche MessageDelivery, così i messaggi concreti possono
 * decidere da soli se farsi inviare in broadcast o in unicast.
 */
public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi, MessageDelivery {

    private final GameController controller;
    private final Map<String, VirtualViewRmi> clients;

    public RmiServer() throws RemoteException {
        super(); // esporta l'oggetto remoto server-side
        this.controller = new GameController();
        this.clients = new HashMap<>();
    }

    public static void main(String[] args) throws Exception {
        final String serverName = "GameServer";
        final int port = 1234;

        RmiServer server = new RmiServer();

        // creo il registry sulla porta scelta
        Registry registry = LocateRegistry.createRegistry(port);

        // registro l'oggetto remoto del server
        registry.rebind(serverName, server);

        System.out.println("RmiServer avviato correttamente.");
    }

    /**
     * Metodo usato da un client per registrare la propria callback remota.
     *
     * In questo modo il server può poi notificargli aggiornamenti tramite onMessage(...).
     */
    @Override
    public void connect(String playerId, VirtualViewRmi client) throws RemoteException {
        synchronized (clients) {
            clients.put(playerId, client);
        }
    }

    /**
     * Richiesta remota createGame.
     *
     * Il server delega la logica al controller, ottiene un MessageToClient
     * e poi lascia al messaggio concreto la decisione su come consegnarsi.
     */
    @Override
    public void createGame(String playerId, String totem, int numPlayers) throws RemoteException {
        MessageToClient message = controller.createGame(playerId, totem, numPlayers);
        message.deliver(playerId, this);
    }

    /**
     * Richiesta remota joinGame.
     */
    @Override
    public void joinGame(String playerId, String totem) throws RemoteException {

        MessageToClient message = controller.joinGame(playerId, totem);
        message.deliver(playerId, this);
    }

    /**
     * Richiesta remota placeTotem.
     */
    @Override
    public void placeTotem(String playerId, int index) throws RemoteException {
       /*
        MessageToClient message = controller.placeTotem(playerId, index);
        message.deliver(playerId, this);
        */

    }

    public void pickCard(String playerId, int cardId) throws RemoteException {
        MessageToClient message = controller.pickCard(playerId, cardId);
        message.deliver(playerId,this);
    }
    /**
     * Invio in broadcast a tutti i client registrati.
     *
     * Questo metodo viene richiamato dai messaggi concreti che decidono
     * di essere consegnati a tutti.
     */
    @Override
    public void broadcast(MessageToClient message) {
        synchronized (clients) {
            for (VirtualViewRmi client : clients.values()) {
                try {
                    client.onMessage(message);
                } catch (RemoteException e) {
                    System.out.println("Client disconnesso durante broadcast.");
                }
            }
        }
    }

    /**
     * Invio mirato a un solo client.
     *
     * Questo metodo viene richiamato dai messaggi concreti che decidono
     * di essere consegnati solo al chiamante.
     */
    @Override
    public void sendTo(String playerId, MessageToClient message) {
        synchronized (clients) {
            VirtualViewRmi client = clients.get(playerId);
            if (client == null) {
                return;
            }

            try {
                client.onMessage(message);
            } catch (RemoteException e) {
                System.out.println("Client disconnesso durante invio mirato.");
            }
        }
    }
}