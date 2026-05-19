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

    private final VirtualServerRmi server;
    private final ClientModel model;
    private String playerId;
    private Timer timer;
    private boolean pingStarted;

    public RmiClient(VirtualServerRmi server, ClientModel model) throws RemoteException {
        super();
        this.server = server;
        this.model = model;
        this.playerId = null;
        this.timer = new Timer(true);
        this.pingStarted = false;
    }

    @Override
    public void onMessage(MessageToClient message) throws RemoteException {
        if(message.shouldUpdateModel()){
            model.update(message);
            if(model.isGameCrashed() || model.isGameEnded()){//Se rilevo la fine della paritita oppure game crashato =>chiudo tutte le connessioni
                server.closeConnection(this.playerId);
            }
        }else{
            try{
                message.executeClientNetworkAction(this);
            }catch (Exception e){
                System.out.println("[RMI CLIENT] Non è possibile avviare il ping");
            }
        }
    }

    @Override
    public void close() throws RemoteException {
        timer.cancel();
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (Exception ignored) {}
        System.out.println("[RMI CLIENT] Chiusura avvenuta.");
    }


    @Override
    public void createGame(String playerId, String totemColor, int numPlayers) throws RemoteException {
        this.playerId = playerId;
        server.createGame(playerId, totemColor, numPlayers, this);
    }

    @Override
    public void joinGame(String playerId, String totemColor) throws RemoteException {
        this.playerId = playerId;
        server.joinGame(playerId, totemColor, this);
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
    public void quitGame(String playerId) throws Exception {
        server.quitGame(playerId);
    }

    /*In questa classe bisognerà aggiungere un thread
    che si attiva periodicamente e faccia una richiesta
    remota la server, chiamando la funzione ping
    * */
    @Override
    public synchronized void startPing(){
        if (pingStarted) {
            return;
        }
        pingStarted = true;

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    pingToServer();
                } catch (Exception e) {
                    System.out.println("[RMI_CLIENT] Invio ping fallito. Chiudo il client.");
                    try {
                        close();
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }, 0, 1500);
    }
    private void pingToServer() throws Exception {
        server.ping(this);
    }
}