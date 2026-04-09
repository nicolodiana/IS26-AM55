package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

//sciamani
public class Shaman extends CharacterCard {
    private int numStars;
    //final CharacterType type = CharacterType.SHAMAN;

    public Shaman(int id, int numStars, int era) {
        super(id, era);
        this.numStars = numStars;
        //this.era = era;
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
