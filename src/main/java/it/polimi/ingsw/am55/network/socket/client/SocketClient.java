package it.polimi.ingsw.am55.network.socket.client;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.ClientCommands;
import it.polimi.ingsw.am55.network.command.*;

import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketClient implements ClientCommands {

    private final ClientModel model;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private final Socket socket;
    private String playerId;
    private BlockingQueue<MessageToClient> queue;
    private Timer timer; //Consente di schedulare un thread in modo da lanciarlo periodicamente

    //La signature del metodo lancia un' eccezione che sarà propagata all' interno della classe Client comune
    public SocketClient(String host, int port ,ClientModel model) throws IOException {
        this.model = model;
        this.socket = new Socket(host, port); //Client si collega al server
        this.input = new ObjectInputStream(socket.getInputStream());
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.queue = new LinkedBlockingQueue<>();
        this.timer = new Timer(true);

        runVirtualServer();
        runExecutor();
    }

    //Gestione del ping delegata ad un esecutore che viene attivato periodicamente
    //5 s (da capire perché 5 secondi)
    private void startPing(){
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    sendCommand(new PingCommand());
                } catch (Exception e) {
                    System.out.println("[ERROR] Invio del ping non riuscito");
                }
            }
        }, 0, 5000);
    }
    //Serve per leggere solo le risposte che invia il server
    private void runVirtualServer() throws IOException {
        Thread virtualServer = new Thread(() -> {
            MessageToClient response;
            while (true) {
                try {
                    response = (MessageToClient) input.readObject();
                    //response.update(this.model);
                    if (response != null) {
                        queue.add(response);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        );
        virtualServer.start();
    }

    //Consente di andare a modificare lo stato interno al model
    private void runExecutor() {
        Thread executor = new Thread(() -> {
            while (true) {
                try {
                    MessageToClient response = queue.take();

                    synchronized (model) {
                        model.update(response);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;

                } catch (Exception e) {
                    System.err.println("Errore durante runExecutor: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        executor.setName("socket-client-executor");
        executor.start();
    }

    @Override
    public void createGame(String playerId, String totemColor, int numPlayers) throws Exception {
        this.playerId = playerId;
        sendCommand(new CreateGameCommand(playerId, totemColor, numPlayers));
        //startPing();
    }

    @Override
    public void joinGame(String playerId, String totemColor) throws Exception {
        this.playerId = playerId;
        sendCommand(new JoinGameCommand(playerId, totemColor));
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
    public String getPlayerId() {
        return this.playerId;
    }

    //Permette di inviare i comandi verso il server
    public void sendCommand(ServerCommand command) throws Exception {
        output.reset();
        output.writeObject(command);
        output.flush();
    }
}
