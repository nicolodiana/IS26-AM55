package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Player.Player;

/**
 * Base strategy for food bonuses and maluses determined by turn order.
 * <p>Concrete implementations adapt the distribution rules to the number of players in the game.
 */
public abstract class TurnOrderEffect {
    /**
     * Applies the turn-order food reward for the specified player position.
     *
     * @param player the player affected by the operation
     * @param index the position to evaluate or access
     */
    public abstract void applyFood(Player player,int index);

    /**
     * Applies the food malus associated with the event effect.
     *
     * @param player the player affected by the operation
     */
    public void applyMalus(Player player){
        if (player.getNumFoods() == 0){
            player.payPP(2);
            return;
        }
        player.payFood(1);
    }

}
