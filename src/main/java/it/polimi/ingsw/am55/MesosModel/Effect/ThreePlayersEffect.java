package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

public class ThreePlayersEffect extends TurnOrderEffect {
    @Override
    public void applyFood(Player player, int index) {
        if(index==0){
            if(player.hasBuilding(BuildingType.BUILDING4)){
                player.addFood(3);
            }else{
                player.addFood(2);
            }
        }
    }
}
