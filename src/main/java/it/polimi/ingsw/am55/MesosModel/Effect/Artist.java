package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.dto.ClientCards.ArtistCardView;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

public class Artist extends CharacterCard {

    @Override
    public void addToPlayer(Player player) {
        player.addTribeCard(this);
    }

    public Artist(int id, int era){
        super(id, era);

    }

    public ArtistCardView toView() { return new ArtistCardView(getId(), era); }
}