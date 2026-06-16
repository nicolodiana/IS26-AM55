package it.polimi.ingsw.am55.controller;

import it.polimi.ingsw.am55.network.ClientImpl;
import it.polimi.ingsw.am55.network.command.CreateGameCommand;
import it.polimi.ingsw.am55.network.command.JoinGameCommand;
import it.polimi.ingsw.am55.network.command.PickCardCommand;
import it.polimi.ingsw.am55.network.command.PickSpecialCommand;
import it.polimi.ingsw.am55.network.command.PlaceTotemCommand;
import it.polimi.ingsw.am55.network.command.QuitGameCommand;
import it.polimi.ingsw.am55.network.command.QuitLobbyCommand;

public class ClientController implements UserActionHandler {

    private final ClientImpl client;

    public ClientController(ClientImpl client) {
        this.client = client;
    }

    @Override
    public void onCreateGameSelected(String playerId, String totemColor, int numPlayers) {
        try {
            client.sendCommand(
                    new CreateGameCommand(playerId, totemColor, numPlayers, client.getSessionId())
            );
        } catch (Exception e) {
            System.err.println("Errore durante createGame: " + e.getMessage());
        }
    }

    @Override
    public void onJoinGameSelected(String playerId, String totemColor) {
        try {
            client.sendCommand(
                    new JoinGameCommand(playerId, totemColor, client.getSessionId())
            );
        } catch (Exception e) {
            System.err.println("Errore durante joinGame: " + e.getMessage());
        }
    }

    @Override
    public void onPlaceTotemSelected(String playerId, int index) {
        try {
            client.sendCommand(new PlaceTotemCommand(playerId, index));
        } catch (Exception e) {
            System.err.println("Errore durante placeTotem: " + e.getMessage());
        }
    }

    @Override
    public void onQuitGameSelected(String playerId) {
        try {
            client.sendCommand(new QuitGameCommand(playerId));
        } catch (Exception e) {
            System.err.println("Errore durante quitGame: " + e.getMessage());
        }
    }

    @Override
    public void onQuitSelectedLobby() {
        try {
            client.sendCommand(new QuitLobbyCommand(client.getSessionId()));
        } catch (Exception e) {
            System.err.println("Errore durante quitLobby: " + e.getMessage());
        }
    }

    @Override
    public void onPickCardSelected(String playerId, int cardId) {
        try {
            client.sendCommand(new PickCardCommand(playerId, cardId));
        } catch (Exception e) {
            System.err.println("Errore durante pickCard: " + e.getMessage());
        }
    }

    @Override
    public void onPickSpecialSelected(String playerId, int cardId) {
        try {
            client.sendCommand(new PickSpecialCommand(playerId, cardId));
        } catch (Exception e) {
            System.err.println("Errore durante pickSpecial: " + e.getMessage());
        }
    }
}