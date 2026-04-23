package it.polimi.ingsw.am55.message;

/**
 * implementati dall'RMI SERVER , perchè lui ha il riferimento ai client nella rete
 */
public interface MessageDelivery {
    void sendTo(String playerId, MessageToClient message);
    void broadcast(MessageToClient message);
}