package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.dto.ClientCards.ShamanCardView;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

//sciamani
public class Shaman extends CharacterCard {
    private final int numStars;

    public Shaman(int id, int numStars, int era) {
        super(id, era);
        this.numStars = numStars;
    }

    @Override
    public void addToPlayer(Player player) {
        player.addTribeCard(this);
    }

    public int getNumStars() {
        return this.numStars;
    }

    public ShamanCardView toView() { return new ShamanCardView(getId(), era, numStars); }
}