package it.polimi.ingsw.am55.view.gui.assets;

import it.polimi.ingsw.am55.dto.CardView;

public final class CardFormatter {

    private CardFormatter() {
    }

    public static String shortLabel(CardView card) {
        if (card == null) {
            return "Carta";
        }
        return card.getClass().getSimpleName()
                .replace("CardView", "")
                .replace("View", "")
                + " #" + card.getId();
    }

    public static String tooltip(CardView card) {
        if (card == null) {
            return "Carta non disponibile";
        }
        return "Id: " + card.getId()
                + "\nEra: " + card.getEra()
                + "\nTipo view: " + card.getClass().getSimpleName()
                + "\n" + card;
    }

    public static boolean isEvent(CardView card) {
        if (card == null) {
            return false;
        }
        String className = card.getClass().getSimpleName().toLowerCase();
        return className.contains("event");

    }
}
