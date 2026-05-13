package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.network.command.QuitGameCommand;

import java.io.Serializable;

public class QuitGameMessage implements MessageToClient{

    private GameView gameView;
    private String message;
    public QuitGameMessage(GameView gameView,String message){
        this.gameView=gameView;
        this.message=message;
    }
    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setGameView(gameView);
        model.setStateRequest(message);
        model.setGameEnded(true);
        model.setGameStarted(false);
        model.setLastMessageUpdatedGameView(true);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}
