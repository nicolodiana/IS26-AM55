package it.polimi.ingsw.am55.view.gui.assets;

/**
 * Risolve l'immagine di una carta partendo dal suo id di model.
 *
 * Mapping usato:
 * - carte Tribe/Event: id 1..96 -> pagina PDF 1..96
 * - building: id 100..120 -> pagina PDF 97..117
 */
public final class CardAssetResolver {

    private CardAssetResolver() {
    }

    public static String resolveCardPath(int cardId) {
        int pageNumber = resolvePdfPageNumber(cardId);
        if (pageNumber <= 0) {
            return "/it/polimi/ingsw/am55/images/cards/card_back.png";
        }
        return String.format("/it/polimi/ingsw/am55/images/cards/card_%03d.png", pageNumber);
    }

    public static int resolvePdfPageNumber(int cardId) {
        if (cardId >= 1 && cardId <= 96) {
            return cardId;
        }

        if (cardId >= 100 && cardId <= 120) {
            return 97 + (cardId - 100);
        }

        // Carta debug o id non standard: usa retro/fallback.
        return -1;
    }
}
