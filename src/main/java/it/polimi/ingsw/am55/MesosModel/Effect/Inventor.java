package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.ClientModel.ClientCards.ArtistCardView;
import it.polimi.ingsw.am55.ClientModel.ClientCards.InventorCardView;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

public class Inventor extends CharacterCard {
    private String iconInvention;
    //final CharacterType type = CharacterType.INVENTOR;

    public void addToPlayer(Player player) {
        //player.getInventorsList().add(this);
        player.addTribeCard(this);
    }

    public Inventor(String iconInvention, int id, int era) {
        super(id, era);
        this.iconInvention = iconInvention;
        //this.era= era;
    }

    public String getIconInvention() {
        return iconInvention;
    }

    public InventorCardView toView() { return new InventorCardView(iconInvention, era, getId()); }
}