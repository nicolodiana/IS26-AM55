package it.polimi.ingsw.mesos.provaeffetti_semplice;

// Costruttore: ha un bonus PP (stampato sulla carta) conteggiato a fine partita.
// Il numero di costruttori in tribù determina lo sconto sul costo cibo
// degli edifici, calcolato in Player.addCard(BuildingCard).
public class BuilderCard extends CharacterCard {
    private final int ppBonus;

    public BuilderCard(String name, int ppBonus) {
        super(name);
        this.ppBonus = ppBonus;
    }

    public int getPpBonus() {
        return ppBonus;
    }
}
