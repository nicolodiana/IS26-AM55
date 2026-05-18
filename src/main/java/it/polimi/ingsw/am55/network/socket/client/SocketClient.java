package it.polimi.ingsw.am55.network.socket.client;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.command.QuitGameCommand;
import it.polimi.ingsw.am55.network.ClientCommands;
import it.polimi.ingsw.am55.network.command.*;
import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class SocketClient implements ClientCommands, ClientConnectionControl {

    private final ClientModel model;
    private ObjectInputStream input;
    private final ObjectOutputStream output;
    private final Socket socket;
    private String playerId;
    private Timer timer;//Consente di schedulare un thread in modo da lanciarlo periodicamente
    private volatile boolean running = true;
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

        runVirtualServer();
    }

    //Gestione del ping delegata ad un esecutore che viene attivato periodicamente
    //1.5 s
    @Override
    public synchronized void startPing(){
        if (pingStarted) {
            return;
        }
        pingStarted = true;

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    sendCommand(new PingCommand());
                } catch (Exception e) {
                    System.out.println("[SOCKET_CLIENT] Invio ping fallito. Chiudo il client.");
                    timer.cancel(); //Chiudo il ping verso il server
                }
            }
        }, 0, 1500);
    }
    //Serve per leggere solo le risposte che invia il server e per applicare le modifiche sul model del client
    private void runVirtualServer(){
        virtualServerThread = new Thread(() -> {
            while (running) {
                try {
                    MessageToClient response = (MessageToClient) input.readObject();

                    if(response != null){
                        if(response.shouldUpdateModel()){
                            synchronized (model) {
                                model.update(response);
                                if(model.isGameEnded() || model.isGameCrashed()){
                                    sendCommand(new CloseConnectionCommand(this.playerId));
                                }
                            }
                        }else{
                            response.executeClientNetworkAction(this);
                        }
                    }
                    //Serve perché voglio che il socket client invii verso il server una richiesta
                    //di disconnessione solo se il game è terminat.

                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("[SOCKET_CLIENT] Connessione con il server interrotta.");
                    running = false;
                    try {
                        close();
                    } catch (Exception ex) {
                        System.err.println("[SOCKET_CLIENT] Errore durante la chiusura del socket."+ex.getMessage());
                    }
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
        this.playerId = playerId;
        sendCommand(new CreateGameCommand(playerId, totemColor, numPlayers));
    }

    @Override
    public void joinGame(String playerId, String totemColor) throws Exception {
        this.playerId = playerId;
        sendCommand(new JoinGameCommand(playerId, totemColor));
    }

    @Override
    public void placeTotem(int index) throws Exception {
        if(playerId==null){
            throw new Exception("[SOCKET_CLIENT] Il playerId non trovat0.");
        }
        sendCommand(new PlaceTotemCommand(this.playerId, index));
    }

    @Override
    public void pickCard(String playerId, int cardId) throws Exception {
        if(playerId==null){
            throw new Exception("[SOCKET_CLIENT] Il playerId non trovat0.");
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
        timer.cancel(); //Se il client chiede la disconnessione => smetto di pingare verso il server
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
