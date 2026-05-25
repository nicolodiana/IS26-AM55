package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.dto.LobbyView;

public class LobbyStatusMessage extends MessageToClient {

    private final LobbyView lobbyView;
    private final String message;

    public LobbyStatusMessage(LobbyView lobbyView, String message) {
        this.lobbyView = lobbyView;
        this.message = message;
    }

    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setInLobby(true);
        model.setLobbyView(lobbyView);
        model.setStateRequest(message);
        model.setLastMessageUpdatedGameView(false);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcastToLobby(this);
    }
}