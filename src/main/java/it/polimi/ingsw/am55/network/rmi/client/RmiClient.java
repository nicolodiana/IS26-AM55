package it.polimi.ingsw.am55.network.rmi.client;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.ClientCommands;
import it.polimi.ingsw.am55.network.rmi.server.VirtualServerRmi;

import java.rmi.RemoteException;
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
public class RmiClient extends UnicastRemoteObject implements VirtualViewRmi, ClientCommands {

    private final VirtualServerRmi server;
    private final ClientModel model;
    private String playerId;

    public RmiClient(VirtualServerRmi server, ClientModel model) throws RemoteException {
        super();
        this.server = server;
        this.model = model;
        this.playerId = null;
    }

    @Override
    public void onMessage(MessageToClient message) throws RemoteException {
        model.update(message);
    }

    @Override
    public void createGame(String playerId, String totemColor, int numPlayers) throws RemoteException {
        this.playerId = playerId;
        server.connect(playerId, this);
        server.createGame(playerId, totemColor, numPlayers);
    }

    @Override
    public void joinGame(String playerId, String totemColor) throws RemoteException {
        this.playerId = playerId;
        server.connect(playerId, this);
        server.joinGame(playerId, totemColor);
    }

    @Override
    public void placeTotem(int index) throws RemoteException {
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
    public String getPlayerId() {
        return playerId;
    }

    /*In questa classe bisognerà aggiungere un thread
    che si attiva periodicamente e faccia una richiesta
    remota la server, chiamando la funzione ping
    * */
}