package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.*;
//raccoglitori
public class Collector extends CharacterCard {
    final private int foodDiscount = 3;

    @Override
    public void addToPlayer(Player player) {
        //player.getCollectorsList().add(this);
        player.addTribeCard(this);
    }
    public Collector(int era) {
        this.era= era;
    }
}
