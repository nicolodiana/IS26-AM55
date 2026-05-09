package it.polimi.ingsw.am55.Socket;
import it.polimi.ingsw.am55.virtualview.VirtualServer;

public interface VirtualServerSocket extends VirtualServer {

    void createGame(String playerID, String totemColor, int numPlayers);

    void joinGame(String playerID,String totemColor) throws Exception;

    void placeTotem(String playerID,int index) throws Exception;

    void pickCard(int cardID, String playerID)throws Exception;

    void pickSpecial(int cardID, String playerID)throws Exception;

    void pickFood(int cardID, String playerID)throws Exception;

}