package it.polimi.ingsw.am55.Socket.Client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.am55.ClientModel.ClientModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SocketClient {
    private SocketServerHandler server;
    private ClientModel model;
    private BufferedReader input;

    public SocketClient(Socket socket, ClientModel model) throws IOException {
        this.server = new SocketServerHandler(new PrintWriter(socket.getOutputStream()));
        this.model = model;
    }

    private void run() {
        new Thread(() -> {
            try {
                runVirtualServer();//Da un lato devo ascoltare i comandi provenienti dal server
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        runCli();//Dall' altro devo ascoltare i comandi provenienti dal client
    }
    private JsonObject readFromJson() throws IOException {
        String jsonLine = input.readLine();
        if(jsonLine!=null){
            return new JsonParser().parse(jsonLine).getAsJsonObject();
        }
        return null;
    }
    // comunicazione dal server al client
    private void runVirtualServer() throws IOException {
        JsonObject command ;
        while ((command = readFromJson()) != null) {

        }
    }

    /*TODO Capire come fare in modo che se due o più client fanno richiesta dello stesso comando nulla vada in crash
    inoltre gestire anche il caso in cui contemporaneamente richiedono comandi diversi (coda di comandi)
    */
    public void runCli(){
        Scanner sc = new Scanner(System.in);
        System.out.println("---WELCOME IN MESOS---");
        System.out.println("Enter your choice: ");
        System.out.println("1. Create Game");
        System.out.println("2. Join Game");
        while(true){
            String command = sc.nextLine();
            if(command.equals("create game")){
                server.createGame(sc.nextLine(),sc.nextLine(),sc.nextInt());
            }
            if(command.equals("join game")){
                server.joinGame(sc.nextLine(),sc.nextLine());
            }
        }

    }
    public static void main(String args[]) throws IOException {
        String ip = "127.0.0.1";
        int port = 4019;

        Socket socketClient = new Socket(ip, port);

        ClientModel model = new ClientModel();

        new SocketClient(socketClient,model).run();
    }
}
