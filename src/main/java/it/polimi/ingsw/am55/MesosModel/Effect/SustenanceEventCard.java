package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.util.List;

// Evento Sostentamento:
// - ogni personaggio della tribù costa 1 cibo
// - gli edifici NON contano
// - ogni Collector sconta 3 cibi sul totale
// - se il giocatore non riesce a pagare tutto, perde numPP
//   per ogni personaggio rimasto non sfamato
public class SustenanceEventCard extends EventCard {

    // Numero di punti prestigio persi per ogni personaggio non sfamato
    private int numPP;

    // Costruttore dell'evento: inizializza la penalità in PP
    public SustenanceEventCard(int id, int era, int numPP) {
        super(id,era);
        this.numPP = numPP;

    }

    @Override
    public void activateEvent(List<Player> players) {

        // Scorro tutti i giocatori coinvolti nell'evento
        for (Player p : players) {

            // 1) Calcolo quanti personaggi ha il giocatore.
            // playerDeckSize() nel tuo Player conta solo le carte personaggio
            // (shaman, hunter, artist, inventor, builder, collector),
            // quindi è giusto per il costo base del sostentamento.
            int totalCharacters = p.playerDeckSize();

            // 2) Calcolo lo sconto totale dato dai Collector.
            // Ogni Collector sconta 3 cibi.
            int collectorDiscount = p.sizeCollectors() * 3;

            // 3) Eventuale bonus/sconto dato dall'edificio 2.
            // Qui sto assumendo che BUILDING2 riduca il costo di 1 cibo.
            // Se la regola reale è diversa, questo valore va cambiato.
            //DA GESTIRE NUM TIPO  per sconto addizionale dato da edificio 2: il tipo su cui contare le occorrenze si dovrebbe leggere da lei
            int building2Discount = 0;

            if (p.hasBuilding(BuildingType.BUILDING2)) {
                for (BuildingCard bc : p.getBuildings()) {
                    if (bc.getType().equals(BuildingType.BUILDING2)) {
                        //building2Discount = bc.getCharacterForED().countSameTypeIn(p);
                        building2Discount = bc.bonusCharType(p);
                        break;
                    }
                }
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

            // 9) Applico la penalità in punti prestigio.
            // Per ogni personaggio non sfamato il giocatore perde numPP.
            p.payPP(unfedCharacters * numPP);
        }
    }
}
