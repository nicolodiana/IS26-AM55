package it.polimi.ingsw.mesos.provaeffetti_semplice;

// Evento Pitture Rupestri.
// Se artisti < minArtists: nessun effetto.
// Se artisti > artistsUpper: si perdono ppLoss PP.
// Se artisti < artistsLower: si guadagnano artisti * ppGain PP.
// Edificio 10: aggiunge 1 cibo per ogni artista posseduto.
public class PaintingsEventCard extends EventCard {
    private final int ppGain;
    private final int ppLoss;
    private final int minArtists;
    private final int artistsUpper;
    private final int artistsLower;

    public PaintingsEventCard(String name, int ppGain, int ppLoss,
                              int minArtists, int artistsUpper, int artistsLower) {
        super(name);
        this.ppGain       = ppGain;
        this.ppLoss       = ppLoss;
        this.minArtists   = minArtists;
        this.artistsUpper = artistsUpper;
        this.artistsLower = artistsLower;
    }

    @Override
    public void applyEffect(Player player) {
        // Va direttamente nella lista degli artisti del player
        int artists = player.getArtists().size();

        // Numero minimo di artisti non raggiunto: evento non si applica
        if (artists < minArtists) return;

        // Edificio 10: modificatore passivo, 1 cibo per ogni artista
        if (player.hasBuilding(10)) {
            player.addFood(artists);
            System.out.println(player.getName() + " PAINTINGS Edificio10: +" + artists + " cibo");
        }

        // Troppi artisti: penalità PP
        if (artists > artistsUpper) {
            player.losePP(ppLoss);
            System.out.println(player.getName() + " PAINTINGS: " + artists
                + " artisti (>" + artistsUpper + ") → -" + ppLoss + " PP");
        }
        // Pochi artisti: bonus PP proporzionale
        else if (artists < artistsLower) {
            int ppAdded = artists * ppGain;
            player.addPP(ppAdded);
            System.out.println(player.getName() + " PAINTINGS: " + artists
                + " artisti (<" + artistsLower + ") → +" + ppAdded + " PP");
        }
    }
}
