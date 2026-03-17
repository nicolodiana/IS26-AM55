package it.polimi.ingsw.mesos.provaeffetti_semplice;

// Cacciatore: ha l'attributo hasIcon che determina l'effetto immediato.
// Se hasIcon=true, quando viene pescato aggiunge cibo pari al numero
// di cacciatori gia presenti in tribù (incluso se stesso).
public class HunterCard extends CharacterCard {
    private final boolean hasIcon;

    public HunterCard(String name, boolean hasIcon) {
        super(name);
        this.hasIcon = hasIcon;
    }

    public boolean hasIcon() {
        return hasIcon;
    }
}
