package it.polimi.ingsw.am55.Socket.Client;

import com.google.gson.JsonObject;
import it.polimi.ingsw.am55.Socket.VirtualServerSocket;

import java.io.PrintWriter;

public class SocketServerHandler implements VirtualServerSocket {
    final PrintWriter output;

    public SocketServerHandler(PrintWriter output) {
        this.output = output;
    }

    @Override
    public void createGame(String playerID, String totemColor, int numPlayers) {
        JsonObject command = new JsonObject();
        command.addProperty("command", "create game");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("nickname", playerID);
        jsonObject.addProperty("color", totemColor);
        jsonObject.addProperty("numPlayers", numPlayers);
        command.add("params", jsonObject);
        sendJson(jsonObject);
    }

    @Override
    public void joinGame(String playerID, String totemColor) {
        JsonObject command = new JsonObject();
        command.addProperty("command", "join game");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("nickname", playerID);
        jsonObject.addProperty("color", totemColor);
        command.add("params", jsonObject);
        sendJson(jsonObject);
    }

    @Override
    public void placeTotem(String playerID, int index){
        JsonObject command = new JsonObject();
        command.addProperty("command", "place totem");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("nickname", playerID);
        jsonObject.addProperty("index", index );
        command.add("params", jsonObject);
        sendJson(command);
    }


    @Override
    public void pickCard(int cardID, String playerID){
        JsonObject command = new JsonObject();
        command.addProperty("command", "pick card");
        command.add("params",createJson(playerID, cardID));
        sendJson(command);
    }

    @Override
    public void pickSpecial(int cardID, String playerID) {
        JsonObject command = new JsonObject();
        command.addProperty("command", "pick special");
        command.add("params",createJson(playerID, cardID));
        sendJson(command);

    }

    @Override
    public void pickFood(int cardID, String playerID) throws Exception {
        JsonObject command = new JsonObject();
        command.addProperty("command", "pick food");
        command.add("params",createJson(playerID, cardID));
        sendJson(command);
    }

    //Creates json for placetotem, pickcard , pickspecial and pickfood
    private JsonObject createJson(String nickname, int index){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("nickname", nickname);
        jsonObject.addProperty("cardID", index);
        return jsonObject;
    }

    //Allows to send json on the network from the client to server
    private void sendJson(JsonObject jsonObject){
       if(output != null){
           output.write(jsonObject.toString());
           output.flush();
           if(output.checkError()){
               System.out.println("Error the client could be disconected");
           }
       }else{
           System.out.println("Error the current output stream has not be created");
       }
    }
}
