package it.polimi.ingsw.mesos.provaeffetti_semplice;

import java.util.ArrayList;
import java.util.List;

// Game e il soggetto Observer: conosce i player e notifica le EventCard.
// Non sa nulla degli effetti specifici: delega tutto ad applyEffect.
// Gli eventi di stato (fine partita) sono gestiti da resolver separati,
// non da carte evento, perche non vengono mai pescati dalla Board.
public class Game {
    private final List<Player> players        = new ArrayList<>();
    private final EndGameResolver endGameResolver = new EndGameResolver();

    public void addPlayer(Player player) {
        players.add(player);
    }

    // La Board pesca una EventCard normale → Game la notifica su ogni player.
    // POTREI USARE LAMBDA: players.forEach(card::applyEffect)
    public void triggerEvent(EventCard card) {
        for (Player player : players) {
            card.applyEffect(player);
        }
    }

    // Rituale Sciamanico: richiede contesto multi-player (min/max stelle).
    // Game calcola min/max PRIMA di notificare ogni player.
    public void triggerRitual(RitualEventCard card) {
        // Calcolo min e max stelle tra tutti i player
        // POTREI USARE LAMBDA: players.stream().mapToInt(Player::getShamanStars)
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (Player p : players) {
            int stars = p.getShamanStars();
            if (stars > max) max = stars;
            if (stars < min) min = stars;
        }
        // Notifica ogni player con il contesto calcolato
        for (Player player : players) {
            card.applyEffect(player, max, min);
        }
    }

    // Fine partita: non e una carta, e una transizione di stato.
    public void endGame() {
        endGameResolver.resolve(players);
    }

    public void printScores() {
        System.out.println("\n=== PUNTEGGI FINALI ===");
        for (Player p : players) {
            System.out.println(p.getName() + ": PP=" + p.getPrestigePoints()
                + ", Cibo=" + p.getFood());
        }
    }
}
