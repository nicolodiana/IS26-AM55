package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.network.ClientConnectionControl;

public class WaitingMessage extends MessageToClient {

    private final String message;
    private final GameView gameView;

    public WaitingMessage(String message, GameView gameView) {
        this.message = message;
        this.gameView = gameView;
    }

    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setGameView(gameView); //gli passo la Game View solo per lo stato created essenzialmente
        model.setStateRequest(message);
        model.setGameStarted(false);
        model.setLastMessageUpdatedGameView(false);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.sendTo(playerId, this);
    }
}
//    @Override
//    public void executeClientNetworkAction(ClientConnectionControl client) throws Exception {
//        client.startPing();
//    }
//}