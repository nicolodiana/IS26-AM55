package it.polimi.ingsw.mesos.provaeffetti_semplice;

// Evento Caccia.
// Ogni cacciatore produce 1 cibo + ppPerHunter PP.
// Edificio 8: aggiunge +1 cibo e +1 PP per ogni cacciatore.
public class HuntEventCard extends EventCard {
    private final int ppPerHunter;

    public HuntEventCard(String name, int ppPerHunter) {
        super(name);
        this.ppPerHunter = ppPerHunter;
    }

    @Override
    public void applyEffect(Player player) {
        int hunters = player.getHunters().size();
        if (hunters == 0) return;

        // Edificio 8: modificatore passivo, aggiunge +1 cibo e +1 PP per cacciatore
        int bonus = player.hasBuilding(8) ? 1 : 0;

        int foodAdded = hunters * (1 + bonus);
        int ppAdded   = hunters * (ppPerHunter + bonus);

        player.addFood(foodAdded);
        player.addPP(ppAdded);

        System.out.println(player.getName() + " HUNT: " + hunters
            + " cacciatori → +" + foodAdded + " cibo, +" + ppAdded + " PP"
            + (bonus == 1 ? " (Edificio 8 attivo)" : ""));
    }
}
