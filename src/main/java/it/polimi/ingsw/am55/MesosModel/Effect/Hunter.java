package it.polimi.ingsw.am55.MesosModel.Effect;
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
    public int countSameTypeIn(Player player) {
        return player.getHuntersList().size(); // so che sono un Hunter, conto gli Hunter
    }

    public Boolean getIcon() {
        return icon;
    }
}
