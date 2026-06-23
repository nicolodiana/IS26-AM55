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
/**
 * Event card that resolves hunting effects.
 * <p>It evaluates players during event resolution and produces a client DTO describing food and prestige-point changes.
 */
public class HuntEventCard extends EventCard {
    /**
     * Number of prestige points granted or required by this element.
     */
    private final int numPP;
    /**
     * Mapping between affected players and the food assigned or removed by the effect.
     */
    private final Map<String, Integer> effectToFood = new HashMap<>();
    /**
     * Mapping between affected players and the prestige points assigned by the effect.
     */
    private final Map<String, Integer> effectToPP = new HashMap<>();
    /**
     * Creates a hunt event card with its card metadata and rule values.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     * @param numPP the num pp value
     */
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

    /**
     * Returns the event-resolution order used to sort event cards.
     *
     * @return the order value
     */
    public int getOrder(){
        return 0;
    }

    /**
     * Builds the client-facing view representation of this hunt event card.
     *
     * @return the client-facing view representation of this hunt event card
     */
    public HuntEventView toView() { return new HuntEventView(getId(), this.era, this.numPP); }

    /**
     * Builds the event-resolution view generated after resolving this event card.
     *
     * @return the client-facing view representation of this hunt event card
     */
    public ResolveHuntingView toViewResolve() {
        return new ResolveHuntingView(effectToFood, effectToPP, "HUNTING"); }

}

