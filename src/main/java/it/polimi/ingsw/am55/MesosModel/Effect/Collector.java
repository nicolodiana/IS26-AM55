package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.ClientCards.ArtistCardView;
import it.polimi.ingsw.am55.dto.ClientCards.CollectorCardView;

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
    public CollectorCardView toView() { return new CollectorCardView(getId(), getEra()); }
}