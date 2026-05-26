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
import java.util.UUID;

public class SocketClient implements ClientCommands , ClientConnectionControl {

    private final ClientModel model;
    private final String sessionId;
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
    public SocketClient(String host, int port, ClientModel model) throws IOException {
        this.model = model;
        this.socket = new Socket(host, port);

        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.output.flush();
        this.input = new ObjectInputStream(socket.getInputStream());

        this.timer = new Timer(true);
        this.pingStarted = false;
        this.timerChekerAliver = new Timer(true);
        this.playerId = "";
        this.pingLock = new Object();
        this.sessionId = UUID.randomUUID().toString();

        runVirtualServer();

        try {
            sendCommand(new RegisterLobbyCommand(sessionId));
        } catch (Exception e) {
            throw new IOException("Registrazione lobby fallita", e);
        }
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
//                        synchronized (pingLock) {
//                            lastPingFromServer = System.currentTimeMillis();
//                        }
                        checkerAliverActive = true;
                        checkerAliver();
                    }
                    sendCommand(new PingCommand());
                } catch (Exception e) {
                    System.out.println("[SOCKET_CLIENT] Invio ping fallito ");
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
                    System.out.println( lastPingFromServer);
                    if(now-lastPingFromServer>8000){
                        System.out.println("[SOCKET_CLIENT] Server non raggiungibile: chiudo il client.");
                        closeConnection();
                    }
                }
            }
        }, 1500, 1500);
    }
    //Serve per leggere solo le risposte che invia il server e per applicare le modifiche sul model del client
    private void runVirtualServer() {
        if (virtualServerThread != null && virtualServerThread.isAlive()) {
            return;
        }

        virtualServerThread = new Thread(() -> {
            while (running) {
                try {
                    MessageToClient response = (MessageToClient) input.readObject();

                    if (response != null) {
//                        System.out.println("[SOCKET_CLIENT] Ricevuto messaggio: "
//                                + response.getClass().getSimpleName());

                        if (response.shouldUpdateModel()) {
                            synchronized (model) {
                                model.update(response);
                            }
                        }
                        response.executeClientNetworkAction(this);
                    }

                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("[SOCKET_CLIENT] Reader socket terminato: " + e.getMessage());
                    break;
                } catch (Exception e) {
                    System.out.println("[SOCKET_CLIENT] Errore gestione messaggio: " + e.getMessage());
                }
            }
        });

        virtualServerThread.setName("virtual-server-reader");
        virtualServerThread.start();
    }

    @Override
    public void closeConnection() {
        running = false;
        try {
            timer.cancel(); //Viene interrotto il ping
        } catch (Exception ignored) {}
        try {
            timerChekerAliver.cancel(); //Viene interrotto il controllo che il server sia crashato
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
        sendCommand(new CreateGameCommand(playerId, totemColor, numPlayers, sessionId));
    }

    @Override
    public void joinGame(String playerId, String totemColor) throws Exception {
        sendCommand(new JoinGameCommand(playerId, totemColor, sessionId));
    }
    @Override
    public void placeTotem(String playerId, int index) throws Exception {
        if(playerId==null){
            throw new Exception("[SOCKET_CLIENT] Il playerId non trovato.");
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

    @Override
    public void quitLobby() throws Exception {
        sendCommand(new QuitLobbyCommand(this.sessionId));
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
