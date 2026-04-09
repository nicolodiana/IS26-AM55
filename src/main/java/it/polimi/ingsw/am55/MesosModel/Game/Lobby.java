package it.polimi.ingsw.am55.MesosModel.Game;

import it.polimi.ingsw.am55.MesosModel.Exceptions.PlayerNumberOutOfRange;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private List<Game> activeGames;
    private List<Player> players;


    public Lobby(){
        players=new ArrayList<Player>();
        activeGames = new ArrayList();
    }

    public List<String> getGamesIds(){
        List<String> gamesIds = new ArrayList();
        for(int i=0;i<activeGames.size();i++){
            gamesIds.add(activeGames.get(i).getIdGame());
        }
        return gamesIds;
    }
    public List<Game> getActiveGames(){return activeGames;}
    public void addPlayerCreator(String nickname, String totem, String summaryCardImage, int numPlayers) throws PlayerNumberOutOfRange {
        Player p = new Player(nickname,totem,summaryCardImage);
        Game g = p.createGame(numPlayers);
        activeGames.add(g);
        g.addPlayer(p);

    }
    public void addPlayerToGame(String nickname, String totem, String summaryCardImage, String idGame) throws PlayerNumberOutOfRange {
        Player p = new Player(nickname,totem,summaryCardImage);
        if(activeGames.isEmpty()){
            throw new IllegalArgumentException("There aren't any games availables");
        }else{
            for(int i=0;i<activeGames.size();i++){
                if(activeGames.get(i).getIdGame().equals(idGame)){
                    activeGames.get(i).addPlayer(p);
                }
            }
        }
    }
}
