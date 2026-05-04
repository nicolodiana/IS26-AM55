package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.ClientModel.ClientCards.ArtistCardView;
import it.polimi.ingsw.am55.ClientModel.ClientCards.HunterCardView;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
//cacciatori
public class Hunter extends CharacterCard {
    private Boolean icon;
    //final CharacterType type = CharacterType.COLLECTOR;

    public Hunter(int id, Boolean icon, int era) {
        super(id, era);
        this.icon = icon;
        //this.era= era;
    }

    public void addToPlayer(Player player) {
        //player.getHuntersList().add(this);
        player.addTribeCard(this);
    }

    public Boolean getIcon() {
        return icon;
    }

    public HunterCardView toView() { return new HunterCardView(getId(), icon, era); }
}
