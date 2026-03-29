package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.*;
//inventori
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

    public int countSameTypeIn(Player player) {
        return player.getInventorsList().size(); // so che sono un Hunter, conto gli Hunter
    }
    public String getIconInvention() {
        return iconInvention;
    }
}
