package it.polimi.ingsw.am55.network;

public interface ClientCommands {

    void createGame(String playerId, String totemColor, int numPlayers) throws Exception;

    void joinGame(String playerId, String totemColor) throws Exception;

    void placeTotem(String playerId,int index) throws Exception;

    void pickCard(String playerId, int cardId) throws Exception;

    void pickSpecial(String playerId, int cardId) throws Exception;

    void quitGame(String playerId) throws Exception;

    void quitLobby() throws Exception;

}