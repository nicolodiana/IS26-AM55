package it.polimi.ingsw.mesos.provaeffetti_semplice;

import java.util.List;

// Resolver di fine partita: NON e una carta pescata dalla Board.
// E una regola di stato del gioco, chiamata da Game.endGame().
// Calcola i PP finali per ogni player in base alla composizione della tribù.
public class EndGameResolver {

    public void resolve(List<Player> players) {
        for (Player player : players) {
            applyEndGameEffects(player);
        }
    }

    private void applyEndGameEffects(Player player) {

        // Inventori: PP = numero inventori * numero icone distinte possedute
        int inventorCount = player.getInventors().size();
        int distinctIcons = player.getDistinctInventorIcons();
        int inventorPP    = inventorCount * distinctIcons;
        player.addPP(inventorPP);
        System.out.println(player.getName() + " ENDGAME Inventori: "
            + inventorCount + " x " + distinctIcons + " icone = +" + inventorPP + " PP");

        // Artisti: 10 PP ogni 2 artisti in tribù
        int artistPP = (player.getArtists().size() / 2) * 10;
        player.addPP(artistPP);
        System.out.println(player.getName() + " ENDGAME Artisti: "
            + player.getArtists().size() + " artisti → +" + artistPP + " PP");

        // Costruttori: somma dei PP bonus stampati sulle carte costruttore
        int builderPP = player.getTotalBuilderPP();
        player.addPP(builderPP);
        System.out.println(player.getName() + " ENDGAME Costruttori: +" + builderPP + " PP");

        // Edificio 14: bonus fisso di 25 PP a fine partita
        if (player.hasBuilding(14)) {
            player.addPP(25);
            System.out.println(player.getName() + " ENDGAME Edificio14: +25 PP");
        }
    }
}
