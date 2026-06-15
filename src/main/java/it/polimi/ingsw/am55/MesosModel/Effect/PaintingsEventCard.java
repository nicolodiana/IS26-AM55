package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.ClientCards.PaintingsEventView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolvePaintingsView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveSustenanceView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//evento pitture rupestri

public class PaintingsEventCard extends EventCard {
    private final int upperPP;
    private final int lowerPP;
    private final int upperNumberOfArtist;
    private final int lowerNumberOfArtist;
    private final Map<String, Integer> effectToFood = new HashMap<>();
    private final Map<String, Integer> effectToPP = new HashMap<>();


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
                effectToFood.put(p.getNickname(), counterArtist);
            }

            // nessun controllo: può andare sotto zero la riserva di punti prestigio
            if (counterArtist <= upperNumberOfArtist) {
                p.payPP(upperPP);
                effectToPP.put(p.getNickname(), - upperPP);
            } else if (counterArtist >= lowerNumberOfArtist) {
                p.addPP(counterArtist * lowerPP);
                effectToPP.put(p.getNickname(), counterArtist * lowerPP);
            }
        }
    }

    public int getOrder(){
        return 1;
    }

    public PaintingsEventView toView() { return new PaintingsEventView(getId(), this.era, this.upperPP, this.lowerPP,
            this.upperNumberOfArtist, this.lowerNumberOfArtist); }

    public ResolvePaintingsView toViewResolve() {
        return new ResolvePaintingsView(effectToFood, effectToPP, "PAINTINGS"); }

}
