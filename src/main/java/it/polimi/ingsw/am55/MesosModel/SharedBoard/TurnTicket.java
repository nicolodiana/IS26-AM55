package it.polimi.ingsw.am55.MesosModel.SharedBoard;
import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.util.*;

public class TurnTicket {
    private List<Player> turnOrder;
    TurnOrderEffect effect; //By using static TurnOrderEffect we use polymorfism to apply the correct
    // effect to the player on Turn Ticket thanks to dynamic type allocated in the constructor
    private Random random;
    public TurnTicket() {
        turnOrder = new ArrayList<Player>();
    }

    public TurnTicket(Random random) {
        this.turnOrder = new ArrayList<Player>();
        this.random = random;
    }
    public void initTurnTicket(List<Player> players){
        turnOrder = new ArrayList<>(players);
        Collections.shuffle(turnOrder);
        this.effect = switch (players.size()) {
            case 2 -> new TwoPlayersEffect();
            case 3 -> new ThreePlayersEffect();
            case 4 -> new FourPlayersEffect();
            case 5 -> new FivePlayersEffect();
            default -> throw new IllegalArgumentException("Numero giocatori non valido");
        };

    }

    //getter
    public List<Player> getTurnOrder() {
        return turnOrder;
    }
    public Player getTurnPlayer(int index){
        return turnOrder.get(index);
    }

    public Optional<Player> getNextPlayerFirstPhase(Player player){
        if(player == null){throw new IllegalArgumentException("Player isn't valid");}
        int index = this.turnOrder.indexOf(player);;
        if (index < turnOrder.size()-1){
            return Optional.of(turnOrder.get(index+1));
        }
        else{
            return Optional.empty(); //return an empty optional if there aren't no other players
        }
    }
    public void giveMalusOrBonus(Player player){
        int playerPosition = this.turnOrder.indexOf(player);;
        int lastIndex = turnOrder.size()-1;
        if(playerPosition==lastIndex){
            effect.applyMalus(player);
        }else{
            effect.applyFood(player,playerPosition);
        }
    }

    /*public Player getFirstPlayerFirstPhase(){
        return turnOrder.getFirst();
    }*/

    public void addPlayer(Player player){
        for (int i = 0; i < turnOrder.size(); i++) {
            if(turnOrder.get(i) == null){
                turnOrder.set(i, player);
                return;
            }
        }
    }
    public void removePlayerFromTurnTicket(){
        for (int i = 0; i < turnOrder.size(); i++) {
            if(turnOrder.get(i) != null){
                turnOrder.set(i, null);
                return;
            }
        }
    }
}
