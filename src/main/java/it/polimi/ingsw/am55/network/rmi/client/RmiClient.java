package it.polimi.ingsw.am55.network.rmi.client;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.ClientController;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.rmi.server.VirtualServerRmi;
import it.polimi.ingsw.am55.view.CLIView;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

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
public class RmiClient extends UnicastRemoteObject implements VirtualViewRmi {

    private final VirtualServerRmi server;
    private final ClientModel model;
    private String playerId;

    public RmiClient(VirtualServerRmi server, ClientModel model) throws RemoteException {
        super();
        this.server = server;
        this.model = model;
        this.playerId = null;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String serverName = "GameServer";

        Registry registry = LocateRegistry.getRegistry(
                args.length > 0 ? args[0] : "localhost",
                1234
        );

        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(serverName);

        ClientModel model = new ClientModel();

        CLIView view = new CLIView();
        model.addObserver(view);

        RmiClient rmiClient = new RmiClient(server, model);

        ClientController controller = new ClientController(rmiClient);
        view.setActionHandler(controller);

        view.start();
    }

    /**
     * Metodo remoto chiamato dal server per notificare un messaggio al client.
     * Il ClientModel applica il messaggio e notifica le view observer.
     */
    @Override
    public void onMessage(MessageToClient message) throws RemoteException {
        model.update(message);
    }

    /**
     * Metodo chiamato dal ClientController quando l'utente vuole creare una partita.
     */
    public void createGame(String playerId, String totemColor, int numPlayers) throws RemoteException {
        this.playerId = playerId;

        server.connect(playerId, this);
        server.createGame(playerId, totemColor, numPlayers);
    }

    /**
     * Metodo chiamato dal ClientController quando l'utente vuole unirsi a una partita.
     */
    public void joinGame(String playerId, String totemColor) throws RemoteException {
        this.playerId = playerId;

        server.connect(playerId, this);
        server.joinGame(playerId, totemColor);
    }

    /**
     * Metodo chiamato dal ClientController quando l'utente vuole piazzare il totem.
     */
    public void placeTotem(int index) throws RemoteException {
        if (playerId == null) {
            throw new RemoteException("Player non ancora registrato tramite createGame/joinGame.");
        }

        server.placeTotem(playerId, index);
    }

    public String getPlayerId() {
        return playerId;
    }
}