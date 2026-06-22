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

// Sustenance Event:
// - each character in the tribe costs 1 food
// - buildings do NOT count
// - each Collector discounts 3 food from the total
// - if the player fails to pay everything, he loses numPP
// for each character left unfed
public class SustenanceEventCard extends EventCard {
    private final Map<String, Integer> effectToFood = new HashMap<>();
    private final Map<String, Integer> effectToPP = new HashMap<>();
    // Number of PP lost for each unfed character
    private final int numPP;

    // Event constructor: initializes the PP penalty
    public SustenanceEventCard(int id, int era, int numPP) {
        super(id,era);
        this.numPP = numPP;
    }

    @Override
    public void activateEvent(List<Player> players) {
        // I scroll through all the players involved in the event
        for (Player p : players) {

            //1) It calculates how many characters the player has.
            // playerDeckSize() in your Player only counts the character cards
            // (shaman, hunter, artist, inventor, builder, collector),
            // so it's accurate for the base cost of sustenance.
            int totalCharacters = p.playerDeckSize();

            // 2) It calculates the total discount given by the Collector.
            // Every Collector's discount is 3 food.
            int collectorDiscount = p.countByType(CharacterType.COLLECTOR) * 3;

            // 3) Eventuale bonus/sconto dato dall'edificio 2.
            // Qui sto assumendo che BUILDING2 riduca il costo di 1 cibo.
            // Se la regola reale è diversa, questo valore va cambiato.
            //DA GESTIRE NUM TIPO  per sconto addizionale dato da edificio 2: il tipo su cui contare le occorrenze si dovrebbe leggere da lei
            int building2Discount = 0;
            //A differenza degli altri edifici , che per portare un effetto mi bastava semplicemente capire se ci fosse quella carta edificio
            //Gli edifici 2 possono ripetersi e portano uno sconto diverso in base al personaggio che indicano
            for (BuildingCard bc : p.getBuildings()) {
                building2Discount += bc.getSustenanceDiscount(p);
            }
            // 4) Calcolo il costo netto totale dell'evento.
            // È il costo base meno tutti gli sconti.
            // Uso Math.max(0, ...) perché il costo non può mai diventare negativo.
            int foodToPay = Math.max(0, totalCharacters - collectorDiscount - building2Discount);

            // 5) Recupero quanto cibo possiede attualmente il giocatore.
            int availableFood = p.getNumFoods();

            // 6) Il giocatore deve pagare tutto il possibile.
            // Se ha abbastanza cibo, paga tutto foodToPay.
            // Se non ne ha abbastanza, paga solo quello che possiede.
            int paidFood = Math.min(availableFood, foodToPay);

            // 7) Calcolo quanti "costi" restano scoperti.
            // Ogni unità non pagata corrisponde a un personaggio non sfamato.
            int unfedCharacters = foodToPay - paidFood;

            // 8) Tolgo al giocatore il cibo che ha effettivamente pagato.
            // Non tolgo foodToPay direttamente, perché il giocatore potrebbe
            // non avere abbastanza cibo.
            p.payFood(paidFood);
            effectToFood.put(p.getNickname(), -paidFood);
            // 9) Applico la penalità in punti prestigio.
            // Per ogni personaggio non sfamato il giocatore perde numPP.
            p.payPP(unfedCharacters * numPP);
            effectToPP.put(p.getNickname(), -unfedCharacters * numPP);
        }
    }

    public int getOrder(){
        return 3;
    }

    public SustenanceEventView toView() { return new SustenanceEventView(getId(), this.era, this.numPP); }
    public ResolveSustenanceView toViewResolve() {
        return new ResolveSustenanceView(effectToFood, effectToPP, "SUSTENANCE"); }
}
