package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.ClientModel.ClientCard;
import it.polimi.ingsw.am55.MesosModel.Effect.Artist;
import it.polimi.ingsw.am55.dto.CardView;

public class ArtistCardView extends CardView {

    public ArtistCardView(int id, int era){
        super(id, era);
    }

    @Override
    public String toString() {
        return "Artist";
    }

}
