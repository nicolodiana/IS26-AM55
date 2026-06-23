package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

/**
 * Turn-order effect implementation for two-player games.
 * <p>It applies the food distribution rules associated with each player position on the turn track.
 */
public class TwoPlayersEffect extends TurnOrderEffect {
    /**
     * Applies the turn-order food reward for the specified player position.
     *
     * @param player the player affected by the operation
     * @param index the position to evaluate or access
     */
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
