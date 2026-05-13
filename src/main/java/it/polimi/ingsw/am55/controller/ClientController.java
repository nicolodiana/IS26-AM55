package it.polimi.ingsw.am55.controller;

import it.polimi.ingsw.am55.network.ClientCommands;

public class ClientController implements UserActionHandler {
/*il riferimento che ha il controller è un riferimento generico ClientCommands, poi in base alla tecnologia scelta l'override eseguira
i metodi di socket o di RMI
 */
    private final ClientCommands client;

    public ClientController(ClientCommands client) {
        this.client = client;
    }

    @Override
    public void onCreateGameSelected(String playerId, String totemColor, int numPlayers) {
        try {
            client.createGame(playerId, totemColor, numPlayers);
        } catch (Exception e) {
            System.err.println("Errore durante createGame: " + e.getMessage());
        }
    }

    @Override
    public void onJoinGameSelected(String playerId, String totemColor) {
        try {
            client.joinGame(playerId, totemColor);
        } catch (Exception e) {
            System.err.println("Errore durante joinGame: " + e.getMessage());
        }
    }

    @Override
    public void onPlaceTotemSelected(int index) {
        try {
            client.placeTotem(index);
        } catch (Exception e) {
            System.err.println("Errore durante placeTotem: " + e.getMessage());
        }
    }

    @Override
    public void onQuitGameSelected(String playerId) {
        try{
            client.quitGame(playerId);
        }catch(Exception e){
            System.err.println("Errore durante quitGame: " + e.getMessage());
        }
    }



    @Override
    public void onPickCardSelected(String playerId, int cardId) {
        try {
            client.pickCard(playerId, cardId);
        } catch (Exception e) {
            System.err.println("Errore durante pickCard: " + e.getMessage());
        }
    }

    @Override
    public void onPickSpecialSelected(String playerId, int cardId) {
        try {
            client.pickSpecial(playerId, cardId);
        } catch (Exception e) {
            System.err.println("Errore durante pickSpecial: " + e.getMessage());
        }
    }
}