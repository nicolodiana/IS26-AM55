package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.ClientCards.ShamanRitualEventView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveShamanRitualView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event card that resolves shaman ritual effects.
 * <p>It rewards or penalizes players according to their shaman stars and exposes the result through a resolution DTO.
 */
public class ShamanRitualEventCard extends EventCard {
    /**
     * Maximum prestige points awarded by this effect.
     */
    private final int maxPP;
    /**
     * Minimum prestige points awarded by this effect.
     */
    private final int minPP;
    /**
     * Mapping between affected players and the prestige points assigned by the effect.
     */
    private final Map<String, Integer> effectToPP = new HashMap<>();

    /**
     * Creates a shaman ritual event card with its card metadata and rule values.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     * @param maxPP the prestige-point value involved in the operation
     * @param minPP the prestige-point value involved in the operation
     */
    public ShamanRitualEventCard(int id, int era,int maxPP, int minPP) {
        super(id,era);
        this.maxPP = maxPP;
        this.minPP = minPP;
    }

    /**
     * Resolves this event card and applies its effects to the participating players.
     *
     * @param players the players participating in the operation
     */
    @Override
    public void activateEvent(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return;
        }

        int maxStars = players.get(0).countShamanStars();
        int minStars = players.get(0).countShamanStars();

        for (Player p : players) {
            int stars = p.countShamanStars();

            if (stars > maxStars) {
                maxStars = stars;
            }

            if (stars < minStars) {
                minStars = stars;
            }
        }

        for (Player p : players) {
            int stars = p.countShamanStars();

            /** the player with the max number of stars gain PP
             * if he has also building 7 the pp gained doubles
             */
            if (stars == maxStars) {
                int gain = p.hasBuilding(BuildingType.BUILDING7) ? maxPP * 2 : maxPP;
                p.addPP(gain);
                effectToPP.put(p.getNickname(), gain);
            }

            /** the player with the minimum number of stars lose PP
             * but if he has building 3 he loses 0 PP
             */
            if (stars == minStars) {
                if (!p.hasBuilding(BuildingType.BUILDING3)) {
                    p.payPP(minPP);
                    effectToPP.put(p.getNickname(), - minPP);
                }
            }
        }
    }

    /**
     * Returns the event-resolution order used to sort event cards.
     *
     * @return the order value
     */
    public int getOrder(){
        return 2;
    }

    /**
     * Builds the client-facing view representation of this shaman ritual event card.
     *
     * @return the client-facing view representation of this shaman ritual event card
     */
    public ShamanRitualEventView toView() { return new ShamanRitualEventView(getId(), this.era, this.maxPP, this.minPP); }

    /**
     * Builds the event-resolution view generated after resolving this event card.
     *
     * @return the client-facing view representation of this shaman ritual event card
     */
    public ResolveShamanRitualView toViewResolve() {
        return new ResolveShamanRitualView(effectToPP, "SHAMAN RITUAL");
    }
}