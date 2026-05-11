package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

import java.util.ArrayList;
import java.util.List;

public class MultipleMessages implements MessageToClient {
    List<MessageToClient> messages = new ArrayList<>();

    public MultipleMessages(List<MessageToClient> messages) {
        this.messages = messages;
    }

    @Override
    public void update(ClientModel model) {
        for (MessageToClient message : messages) {
            message.update(model);
        }
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        for (MessageToClient message : messages) {
            message.deliver(playerId, context);
        }
    }
}
