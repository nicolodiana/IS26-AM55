package it.polimi.ingsw.am55.ClientModel;

import it.polimi.ingsw.am55.message.MessageToClient;

/**
 * Model locale del client CLI.
 *
 * Mantiene una rappresentazione semplificata dello stato lato client
 * e viene aggiornato dai MessageToClient ricevuti dal server.
 *
 * Il dispatch dinamico viene fatto dai messaggi concreti tramite
 * message.update(this), evitando instanceof dentro il model.
 */
public class CliModel {

    private int numPlayers;
    private String lastError;
    private int lastPlacedTotemIndex;
    private String currentPlayer;

    public CliModel() {
        this.numPlayers = 0;
        this.lastError = null;
        this.lastPlacedTotemIndex = -1;
        this.currentPlayer = null;
    }

    /**
     * Metodo chiamato da RmiClient quando arriva un messaggio dal server.
     *
     * Qui non facciamo instanceof:
     * il messaggio concreto decide da solo come aggiornare il model.
     */
    public void update(MessageToClient message) {
        message.update(this); //qui chiamo il metodo update della classe concreta che sceglierà quali dei
        // miei metodi usare per aggiornarmi lo stato
    }

    /**
     * Aggiornamento applicato quando arriva un GameCreatedMessage.
     */
    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    /**
     * Aggiornamento applicato quando arriva un ErrorMessage.
     */
    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    /**
     * Aggiornamento applicato quando arriva un BoardUpdatedMessage.
     */
    public void setLastPlacedTotemIndex(int lastPlacedTotemIndex) {
        this.lastPlacedTotemIndex = lastPlacedTotemIndex;
    }

    /**
     * Aggiornamento applicato quando cambia il current player.
     */
    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getNumPlayers() {
        return numPlayers;
    }


    public String getCurrentPlayer() {
        return currentPlayer;
    }
}