package it.polimi.ingsw.mesos.provaeffetti_semplice;

// Sciamano: ha un numero di stelle che determinano il risultato
// dell'evento Rituale Sciamanico (confronto min/max tra player).
public class ShamanCard extends CharacterCard {
    private final int stars;

    public ShamanCard(String name, int stars) {
        super(name);
        this.stars = stars;
    }

    public int getStars() {
        return stars;
    }
}
