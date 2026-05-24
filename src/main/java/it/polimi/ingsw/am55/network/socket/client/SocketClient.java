package it.polimi.ingsw.am55.network.socket.client;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.ClientConnectionControl;
import it.polimi.ingsw.am55.network.command.QuitGameCommand;
import it.polimi.ingsw.am55.network.ClientCommands;
import it.polimi.ingsw.am55.network.command.*;
import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class SocketClient implements ClientCommands , ClientConnectionControl {

    private final ClientModel model;
    private ObjectInputStream input;
    private final ObjectOutputStream output;
    private final Socket socket;
    private String playerId;
    private Timer timer;
    private Timer timerChekerAliver;//Consente di schedulare un thread in modo da lanciarlo periodicamente
    private volatile boolean running = true;
    private volatile boolean checkerAliverActive =false;
    private Long lastPingFromServer;
    private final Object pingLock;
    private Thread virtualServerThread;
    private boolean pingStarted;

    //La signature del metodo lancia un' eccezione che sarà propagata all' interno della classe Client comune
    public SocketClient(String host, int port ,ClientModel model) throws IOException {
        this.model = model;
        this.socket = new Socket(host, port); //Client si collega al server
        this.input = new ObjectInputStream(socket.getInputStream());
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.timer = new Timer(true);
        this.pingStarted = false;
        this.timerChekerAliver = new Timer(true);
        this.playerId="";
        this.pingLock = new Object();
        runVirtualServer();
    }

    //Gestione del ping delegata ad un esecutore che viene attivato periodicamente
    //1.5 s
    @Override
    public void startPing(){
        if (pingStarted) {
            return;
        }
        pingStarted = true;

        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    if(!checkerAliverActive){
                        synchronized (pingLock) {
                            lastPingFromServer = System.currentTimeMillis();
                        }
                        checkerAliverActive = true;
                        checkerAliver();
                    }
                    sendCommand(new PingCommand());
                } catch (Exception e) {
                    System.out.println("[SOCKET_CLIENT] Invio ping fallito. Chiudo il client.");
                    timer.cancel(); //Chiudo il ping verso il server
                }
            }
        }, 0, 1500);
    }

    @Override
    public void stopPing(){
        timer.cancel();
        timerChekerAliver.cancel();
    }

    @Override
    public void pongFromSever() {
        synchronized (pingLock) {
                lastPingFromServer = System.currentTimeMillis();
        }
    }
    public void checkerAliver(){
        timerChekerAliver.schedule(new TimerTask() {
            public void run() {
                long now = System.currentTimeMillis();
                synchronized (pingLock) {
                    if(now-lastPingFromServer>6000){
                        try {
                            System.out.println("[SOCKET_CLIENT] Sto chiudendo il client");
                            close();
                        } catch (IOException e) {
                            System.out.println("[SOCKET_CLIENT] Impossibile chiudere il client");
                        }
                    }
                }
            }
        }, 1500, 1500);
    }
    //Serve per leggere solo le risposte che invia il server e per applicare le modifiche sul model del client
    private void runVirtualServer(){
        virtualServerThread = new Thread(() -> {
            while (running) {
                try {
                    MessageToClient response = (MessageToClient) input.readObject();

                    if(response != null){
                        response.executeClientNetworkAction(this);
                        if(response.shouldUpdateModel()){
                            synchronized (model) {
                                model.update(response);
                                if(model.isGameEnded() || model.isGameCrashed()){
                                    //sendCommand(new CloseConnectionCommand(this.playerId));
                                    close();
                                    System.out.println("[SOCKET_CLIENT] Richiesta terminata");
                                    break;
                                }
                            }
                        }
                    }
                    //Serve perché voglio che il socket client invii verso il server una richiesta
                    //di disconnessione solo se il game è terminat.

                } catch (IOException | ClassNotFoundException e) {
                    break;
                } catch (Exception e) {
                    System.out.println("[SOCKET_CLIENT] Richiesta disconnessione non andata a buon fine "+ e.getMessage());
                }
            }
        });

        virtualServerThread.setName("virtual-server-reader");
        virtualServerThread.start();
    }

    public void close() throws IOException {
        running = false;
        try {
            timer.cancel(); //Viene interrotto il ping
        } catch (Exception ignored) {}
        try {
            timerChekerAliver.cancel(); //Viene interrotto il ping
        } catch (Exception ignored) {}
        try {
            input.close();
        } catch (Exception ignored) {}
        try {
            output.close();
        } catch (Exception ignored) {}
        try {
            socket.close();
        } catch (Exception ignored) {}
        System.out.println("[SOCKET_CLIENT] Il socket è stato chiuso.");
    }
    @Override
    public void createGame(String playerId, String totemColor, int numPlayers) throws Exception {
        //this.playerId = playerId;
        sendCommand(new CreateGameCommand(playerId, totemColor, numPlayers));
    }

    @Override
    public void joinGame(String playerId, String totemColor) throws Exception {
        //this.playerId = playerId;
        sendCommand(new JoinGameCommand(playerId, totemColor));
    }

    @Override
    public void placeTotem(String playerId, int index) throws Exception {
        if(playerId==null){
            throw new Exception("[SOCKET_CLIENT] Il playerId non trovat0.");
        }
        sendCommand(new PlaceTotemCommand(playerId, index));
    }

    @Override
    public void pickCard(String playerId, int cardId) throws Exception {
        if(playerId==null){
            throw new Exception("[SOCKET_CLIENT] Il playerId non trovato.");
        }
        sendCommand(new PickCardCommand(playerId, cardId));
    }

    @Override
    public void pickSpecial(String playerId, int cardId) throws Exception {
        if(playerId==null){
            throw new Exception("[SOCKET_CLIENT] Il playerId non trovat0.");
        }
        sendCommand(new  PickSpecialCommand(playerId, cardId));
    }

    @Override
    public void quitGame(String playerId) throws Exception {
        if(playerId==null){
            throw new Exception("[SOCKET_CLIENT] Il playerId non trovat0.");
        }
        //timer.cancel(); //Se il client chiede la disconnessione => smetto di pingare verso il server
        sendCommand(new QuitGameCommand(playerId));
    }

    //Permette di inviare i comandi verso il server
    public void sendCommand(ServerCommand command) throws Exception {
        synchronized (output){
            output.writeObject(command);
            output.flush();
            output.reset();
        }
    }
}
