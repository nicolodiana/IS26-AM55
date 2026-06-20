package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

/**
 * Messaggio locale client-side.
 * Non arriva dal server.
 * Viene creato dal ClientImpl quando il client non riceve più Pong.
 */
public class ConnectionLostMessage extends MessageToClient {

    private final String message;

    public ConnectionLostMessage(String message) {
        this.message = message;
    }

    @Override
    public void update(ClientModel model) {
        model.clearError();

        model.setStateRequest(message);

        model.setGameStarted(false);
        model.setGameEnded(true);
        model.setGameCrashed(true);

        // Importante: se perdi connessione mentre sei ancora in lobby,
        // la CLI non deve più trattarti come client in lobby.
        model.setInLobby(false);

        model.setLastMessageUpdatedGameView(false);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        // Messaggio locale: non deve essere consegnato dal server.
    }
}