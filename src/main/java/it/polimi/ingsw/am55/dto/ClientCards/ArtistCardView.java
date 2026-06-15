package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.dto.PlayerView;

public class ArtistCardView extends CardView {

    public ArtistCardView(int id, int era){
        super(id, era);
    }

    @Override
    public String toString() {
        return "Artist";
    }

    @Override
    public void addToPlayer(PlayerView player) {
        player.pickCard(this);
    }
}
