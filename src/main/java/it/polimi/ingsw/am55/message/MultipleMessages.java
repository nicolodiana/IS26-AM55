package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite message that groups several server-to-client messages into one object.
 */
public class MultipleMessages extends MessageToClient {
    /**
     * Ordered list of messages that must be applied and delivered as a single composite network payload.
     */
    List<MessageToClient> messages = new ArrayList<>();

    /**
     * Creates a composite message.
     *
     * @param messages messages to update and deliver in order
     */
    public MultipleMessages(List<MessageToClient> messages) {
        this.messages = messages;
    }

    /**
     * Applies each contained message to the client model in order.
     *
     * @param model client-side model to update
     */
    @Override
    public void update(ClientModel model) {
        for (MessageToClient message : messages) {
            message.update(model);
        }
    }

    /**
     * Delivers each contained message using its own delivery strategy.
     *
     * @param playerId player or session identifier passed to contained messages
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        for (MessageToClient message : messages) {
            message.deliver(playerId, context);
        }
    }
}

