package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.*;
public class Artist extends CharacterCard {
    @Override
    public void addToPlayer(Player player) {
        //player.getArtistsList().add(this);
        player.addTribeCard(this);
    }
    public int countSameTypeIn(Player player) {
        return player.getArtistsList().size(); // so che sono un Hunter, conto gli Hunter
    }
    public Artist(int era){
        this.era= era;
    }
}
