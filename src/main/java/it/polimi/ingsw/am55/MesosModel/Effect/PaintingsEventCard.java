package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.util.List;

//evento pitture rupestri

public class PaintingsEventCard extends EventCard {
    private int upperPP;
    private int lowerPP;
    private int upperNumberOfArtist;
    private int lowerNumberOfArtist;


    public PaintingsEventCard(int id, int era, int upperPP, int lowerPP, int upperNumberOfArtist, int lowerNumberOfArtist) {
        super(id, era);
        this.upperPP = upperPP;
        this.lowerPP = lowerPP;
        this.upperNumberOfArtist = upperNumberOfArtist;
        this.lowerNumberOfArtist = lowerNumberOfArtist;
    }

    @Override
    public void activateEvent(List<Player> players) {
        for (Player p : players) {
            int counterArtist = p.countByType(CharacterType.ARTIST);


            if (p.hasBuilding(BuildingType.BUILDING10)) {
                p.addFood(counterArtist);
            }

            // nessun controllo: può andare sotto zero la riserva di punti prestigio
            if (counterArtist <= upperNumberOfArtist) {
                p.payPP(upperPP);
            } else if (counterArtist >= lowerNumberOfArtist) {
                p.addPP(counterArtist * lowerPP);
            }
        }
    }
}
