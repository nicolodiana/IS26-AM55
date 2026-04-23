package it.polimi.ingsw.am55.network.rmi.client;

import it.polimi.ingsw.am55.controller.ClientController;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.rmi.client.VirtualViewRmi;
import it.polimi.ingsw.am55.network.rmi.server.VirtualServerRmi;
import it.polimi.ingsw.am55.ClientModel.CliModel;
import it.polimi.ingsw.am55.view.CLIPView;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Questa classe rappresenta la logica di rete del client implementata con tecnologia RMI.
 * OGNI VIEW CLIENT HA UNA CLASSE RMICLIENT PER ACCEDERE ALLA RETE
 *
 * Si occupa di:
 * - mantenere il riferimento remoto al server
 * - registrarsi al server tramite connect(...)
 * - ricevere i messaggi dal server tramite onMessage(...)
 * - inoltrare i messaggi ricevuti al model client
 * - offrire metodi di rete chiamabili dal ClientController
 */
public class RmiClient extends UnicastRemoteObject implements VirtualViewRmi {

    private final VirtualServerRmi server;   // riferimento remoto al server
    private final CliModel model;         // model del client
    private String playerId;           // id del giocatore client

    public RmiClient(VirtualServerRmi server, CliModel model) throws RemoteException {
        super(); // esporta l'oggetto remoto client-side
        this.server = server;
        this.model = model;
        this.playerId = null;//inizialmente nulla, dopo la join/createGame assume valore passato da client
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String serverName = "GameServer";
        String host = args[0];
        // chiedo al registry il riferimento all'oggetto remoto del server
        Registry registry = LocateRegistry.getRegistry(host, 1234);

        // lookup e cast allo stub del server
        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(serverName);

        // creo model e view client
        CliModel model = new CliModel();

        CLIPView view = new CLIPView();
                /*questo dopo per gestire la registrazione degli observer al model
         la view osserva il model
        model.registerObserver(view);
        */
        // creo il client RMI
        RmiClient rmiClient = new RmiClient(server, model);

        // run registra questo client al server per ricevere callback
        rmiClient.run();

        // qui poi collegherai controller e view
        //perchè la view deve sapere chi è lo UserActionHandler a cui comunicare gli eventi, e il controller a sua volta
        //deve sapere qual'è la classe a cui far gestire il contatto con l'esterno cioè RMIclient

        ClientController controller = new ClientController(rmiClient);
        view.setActionHandler(controller);

        // eventualmente fai partire la CLI
        //view.start();
    }

    /**
     * Run mi permette di registrarmi al server,
     * così il server può inviare callback con onMessage(...).
     */
    private void run() throws RemoteException {
        this.connect();
    }

    /**
     * Registra questo client presso il server,
     * così il server può inviare callback con onMessage(...).
     */
    public void connect() throws RemoteException {
        this.server.connect(playerId, this);
    }

    /**
     * Metodo remoto chiamato dal server per notificare un aggiornamento( qui ne fornisco un implementazione per collegarsi al model client vero e proprio)
     * Il model client si occupa di interpretare/applicare il messaggio.
     */
    @Override
    public void onMessage(MessageToClient message) throws RemoteException {
        model.update(message);
    }

    /**
     * Metodo di rete chiamato dal ClientController quando l'utente
     * seleziona placeTotem dalla view.
     * si suppone che quando viene chiamato il main del Rmi Client sia stato fatto quindi
     */
    public void createGame(String playerId, String totemColor, int numPlayers) throws RemoteException {
        this.playerId = playerId;
        this.server.connect(playerId, this);
        this.server.createGame(playerId, totemColor, numPlayers);
    }
  /*
    public void joinGame(String playerId, String totemColor) throws RemoteException {
        this.playerId = playerId;
        this.server.connect(playerId, this);
        this.server.joinGame(playerId, totemColor);
    }

    public void placeTotem(int index) throws RemoteException {
        if (playerId == null) {
            throw new RemoteException("Player non ancora registrato tramite createGame/joinGame.");
        }
        server.placeTotem(playerId, index);
    }

    /*
    public void pickCard(int cardId) throws RemoteException {
        server.pickCard(playerId, cardId);
    }

    public void endTurn() throws RemoteException {
        server.endTurn(playerId);
    }
    */
}