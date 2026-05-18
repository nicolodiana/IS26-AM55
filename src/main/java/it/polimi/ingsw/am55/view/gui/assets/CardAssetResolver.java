package it.polimi.ingsw.am55.view.gui.assets;

public final class CardAssetResolver {

    private CardAssetResolver() {
    }

    public static String resolveCardPath(int cardId) {
        if (cardId <= 0) {
            return "/it/polimi/ingsw/am55/images/cards/card_back.png";
        }

        return String.format(
                "/it/polimi/ingsw/am55/images/cards/card_%03d.png",
                cardId
        );
    }
}