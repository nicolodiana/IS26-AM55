package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.*;
//raccoglitori
public class Collector extends CharacterCard {
    final private int foodDiscount = 3;
    //final CharacterType type = CharacterType.COLLECTOR;

    @Override
    public void addToPlayer(Player player) {
        //player.getCollectorsList().add(this);
        player.addTribeCard(this);
    }
    public Collector(int id, int era) {

        super(id, era);
    //this.era= era;
    }
}
