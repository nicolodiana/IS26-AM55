package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.dto.LobbyView;

/**
 * Lobby snapshot broadcast to clients that have connected but are not yet in the game.
 */
public class LobbyStatusMessage extends MessageToClient {

    /**
     * Current lobby snapshot to render on clients waiting for the game to start.
     */
    private final LobbyView lobbyView;
    /**
     * Human-readable lobby status text shown with the snapshot.
     */
    private final String message;

    /**
     * Creates a lobby-status message.
     *
     * @param lobbyView lobby snapshot to show on the client
     * @param message   text displayed by clients
     */
    public LobbyStatusMessage(LobbyView lobbyView, String message) {
        this.lobbyView = lobbyView;
        this.message = message;
    }

    /**
     * Updates the client model with the latest lobby state.
     *
     * @param model client-side model to update
     */
    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setInLobby(true);
        model.setLobbyView(lobbyView);
        model.setStateRequest(message);
        model.setLastMessageUpdatedGameView(false);
    }

    /**
     * Broadcasts the lobby state to all lobby clients.
     *
     * @param playerId ignored because this message targets the lobby
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcastToLobby(this);
    }
}