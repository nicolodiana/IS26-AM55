package it.polimi.ingsw.mesos.provaeffetti_semplice;

// Classe astratta per tutte le carte evento pescate dalla Board.
// Ogni sottoclasse concreta implementa applyEffect(Player).
// Game non conosce la logica degli effetti: chiama solo applyEffect su ogni player.
public abstract class EventCard extends Card {
    public EventCard(String name) {
        super(name);
    }

    public abstract void applyEffect(Player player);
}
