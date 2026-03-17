package it.polimi.ingsw.mesos.provaeffetti_semplice;

// Classe astratta base per tutti i personaggi.
// Non ha logica propria: serve solo come tipo comune nella gerarchia.
public abstract class CharacterCard extends Card {
    public CharacterCard(String name) {
        super(name);
    }
}
