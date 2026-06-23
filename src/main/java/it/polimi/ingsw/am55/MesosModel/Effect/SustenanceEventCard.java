package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.ClientCards.SustenanceEventView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveSustenanceView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event card that resolves sustenance effects.
 * <p>It applies food-related costs and prestige-point consequences at the configured event order.
 */
public class SustenanceEventCard extends EventCard {
    /**
     * Mapping between affected players and the food assigned or removed by the effect.
     */
    private final Map<String, Integer> effectToFood = new HashMap<>();
    /**
     * Mapping between affected players and the prestige points assigned by the effect.
     */
    private final Map<String, Integer> effectToPP = new HashMap<>();
    /**
     * Number of prestige points granted or required by this element.
     */
    private final int numPP;

    /**
     * Creates a sustenance event card with its card metadata and rule values.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     * @param numPP the num pp value
     */
    public SustenanceEventCard(int id, int era, int numPP) {
        super(id,era);
        this.numPP = numPP;
    }

    /**
     * Resolves this event card and applies its effects to the participating players.
     *
     * @param players the players participating in the operation
     */
    @Override
    public void activateEvent(List<Player> players) {
        for (Player p : players) {


            int totalCharacters = p.playerDeckSize();


            int collectorDiscount = p.countByType(CharacterType.COLLECTOR) * 3;

            int building2Discount = 0;
            for (BuildingCard bc : p.getBuildings()) {
                building2Discount += bc.getSustenanceDiscount(p);
            }

            int foodToPay = Math.max(0, totalCharacters - collectorDiscount - building2Discount);

            int availableFood = p.getNumFoods();


            int paidFood = Math.min(availableFood, foodToPay);

            int unfedCharacters = foodToPay - paidFood;


            p.payFood(paidFood);
            effectToFood.put(p.getNickname(), -paidFood);

            p.payPP(unfedCharacters * numPP);
            effectToPP.put(p.getNickname(), -unfedCharacters * numPP);
        }
    }

    /**
     * Returns the event-resolution order used to sort event cards.
     *
     * @return the order value
     */
    public int getOrder(){
        return 3;
    }

    /**
     * Builds the client-facing view representation of this sustenance event card.
     *
     * @return the client-facing view representation of this sustenance event card
     */
    public SustenanceEventView toView() { return new SustenanceEventView(getId(), this.era, this.numPP); }
    /**
     * Builds the event-resolution view generated after resolving this event card.
     *
     * @return the client-facing view representation of this sustenance event card
     */
    public ResolveSustenanceView toViewResolve() {
        return new ResolveSustenanceView(effectToFood, effectToPP, "SUSTENANCE"); }
}
