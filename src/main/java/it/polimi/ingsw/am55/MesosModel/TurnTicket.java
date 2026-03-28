package it.polimi.ingsw.am55.MesosModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TurnTicket {
    private List<Player> turnOrder;
    private int[] effectList;

    public TurnTicket() {
        turnOrder = new ArrayList<Player>();
    }

    public void initTurnTicket(List<Player> players){
        turnOrder = new ArrayList<Player>(players);
        Collections.shuffle(turnOrder);
        effectList = new int[players.size()];
        setupEffect(players.size(), effectList);
    }

    private void setupEffect(int numPlayer, int[] effectList) {
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
    }

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
    public int getEffect(int index){
        return effectList[index];
    }

    public Player getNextPlayerFirstPhase(Player player){
        int index = getTurnIndex(player);

        if (index < turnOrder.size()-1){
            return turnOrder.get(index+1);
        }
        else{
            return null; //return null if there's no more players
        }
    }
    public Player getNextIndexFirstPhase(int index){
        if (index < turnOrder.size()-1){
            return turnOrder.get(index+1);
        }
        else{
            return null; //return null if there's no more players
        }
    }

    public void giveMalusOrBonus(Player player, int index){
        int num;
        num = getEffect(index);

        if (num == -1){
            if (player.getNumFoods() == 0){
                player.payPP(2);
                return;
            }
            player.payFood(1);
            return;
        }
        player.addFood(num);
    }

    public void removePlayerFromTurnTicket(){
        turnOrder.removeFirst();
    }
    public void addPlayer(Player player){
        turnOrder.add(player);
    }




}
