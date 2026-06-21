package it.polimi.ingsw.am55.view.gui.assets;

/**
 * Resolves card image resources from card identifiers.
 */
public final class CardAssetResolver {

    private CardAssetResolver() {
    }

    /**
     * Returns the classpath path for a card image or the card back when the id is invalid.
     *
     * @param cardId card identifier
     * @return classpath resource path
     */
    public static String resolveCardPath(int cardId) {
        if (cardId <= 0) {
            return "/it/polimi/ingsw/am55/images/cards/card_back.png";
        }
        return String.format("/it/polimi/ingsw/am55/images/cards/card_%03d.png", cardId);
    }
}
