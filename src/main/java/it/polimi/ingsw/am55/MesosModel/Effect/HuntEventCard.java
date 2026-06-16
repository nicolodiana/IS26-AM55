package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.ClientCards.HuntEventView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveHuntingView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveSustenanceView;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HuntEventCard extends EventCard {
    private final int numPP;
    private final Map<String, Integer> effectToFood = new HashMap<>();
    private final Map<String, Integer> effectToPP = new HashMap<>();

    public HuntEventCard(int id, int era, int numPP) {
        super(id, era);
        this.numPP = numPP;
    }

    /** Hunt Event:
     * During this event you gain the food on this card for all the hunters a player has
     * and can receive the bonus of building 8 if he also has that building
     */
    public void activateEvent(List<Player> players) {
        for (Player p : players) {
            int numHunters = p.countByType(CharacterType.HUNTER);
            int bonusPerBuilding8 = p.hasBuilding(BuildingType.BUILDING8) ? numHunters : 0;

            p.addPP(numHunters * numPP + bonusPerBuilding8);
            p.addFood(numHunters + bonusPerBuilding8);
            effectToPP.put(p.getNickname(), numHunters * numPP + bonusPerBuilding8);
            effectToFood.put(p.getNickname(), numHunters + bonusPerBuilding8);
        }
    }

    public int getOrder(){
        return 0;
    }

    public HuntEventView toView() { return new HuntEventView(getId(), this.era, this.numPP); }

    public ResolveHuntingView toViewResolve() {
        return new ResolveHuntingView(effectToFood, effectToPP, "HUNTING"); }

}

