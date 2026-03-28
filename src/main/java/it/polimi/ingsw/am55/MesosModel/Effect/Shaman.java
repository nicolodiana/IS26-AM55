package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.*;
//sciamani
public class Shaman extends CharacterCard {
    private int numStars;

    public Shaman(int numStars,int era) {
        this.numStars = numStars;
        this.era = era;
    }

    @Override
    public void addToPlayer(Player player) {
        //player.getShamansList().add(this);
        player.addTribeCard(this);
    }
    public int countSameTypeIn(Player player) {
        return player.getShamansList().size(); // so che sono un Hunter, conto gli Hunter
    }

    public int getNumStars() {
        return this.numStars;
    }
}
