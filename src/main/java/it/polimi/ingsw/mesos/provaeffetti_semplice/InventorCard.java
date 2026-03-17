package it.polimi.ingsw.mesos.provaeffetti_semplice;

// Inventore: ha un'icona invenzione (stringa).
// A fine partita: PP = numero inventori * numero icone DISTINTE possedute.
// L'edificio 5 aggiunge cibo per ogni coppia di inventori (gestito in Player).
public class InventorCard extends CharacterCard {
    private final String inventionIcon;

    public InventorCard(String name, String inventionIcon) {
        super(name);
        this.inventionIcon = inventionIcon;
    }

    public String getInventionIcon() {
        return inventionIcon;
    }
}
