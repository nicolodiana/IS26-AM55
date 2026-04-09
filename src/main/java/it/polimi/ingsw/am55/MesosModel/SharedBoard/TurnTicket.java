package it.polimi.ingsw.am55.MesosModel.SharedBoard;
import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TurnTicket {
    private List<Player> turnOrder;
    //private int[] effectList;
    TurnOrderEffect effect; //By using static TurnOrderEffect we use polymorfism to apply the correct
    // effect to the player on Turn Ticket thanks to dynamic type allocated in the constructor
    public TurnTicket() {
        turnOrder = new ArrayList<Player>();
    }

    public void initTurnTicket(List<Player> players){
        turnOrder = new ArrayList<Player>(players);
        Collections.shuffle(turnOrder);
        //effectList = new int[players.size()];
        //setupEffect(players.size(), effectList);
        this.effect = switch (players.size()) {
            case 2 -> new TwoPlayersEffect();
            case 3 -> new ThreePlayersEffect();
            case 4 -> new FourPlayersEffect();
            case 5 -> new FivePlayersEffect();
            default -> throw new IllegalArgumentException("Numero giocatori non valido");
        };

    }

    /*private void setupEffect(int numPlayer, int[] effectList) {
        switch (numPlayer) {
            case 2:
                effectList[0] = 1;
                effectList[1] = -1;
                break;
            case 3:
                effectList[0] = 2;
                effectList[1] = 0;
                effectList[2] = -1;
                break;
            case 4:
                effectList[0] = 2;
                effectList[1] = 1;
                effectList[2] = 0;
                effectList[3] = -1;
                break;
            case 5:
                effectList[0] = 3;
                effectList[1] = 1;
                effectList[2] = 0;
                effectList[3] = 0;
                effectList[4] = -1;
                break;
        }
    }*/

    //getter
    public List<Player> getTurnOrder() {
        return turnOrder;
    }
    public int getTurnIndex(Player player) {
        return turnOrder.indexOf(player);
    }
    public Player getTurnPlayer(int index){
        return turnOrder.get(index);
    }
    /*public int getEffect(int index){
        return effectList[index];
    }*/

    //***Changes by using optional instead return null value
    public Optional<Player> getNextPlayerFirstPhase(Player player){
        if(player == null){throw new IllegalArgumentException("Player isn't valid");}
        int index = getTurnIndex(player);
        if (index < turnOrder.size()-1){
            return Optional.of(turnOrder.get(index+1));
        }
        else{
            return Optional.empty(); //return an empty optional if there aren't no other players
        }
    }

    //Past version
    /*public Player getNextIndexFirstPhase(int index){
        if (index < turnOrder.size()-1){
            return turnOrder.get(index+1);
        }
        else{
            return null; //return null if there's no more players
        }
    }*/


    //***Changes
    public void giveMalusOrBonus(Player player){
        int playerPosition = getTurnIndex(player);
        int lastIndex = turnOrder.size()-1;
        if(playerPosition==lastIndex){
            effect.applyMalus(player);
        }else{
            effect.applyFood(player,playerPosition);
        }
    }
    //Past version
   /*public void giveMalusOrBonus(Player player){
        int playerPosition = getTurnIndex(player);
        int num;
        num = getEffect(playerPosition);

        if (num == -1){
            if (player.getNumFoods() == 0){
                player.payPP(2);
                return;
            }
            player.payFood(1);
            return;
        }
        player.addFood(num);
    }*/

    public void removePlayerFromTurnTicket(){
        turnOrder.removeFirst();//To be checked again....
    }
    public void addPlayer(Player player){
        turnOrder.add(player);
    }

    //Unused method
    public Player getFirstPlayerFirstPhase(){
        return turnOrder.getFirst();
    }



}
