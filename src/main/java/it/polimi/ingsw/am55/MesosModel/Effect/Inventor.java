package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.dto.ClientCards.InventorCardView;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

public class Inventor extends CharacterCard {
    private final String iconInvention;

    public void addToPlayer(Player player) {
        player.addTribeCard(this);
    }

    public Inventor(String iconInvention, int id, int era) {
        super(id, era);
        this.iconInvention = iconInvention;
    }

    public String getIconInvention() {
        return iconInvention;
    }

    public InventorCardView toView() { return new InventorCardView(iconInvention, getId(), era); }
}