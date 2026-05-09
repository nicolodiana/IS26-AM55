package it.polimi.ingsw.am55.network.socket.Server;


import it.polimi.ingsw.am55.controller.GameController;
import it.polimi.ingsw.am55.message.ErrorMessage;
import it.polimi.ingsw.am55.message.MessageToClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//Deve contenere un riferimento al controller perché esso servirà ad invocare i metodi del model
// all' interno dei metodi che la classe implementa ad esempio createGame
//Si occupa di accogliere i metodi del client e trasformarli a chiamate verso i metodi nel model attraverso il controller
public class SocketClientHandler {
//    private final ObjectOutputStream outputStream;
//    private final BufferedReader inputStream;
//    private final SocketServer server;
//    private final GameController gameController;
//
    private String playerId;

//    public SocketClientHandler(ObjectOutputStream outputStream, BufferedReader inputStream, SocketServer server, GameController gameController) {
//        this.outputStream = outputStream ;
//        this.inputStream = inputStream;
//        this.server = server;
//        this.gameController = gameController;
//
//        initCommands();
//    }
//    private JsonObject readFromJson() throws IOException {
//        String jsonLine = inputStream.readLine();
//        if(jsonLine!=null){
//            return new JsonParser().parse(jsonLine).getAsJsonObject();
//        }
//        return null;
//    }
//    public String getPlayerId() {return playerId;}
//    //Si occuperà di andare a tradurre i comandi provenienti dal client verso il controller
//    public void runVirtualView() throws IOException {
//        JsonObject command ;
//        while((command = readFromJson())!=null){
//            try{
//                if(!command.has("command")){
//                  //  throw new InvalidCommandException("The command requested is invalid");
//                }
//                String commandName = command.get("command").getAsString();
//                Function<JsonObject,MessageToClient> commandFunction = commands.get(commandName);
//
//                MessageToClient response = commandFunction.apply(command.getAsJsonObject("params"));
//
//                if(response!=null){
//                    response.deliver(this.playerId,server);
//                }
//            }catch(Exception e){
//                MessageToClient message = new ErrorMessage(e.getMessage());
//                message.deliver(this.playerId,server);
//            }
//        }
//
//    }
//    //Inizziallizzo tutti i comandi che il client può eventualmente invocare, e quali sono le operazioni da compiere
//    private void initCommands(){
//
//        commands.put("create game", (params)-> {
//            synchronized (gameController) {
//                MessageToClient message = this.gameController.createGame(params.get("nickname").getAsString(), params.get("color").getAsString(),
//                        params.get("numPlayers").getAsInt());
//                this.playerId=params.get("playerId").getAsString();
//                return message;
//            }
//        });
//        //Il controller gestisce già la cattura delle eccezioni=> non le catturo perché avrò il messaggio giusto
//        //in base al fatto che l' eccezione sia avventua o meno
//        commands.put("join game", (params)->{
//             synchronized (gameController) {
//                 MessageToClient message = this.gameController.joinGame(params.get("nickname").getAsString(),
//                         params.get("totem").getAsString());
//                 this.playerId=params.get("playerId").getAsString();
//                 return message;
//             }
//        });
//        /*commands.put("place totem", (params)->{
//            synchronized(gameController) {
//                this.gameController.placetotem(params.get("nickname").getAsString(),
//                        params.get("index").getAsString());
//            }
//
//         });*/
//        /*commands.put("pick card", (params)->{
//            synchronized (gameController) {
//                this.gameController.pickCard(params.get("nickname").getAsString(),
//                        params.get("cardID").getAsString());
//            }
//
//        });*/
//        /*commands.put("pick special", (params)->{
//            synchronized (gameController) {
//                this.gameController.pickSpecial(params.get("nickname").getAsString(),
//                        params.get("cardID").getAsString());
//            }
//
//        });*/
//        /*commands.put("pick food", (params)->{
//        synchronized (gameController) {
//                return  this.gameController.pickFood(params.get("nickname").getAsString(),
//            params.get("cardID").getAsString());
//            }
//
//        });*/
//        /*commands.put("end game", (params)->{
//        synchronized (gameController) {
//                return this.gameController.EndGame();
//            }
//
//        });*/
//    }
//
//
//    @Override
//    public synchronized void onMessage(MessageToClient message) throws Exception {
//        if (message == null) {
//            return;
//        }
//        outputStream.reset();
//        outputStream.writeObject(message);
//        outputStream.flush();
//    }

}
