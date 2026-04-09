package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

public class TwoPlayersEffect extends TurnOrderEffect {
    @Override
    public void applyFood(Player player,int index) {
        if(index==0){
            if(player.hasBuilding(BuildingType.BUILDING4)){
                player.addFood(2);
            }else{
                player.addFood(1);
            }
        }
    }
}
