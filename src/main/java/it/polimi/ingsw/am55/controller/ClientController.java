package it.polimi.ingsw.am55.controller;

import it.polimi.ingsw.am55.network.ClientImpl;
import it.polimi.ingsw.am55.network.command.CreateGameCommand;
import it.polimi.ingsw.am55.network.command.JoinGameCommand;
import it.polimi.ingsw.am55.network.command.PickCardCommand;
import it.polimi.ingsw.am55.network.command.PickSpecialCommand;
import it.polimi.ingsw.am55.network.command.PlaceTotemCommand;
import it.polimi.ingsw.am55.network.command.QuitGameCommand;
import it.polimi.ingsw.am55.network.command.QuitLobbyCommand;

/**
 * Translates user actions into server commands sent through the client connection.
 */
public class ClientController implements UserActionHandler {

    /**
     * Client associated
     */
    private final ClientImpl client;

    /**
     * Creates a controller for the specified client connection.
     *
     * @param client client used to send commands to the server
     */
    public ClientController(ClientImpl client) {
        this.client = client;
    }

    /**
     * Sends a command to create a game with the selected player settings.
     *
     * @param playerId   identifier of the player creating the game
     * @param totemColor totem color selected by the player
     * @param numPlayers number of players required for the game
     */
    @Override
    public void onCreateGameSelected(String playerId, String totemColor, int numPlayers) {
        try {
            client.sendCommand(
                    new CreateGameCommand(playerId, totemColor, numPlayers, client.getSessionId())
            );
        } catch (Exception e) {
            System.err.println("Error while createGame: " + e.getMessage());
        }
    }

    /**
     * Sends a command to join the active game with the selected player settings.
     *
     * @param playerId   identifier of the joining player
     * @param totemColor totem color selected by the player
     */
    @Override
    public void onJoinGameSelected(String playerId, String totemColor) {
        try {
            client.sendCommand(
                    new JoinGameCommand(playerId, totemColor, client.getSessionId())
            );
        } catch (Exception e) {
            System.err.println("Error while joinGame: " + e.getMessage());
        }
    }

    /**
     * Sends a command to place a player's totem at the selected index.
     *
     * @param playerId identifier of the player performing the action
     * @param index    target bidding-ticket index
     */
    @Override
    public void onPlaceTotemSelected(String playerId, int index) {
        try {
            client.sendCommand(new PlaceTotemCommand(playerId, index));
        } catch (Exception e) {
            System.err.println("Error while placeTotem " + e.getMessage());
        }
    }

    /**
     * Sends a command for the selected player to leave the active game.
     *
     * @param playerId identifier of the player leaving the game
     */
    @Override
    public void onQuitGameSelected(String playerId) {
        try {
            client.sendCommand(new QuitGameCommand(playerId));
        } catch (Exception e) {
            System.err.println("Error while quitGame " + e.getMessage());
        }
    }

    /**
     * Sends a command to leave the lobby using the current client session.
     */
    @Override
    public void onQuitSelectedLobby() {
        try {
            client.sendCommand(new QuitLobbyCommand(client.getSessionId()));
        } catch (Exception e) {
            System.err.println("Error while quitLobby: " + e.getMessage());
        }
    }

    /**
     * Sends a command to pick the selected normal card.
     *
     * @param playerId identifier of the player performing the action
     * @param cardId   identifier of the selected card
     */
    @Override
    public void onPickCardSelected(String playerId, int cardId) {
        try {
            client.sendCommand(new PickCardCommand(playerId, cardId));
        } catch (Exception e) {
            System.err.println("Error while pickCard: " + e.getMessage());
        }
    }

    /**
     * Sends a command to pick the selected special card.
     *
     * @param playerId identifier of the player performing the action
     * @param cardId   identifier of the selected special card
     */
    @Override
    public void onPickSpecialSelected(String playerId, int cardId) {
        try {
            client.sendCommand(new PickSpecialCommand(playerId, cardId));
        } catch (Exception e) {
            System.err.println("Error while pickSpecial: " + e.getMessage());
        }
    }
}