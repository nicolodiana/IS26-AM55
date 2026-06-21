package it.polimi.ingsw.am55.view.gui.assets;

import it.polimi.ingsw.am55.dto.CardView;

/**
 * Formats card DTOs for short labels and tooltips used by the GUI.
 */
public final class CardFormatter {

    private CardFormatter() {
    }

    /**
     * Returns a concise fallback label for a card.
     *
     * @param card card to format
     * @return short card label
     */
    public static String shortLabel(CardView card) {
        if (card == null) {
            return "Card";
        }
        return card.getClass().getSimpleName()
                .replace("CardView", "")
                .replace("View", "")
                + " #" + card.getId();
    }

    /**
     * Returns the tooltip text shown when the user hovers over a card.
     *
     * @param card card to describe
     * @return tooltip text
     */
    public static String tooltip(CardView card) {
        if (card == null) {
            return "Card unavailable";
        }
        return card + "\nId: " + card.getId() + "\nEra: " + card.getEra();
    }

    /**
     * Checks whether a card is an event card and therefore cannot be picked.
     *
     * @param card card to inspect
     * @return {@code true} when the card class name represents an event
     */
    public static boolean isEvent(CardView card) {
        if (card == null) {
            return false;
        }
        return card.getClass().getSimpleName().toLowerCase().contains("event");
    }
}
