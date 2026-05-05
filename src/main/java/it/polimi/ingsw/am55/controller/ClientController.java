package it.polimi.ingsw.am55.controller;

import it.polimi.ingsw.am55.network.rmi.client.RmiClient;

import java.rmi.RemoteException;

public class ClientController implements UserActionHandler {

    private final RmiClient rmiClient;

    public ClientController(RmiClient rmiClient) {
        this.rmiClient = rmiClient;
    }

    @Override
    public void onCreateGameSelected(String playerId, String totemColor, int numPlayers) {
        try {
            rmiClient.createGame(playerId, totemColor, numPlayers);
        } catch (RemoteException e) {
            System.err.println("Errore durante createGame: " + e.getMessage());
        }
    }

    @Override
    public void onJoinGameSelected(String playerId, String totemColor) {

        try {
            rmiClient.joinGame(playerId, totemColor);
        } catch (RemoteException e) {
            System.err.println("Errore durante joinGame: " + e.getMessage());
        }
    }

    @Override
    public void onPlaceTotemSelected(int index) {

        try {
            rmiClient.placeTotem(index);
        } catch (RemoteException e) {
            System.err.println("Errore durante placeTotem: " + e.getMessage());
        }
    }

    @Override
    public void onPickCardSelected(String playerId, int cardId) {
        try {
            rmiClient.pickCard(playerId, cardId);
        } catch (RemoteException e) {
            System.err.println("Errore durante createGame: " + e.getMessage());
        }
    }
}
