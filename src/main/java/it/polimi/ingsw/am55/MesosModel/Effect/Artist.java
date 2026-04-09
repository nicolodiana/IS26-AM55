package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

public class Artist extends CharacterCard {
    //final CharacterType type = CharacterType.ARTIST;

    @Override
    public void addToPlayer(Player player) {
        //player.getArtistsList().add(this);
        player.addTribeCard(this);
    }
    public int countSameTypeIn(Player player) {
        return player.getArtistsList().size(); // so che sono un Hunter, conto gli Hunter
    }
    public Artist(int id, int era){
        super(id, era);
        //this.era= era;
    }
}
