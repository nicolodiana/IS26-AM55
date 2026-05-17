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

public class SocketClient implements ClientCommands {

    private final ClientModel model;
    private ObjectInputStream input;
    private final ObjectOutputStream output;
    private final Socket socket;
    private String playerId;
    private Timer timer;//Consente di schedulare un thread in modo da lanciarlo periodicamente
    private volatile boolean running = true;
    private volatile boolean pingStarted = false;
    private Thread virtualServerThread;

    //La signature del metodo lancia un' eccezione che sarà propagata all' interno della classe Client comune
    public SocketClient(String host, int port ,ClientModel model) throws IOException {
        this.model = model;
        this.socket = new Socket(host, port); //Client si collega al server
        this.input = new ObjectInputStream(socket.getInputStream());
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.timer = new Timer(true);

        runVirtualServer();
        //runExecutor();
    }

    //Gestione del ping delegata ad un esecutore che viene attivato periodicamente
    //5 s (da capire perché 5 secondi)
    private void startPing(){
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    sendCommand(new PingCommand());
                } catch (Exception e) {
                    System.out.println("[SOCKET_CLIENT] Invio ping fallito. Chiudo il client.");
                    stopPing();
                }
            }
        }, 0, 1500);
    }
    private void stopPing(){
        timer.cancel();
    }
    //Serve per leggere solo le risposte che invia il server e per applicare le modifiche sul model del client
    private void runVirtualServer(){
        virtualServerThread = new Thread(() -> {
            while (running) {
                try {
                    MessageToClient response = (MessageToClient) input.readObject();

                    if(response != null){
                        synchronized (model) {
                            model.update(response);
                            if(model.isGameEnded() || model.isGameCrashed()){
                                stopPing();
                                sendCommand(new CloseConnectionCommand());
                            }
                        }
                    }
                    //Serve perché voglio che il socket client invii verso il server una richiesta
                    //di disconnessione solo se il game è terminato


                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("[SOCKET_CLIENT] Connessione con il server interrotta.");
                    running = false;
                    try {
                        close();
                    } catch (Exception ex) {
                        System.err.println("[SOCKET_CLIENT] Errore durante la chiusura del socket.");
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("[SOCKET_CLIENT] Richiesta disconnessione non andata a buon fine");
                }
            }
        });

        virtualServerThread.setName("virtual-server-reader");
        virtualServerThread.start();
    }

    public void close() throws IOException {
//        try {
//            virtualServerThread.interrupt();
//        } catch (Exception ignored) {}
        try {
            timer.cancel();
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
        System.out.println("[SOCKET_CLIENT] socket closed.");
    }
    @Override
    public void createGame(String playerId, String totemColor, int numPlayers) throws Exception {
        this.playerId = playerId;
        sendCommand(new CreateGameCommand(playerId, totemColor, numPlayers));
        startPing();
    }

    @Override
    public void joinGame(String playerId, String totemColor) throws Exception {
        this.playerId = playerId;
        sendCommand(new JoinGameCommand(playerId, totemColor));
        startPing();
    }

    @Override
    public void placeTotem(int index) throws Exception {
        sendCommand(new PlaceTotemCommand(this.playerId, index));
    }

    @Override
    public void pickCard(String playerId, int cardId) throws Exception {
        sendCommand(new PickCardCommand(playerId, cardId));
    }

    @Override
    public void pickSpecial(String playerId, int cardId) throws Exception {
        sendCommand(new  PickSpecialCommand(playerId, cardId));
    }

    @Override
    public void quitGame(String playerId) throws Exception {
        sendCommand(new QuitGameCommand(playerId));
    }

    //Permette di inviare i comandi verso il server
    public void sendCommand(ServerCommand command) throws Exception {
        output.reset();
        output.writeObject(command);
        output.flush();
    }
}
