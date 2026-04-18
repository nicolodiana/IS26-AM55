package it.polimi.ingsw.am55.network.rmi.server;

import it.polimi.ingsw.am55.controller.MatchController;
import it.polimi.ingsw.am55.network.rmi.client.VirtualClientRmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerRmi extends UnicastRemoteObject implements VirtualServerRmi{
    private List<VirtualClientRmi> observers = new ArrayList<>();
    private ConcurrentHashMap<Integer, VirtualClientRmi> allClientsConnected = new ConcurrentHashMap<>(); // per ora non usata
    private Map<Integer, Map<Integer, VirtualClientRmi>> clientsPerMatch = new HashMap<>(); // serve per tenere i client associati ai match a cui mandare le info
    private Map<Integer, MatchController> controllerPerMatch = new HashMap<>(); // controller del match legato al proprio game


    public ServerRmi() throws RemoteException {
        super();
    }

    public void connectClient(int playerId, VirtualClientRmi observer) {
        //this.observers.add(observer);
        //Map<Integer, VirtualClientRmi> clients = clientsPerMatch.get(matchId);
        // put the player in the global map of player connected on this server
        allClientsConnected.put(playerId, observer);

    }

    // ----------------------METODI PER CREARE LA LOBBY----------------------

    public void createGame(int matchId, int playerId, int numPlayers, VirtualClientRmi client) throws RemoteException {
        // capire se dividere controller da model del client
        // Insert new Map<Integer, VirtualClientRmi> and then the player in the match just added
        String message = "Something has gone wrong";
        clientsPerMatch
                .computeIfAbsent(matchId, k -> new HashMap<>())
                .put(playerId, client);

        // if the match was insert in the map, pass the creation of the match to the game's controller
        if (clientsPerMatch.containsKey(matchId)) {
            MatchController matchController = new MatchController();
            matchController.createGame(playerId, numPlayers);
            controllerPerMatch.put(matchId, matchController);

            message = matchController.sendUpdateLobby(); // take the result of the call create game
        }
        updateLobbyState(message, client);
    }

    @Override
    public void joinGame(int matchId, int playerId, VirtualClientRmi vcr) throws RemoteException {
        String message;
        VirtualClientRmi tmpVCR = clientsPerMatch.get(matchId).get(playerId);
       if (clientsPerMatch.containsKey(matchId)) {
           controllerPerMatch.get(matchId).joinGame(playerId, tmpVCR.getNickname(), tmpVCR.getTotem());
           clientsPerMatch.get(matchId).put(playerId, vcr);
           message = controllerPerMatch.get(matchId).sendUpdateLobby();
        } else {
           throw new IllegalArgumentException(message = "This game does not exist");
        }

       updateLobbyState(message, vcr);
    }

    public void endMatchConnection(int matchId) {
        Map<Integer, VirtualClientRmi> clients = clientsPerMatch.get(matchId);

        synchronized (clients) {
            clientsPerMatch.remove(matchId);
        }
    }
    //----------------------METODI DI UPDATESTATE----------------------

    // pass the result of the call to the rmi client
    @Override
    public void updateLobbyState(String message, VirtualClientRmi vcr) throws RemoteException {
        vcr.updateStateLobby(message);
    }

    public static void main(String[] args) throws RemoteException {
        final String serverName = "TestServer";

        VirtualServerRmi server = new ServerRmi();

        Registry registry = LocateRegistry.createRegistry(1234);

        registry.rebind(serverName, server);

        System.out.println("Server bound");
    }

}
