package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.ClientCards.ShamanRitualView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveShamanRitualView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveSustenanceView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Evento Rituale Sciamanico
public class ShamanRitualEventCard extends EventCard {
    private int maxPP;
    private int minPP;
    private Map<String, Integer> effectToPP = new HashMap<>();

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

        // Calcolo una sola volta il massimo e il minimo numero di stelle tra tutti i player
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

        // Applico gli effetti a tutti i player
        for (Player p : players) {
            int stars = p.countShamanStars();

            // Chi ha il massimo guadagna PP
            // Se ha BUILDING7 il guadagno raddoppia
            if (stars == maxStars) {
                int gain = p.hasBuilding(BuildingType.BUILDING7) ? maxPP * 2 : maxPP;
                p.addPP(gain);
                effectToPP.put(p.getNickname(), gain);
            }

            // Chi ha il minimo perde PP
            // Se ha BUILDING3 la perdita viene annullata
            if (stars == minStars) {
                if (!p.hasBuilding(BuildingType.BUILDING3)) {
                    p.payPP(minPP);
                    effectToPP.put(p.getNickname(), - minPP);
                }
            }
        }

        /// occhio sono test
        System.out.println("DOPO PUT PP: " + effectToPP);
    }
    public int getOrder(){
        return 2;
    }

    public ShamanRitualView toView() { return new ShamanRitualView(getId(), this.era, this.maxPP, this.minPP); }

    public ResolveShamanRitualView toViewResolve() {
        System.out.println("SONO TO VIEW RESOLVE DI SHAMAN RITUAL");
        return new ResolveShamanRitualView(effectToPP, "SHAMAN RITUAL"); }

}