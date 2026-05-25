package it.polimi.ingsw.am55.message;

/**
 * implementati dall'RMI SERVER , perchè lui ha il riferimento ai client nella rete
 */
public interface MessageDelivery {
    void sendTo(String playerId, MessageToClient message);
    void broadcast(MessageToClient message);
    //Consente di effettuare un broadcast per i player in lobby, come ad esempio l' ingresso di un giocatore in partita
    //oppure la creazione di una nuova partita
    void broadcastToLobby(MessageToClient message);
    //Consente di inviare un messaggio unico ai player in lobby (comunicazione unicast)
    void sendToSession(String sessionId, MessageToClient message);

}