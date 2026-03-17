package it.polimi.ingsw.mesos.provaeffetti_semplice;

// Evento Sostentamento.
// Ogni personaggio nella tribù consuma 1 cibo.
// Ogni raccoglitore riduce il costo di 3.
// Edificio 2: riduce il costo di altri totalPersonaggi (sconto aggiuntivo).
// Se non si ha abbastanza cibo, si paga tutto quello che si ha
// e i personaggi non sfamati costano ppLossPerUnfed PP ciascuno.
public class SustenanceEventCard extends EventCard {
    private final int ppLossPerUnfed;

    public SustenanceEventCard(String name, int ppLossPerUnfed) {
        super(name);
        this.ppLossPerUnfed = ppLossPerUnfed;
    }

    @Override
    public void applyEffect(Player player) {
        int total    = player.getTotalCharacters();
        int gatherers = player.getGatherers().size();

        // Sconto base: ogni raccoglitore riduce il costo di 3
        int discount = gatherers * 3;

        // Edificio 2: modificatore passivo, sconto aggiuntivo pari a totalPersonaggi
        if (player.hasBuilding(2)) {
            discount += total;
        }

        int needed = Math.max(0, total - discount);
        int paid   = Math.min(player.getFood(), needed);
        player.payFood(paid);

        int unfed = needed - paid;
        if (unfed > 0) {
            int ppLost = unfed * ppLossPerUnfed;
            player.losePP(ppLost);
            System.out.println(player.getName() + " SUSTENANCE: " + unfed
                + " personaggi non sfamati → -" + ppLost + " PP");
        } else {
            System.out.println(player.getName() + " SUSTENANCE: tutti sfamati, pagati " + paid + " cibo");
        }
    }
}
