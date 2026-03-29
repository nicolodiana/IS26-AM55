package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.*;

import java.util.List;

//evento pitture rupestri

public class PaintingsEventCard extends EventCard {
    private int upperPP;
    private int lowerPP;
    private int upperNumberOfArtist;
    private int lowerNumberOfArtist;
    private int minArtist;


    public PaintingsEventCard(int id, int era, int upperPP, int lowerPP, int upperNumberOfArtist, int lowerNumberOfArtist, int minArtist) {
        super(id, era);
        this.upperPP = upperPP;
        this.lowerPP = lowerPP;
        this.upperNumberOfArtist = upperNumberOfArtist;
        this.lowerNumberOfArtist = lowerNumberOfArtist;
        this.minArtist = minArtist;


    }

    @Override
    public void activateEvent(List<Player> players) {
        for (Player p : players) {
            int counterArtist = p.sizeArtists();

            // l'evento si applica solo se il player ha almeno minArtist artisti
            if (counterArtist < minArtist) {
                continue;
            }

            if (p.hasBuilding(BuildingType.BUILDING10)) {
                p.addFood(counterArtist);
            }

            // nessun controllo: può andare sotto zero la riserva di punti prestigio
            if (counterArtist == upperNumberOfArtist) {
                p.payPP( upperPP);
            } else if (counterArtist == lowerNumberOfArtist) {
                p.addPP(counterArtist * lowerPP);
            }
        }
    }
}
