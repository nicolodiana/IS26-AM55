package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Player.Player;

public abstract class TurnOrderEffect {
    public abstract void applyFood(Player player,int index);

    public void applyMalus(Player player){
        if (player.getNumFoods() == 0){
            player.payPP(2);
            return;
        }
        player.payFood(1);
    }

}
