package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.ClientCards.ShamanRitualEventView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveShamanRitualView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Evento Rituale Sciamanico
public class ShamanRitualEventCard extends EventCard {
    private final int maxPP;
    private final int minPP;
    private final Map<String, Integer> effectToPP = new HashMap<>();

    public ShamanRitualEventCard(int id, int era,int maxPP, int minPP) {
        super(id,era);
        this.maxPP = maxPP;
        this.minPP = minPP;
    }

    @Override
    public void activateEvent(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return;
        }

        // calculate the max and min starts one time
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

        // Apply the effect to all players
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

    public int getOrder(){
        return 2;
    }

    public ShamanRitualEventView toView() { return new ShamanRitualEventView(getId(), this.era, this.maxPP, this.minPP); }

    public ResolveShamanRitualView toViewResolve() {
        return new ResolveShamanRitualView(effectToPP, "SHAMAN RITUAL");
    }
}