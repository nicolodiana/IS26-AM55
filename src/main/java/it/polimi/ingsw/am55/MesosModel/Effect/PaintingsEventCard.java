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

/**
 * Event card that resolves cave-painting effects.
 * <p>It compares players against the configured artist thresholds and reports the resulting changes to the client.
 */
public class PaintingsEventCard extends EventCard {
    /**
     * Prestige points awarded by the upper-row condition.
     */
    private final int upperPP;
    /**
     * Prestige points awarded by the lower-row condition.
     */
    private final int lowerPP;
    /**
     * Number of artist cards required for the upper-row reward.
     */
    private final int upperNumberOfArtist;
    /**
     * Number of artist cards required for the lower-row reward.
     */
    private final int lowerNumberOfArtist;

    /**
     * Mapping between affected players and the food assigned or removed by the effect.
     */
    private final Map<String, Integer> effectToFood = new HashMap<>();
    /**
     * Mapping between affected players and the prestige points assigned by the effect.
     */
    private final Map<String, Integer> effectToPP = new HashMap<>();


    /**
     * Creates a paintings event card with its card metadata and rule values.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     * @param upperPP the prestige-point value involved in the operation
     * @param lowerPP the prestige-point value involved in the operation
     * @param upperNumberOfArtist the upper number of artist value
     * @param lowerNumberOfArtist the lower number of artist value
     */
    public PaintingsEventCard(int id, int era, int upperPP, int lowerPP, int upperNumberOfArtist, int lowerNumberOfArtist) {
        super(id, era);
        this.upperPP = upperPP;
        this.lowerPP = lowerPP;
        this.upperNumberOfArtist = upperNumberOfArtist;
        this.lowerNumberOfArtist = lowerNumberOfArtist;
    }

    /**
     * Resolves this event card and applies its effects to the participating players.
     *
     * @param players the players participating in the operation
     */
    @Override
    public void activateEvent(List<Player> players) {
        effectToFood.clear();
        effectToPP.clear();

        for (Player p : players) {
            int counterArtist = p.countByType(CharacterType.ARTIST);

            int foodEffect = 0;
            int ppEffect = 0;

            if (p.hasBuilding(BuildingType.BUILDING10)) {
                foodEffect = counterArtist;
                p.addFood(foodEffect);
            }

            if (counterArtist <= upperNumberOfArtist) {
                ppEffect = -upperPP;
                p.payPP(upperPP);
            } else if (counterArtist >= lowerNumberOfArtist) {
                ppEffect = counterArtist * lowerPP;
                p.addPP(ppEffect);
            }

            effectToFood.put(p.getNickname(), foodEffect);
            effectToPP.put(p.getNickname(), ppEffect);
        }
    }
    /**
     * Returns the event-resolution order used to sort event cards.
     *
     * @return the order value
     */
    public int getOrder(){
        return 1;
    }
    /**
     * Builds the client-facing view representation of this paintings event card.
     *
     * @return the client-facing view representation of this paintings event card
     */
    public PaintingsEventView toView() { return new PaintingsEventView(getId(), this.era, this.upperPP, this.lowerPP,
            this.upperNumberOfArtist, this.lowerNumberOfArtist); }

    /**
     * Builds the event-resolution view generated after resolving this event card.
     *
     * @return the client-facing view representation of this paintings event card
     */
    public ResolvePaintingsView toViewResolve() {
        return new ResolvePaintingsView(effectToFood, effectToPP, "PAINTINGS"); }

}
