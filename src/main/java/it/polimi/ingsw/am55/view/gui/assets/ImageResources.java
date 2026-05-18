package it.polimi.ingsw.am55.view.gui.assets;

import javafx.scene.image.Image;

import java.net.URL;
import java.util.Locale;

/** Caricatore centralizzato per immagini in src/main/resources/it/polimi/ingsw/am55/images. */
public class ImageResources {

    public Image loadCard(int cardId) {
        String path = CardAssetResolver.resolveCardPath(cardId);
        return load(path);
    }

    public Image loadBiddingTicket(char trailPlacement) {
        char normalized = Character.toUpperCase(trailPlacement);
        return load("/it/polimi/ingsw/am55/images/bidding/biddingticket" + normalized + ".png");
    }

    public Image loadTotem(String totemColor) {
        String file = resolveTotemFilename(totemColor);
        return file == null ? null : load("/it/polimi/ingsw/am55/images/totems/" + file);
    }

    public Image loadTurnTicket(int numPlayers) {
        if (numPlayers < 2 || numPlayers > 5) {
            return null;
        }
        return load("/it/polimi/ingsw/am55/images/turn/turnticket" + numPlayers + ".png");
    }

    public Image load(String resourcePath) {
        URL url = ImageResources.class.getResource(resourcePath);
        if (url == null) {
            return null;
        }
        return new Image(url.toExternalForm());
    }

    private String resolveTotemFilename(String rawColor) {
        if (rawColor == null) {
            return null;
        }

        String color = rawColor.trim().toLowerCase(Locale.ROOT);
        return switch (color) {
            case "blue"-> "bluetotem.png";
            case "white"-> "whitetotem.png";
            case "yellow"-> "yellowtotem.png";
            case "orange"-> "orangetotem.png";
            case "pink"-> "purpletotem.png";

            default -> color + "totem.png";
        };
    }
}
