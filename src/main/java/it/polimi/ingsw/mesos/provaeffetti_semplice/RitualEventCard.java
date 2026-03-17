package it.polimi.ingsw.mesos.provaeffetti_semplice;

// Evento Rituale Sciamanico.
// Il confronto min/max stelle tra player richiede contesto multi-player:
// per questo applyEffect(Player) standard non basta.
// Si usa applyEffect(Player, int maxStars, int minStars) chiamato da Game.triggerRitual().
// Edificio 6: aggiunge +3 stelle al totale del player durante questo evento.
// Edificio 7: raddoppia il guadagno PP se il player ha il massimo di stelle.
// Edificio 3: neutralizza la perdita PP se il player ha il minimo di stelle.
public class RitualEventCard extends EventCard {
    private final int ppGain;
    private final int ppLoss;

    public RitualEventCard(String name, int ppGain, int ppLoss) {
        super(name);
        this.ppGain = ppGain;
        this.ppLoss = ppLoss;
    }

    // Metodo principale usato da Game.triggerRitual() con contesto multi-player
    public void applyEffect(Player player, int maxStars, int minStars) {
        int stars = player.getShamanStars();

        // Edificio 6: modificatore passivo, aggiunge +3 stelle durante il rituale
        if (player.hasBuilding(6)) {
            stars += 3;
        }

        // Player con massimo di stelle guadagna PP (parita: tutti i max guadagnano)
        if (stars == maxStars) {
            int gain = ppGain;
            // Edificio 7: raddoppia il guadagno PP per chi ha il massimo
            if (player.hasBuilding(7)) {
                gain *= 2;
            }
            player.addPP(gain);
            System.out.println(player.getName() + " RITUAL MAX: " + stars
                + " stelle → +" + gain + " PP"
                + (player.hasBuilding(7) ? " (Edificio 7 raddoppia)" : ""));
        }

        // Player con minimo di stelle perde PP (a meno che abbia edificio 3)
        if (stars == minStars) {
            if (!player.hasBuilding(3)) {
                player.losePP(ppLoss);
                System.out.println(player.getName() + " RITUAL MIN: " + stars
                    + " stelle → -" + ppLoss + " PP");
            } else {
                System.out.println(player.getName() + " RITUAL MIN: edificio 3 neutralizza la perdita");
            }
        }
    }

    // Override obbligatorio: non usare direttamente per questo evento
    @Override
    public void applyEffect(Player player) {
        throw new UnsupportedOperationException(
            "Usare applyEffect(player, maxStars, minStars) per il Rituale Sciamanico");
    }
}
