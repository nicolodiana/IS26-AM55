package it.polimi.ingsw.am55.controller;

import it.polimi.ingsw.am55.network.rmi.VirtualServer;
//import it.polimi.ingsw.am55.network.rmi.client.ClientRmi;
import it.polimi.ingsw.am55.network.rmi.client.VirtualClientRmi;
import it.polimi.ingsw.am55.network.rmi.server.VirtualServerRmi;
import it.polimi.ingsw.am55.view.ClientView;
import it.polimi.ingsw.am55.view.VirtualView;

import java.rmi.RemoteException;

public class ClientController {
    private final int id;
    private final int matchId;// temporaneo deve darmelo il game
    // temporaneo intato che non abbiamo le classi state
    private String nickname;
    private String totem;
    //
    private final VirtualServerRmi server;
    private final VirtualClientRmi clientRmi;
    private VirtualView clientView;

    public ClientController(int id, int matchId, VirtualServerRmi server, VirtualClientRmi vcr) throws RemoteException {
        this.id = id;
        this.matchId = matchId;
        this.server = server;
        this.clientRmi = vcr;
    }

    // COSE TEMPORANEE PER ORA

    public String getTotem() {
        return totem;
    }

    public void setTotem(String totem) {
        this.totem = totem;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    //
    public void createGame(int matchId, int playerId, int numPlayers) throws RemoteException {
        //this.server.createGame(matchId, playerId, numPlayers, clientRmi);
        this.server.createGame(matchId, playerId, numPlayers, clientRmi);
    }

    public void joinGame(int matchId, int playerId) {
        try {
            this.server.joinGame(matchId, playerId, clientRmi);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void showUpdate(String message) {
        this.clientView.onUpdateStateRequest(message);
    }

    // ------------------METODI GET----------------------
    public int getId() {
        return id;
    }

    public int getMatchId() {
        return matchId;
    }
    // ------------------METODI SET----------------------
    //we need it due to the controller and view that need each other so we need to create one and then to add the other after
    public void setClientView(VirtualView clientView) {
        this.clientView = clientView;
    }
}
