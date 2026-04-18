package it.polimi.ingsw.am55.controller;

import it.polimi.ingsw.am55.MesosModel.Exceptions.PlayerNumberOutOfRange;
import it.polimi.ingsw.am55.MesosModel.Game.Game;
import it.polimi.ingsw.am55.network.rmi.server.VirtualServerRmi;

public class MatchController {
    private Game model;
    private String message;

    public void createGame(int playerId, int numPlayers) {
        if (numPlayers > 1 && numPlayers <= 5) {
            try {
                model = new Game(numPlayers);
            } catch (PlayerNumberOutOfRange e) {
                throw new RuntimeException(e);
            }

            try {
                model.addPlayer("Mario", "red");
            } catch (PlayerNumberOutOfRange e) {
                throw new RuntimeException(e);
            }

            message = "Lobby is ready. Please wait for other players";
        }
        else throw new RuntimeException( message = "Number of players is not valid");
    }

    public void joinGame(int playerId, String nickname, String totem) {
        try {
            model.addPlayer(nickname, totem);
            this.message = "Player aggiunto";
        } catch (PlayerNumberOutOfRange e) {
            message = "The lobby is full";
            throw new RuntimeException(e);
        }
    }

    public String sendUpdateLobby() {
        return message;
    }

}
