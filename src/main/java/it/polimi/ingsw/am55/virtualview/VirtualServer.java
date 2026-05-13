package it.polimi.ingsw.am55.virtualview;

/**
 * Interfaccia che definisce i metodi che il client
 * può invocare sul server.
 * Rappresenta il contratto logico client -> server,
 * indipendente dalla tecnologia di comunicazione usata.
 */
public interface VirtualServer {

    /**
     * Azione richiesta dal client: placeTotem.
     */

    void createGame(String playerId, String totemColor, int numPlayers) throws Exception;

    void joinGame(String playerId, String totemColor) throws Exception;

    void placeTotem(String playerId, int index) throws Exception;

    void pickCard(String playerId, int cardId) throws Exception;
    void pickSpecial(String playerId, int cardId) throws Exception;

    void ping (VirtualView client) throws Exception;

    void quitGame(String id) throws Exception;

    void closeConnection(VirtualView sender) throws Exception;
    /*

    void endTurn(String playerId) throws Exception;
    */
}