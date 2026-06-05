package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.dto.ClientCards.HunterCardView;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
//cacciatori
public class Hunter extends CharacterCard {
    private final Boolean icon;

    public Hunter(int id, Boolean icon, int era) {
        super(id, era);
        this.icon = icon;
    }

    public void addToPlayer(Player player) {
        player.addTribeCard(this);
    }

    public Boolean getIcon() {
        return icon;
    }

    public HunterCardView toView() { return new HunterCardView(getId(), icon, era); }
}
