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
            case "blue", "blu" -> "bluetotem.png";
            case "white", "bianco" -> "whitetotem.png";
            case "yellow", "giallo" -> "yellowtotem.png";
            case "orange", "arancione" -> "orangetotem.png";
            case "pink", "rosa" -> "pinktotem.png";

            // Il model attuale accetta red/black, ma le risorse ricevute sono pink/orange.
            // Questi fallback evitano immagini mancanti senza cambiare la logica server.
            case "red", "rosso" -> "pinktotem.png";
            case "black", "nero" -> "orangetotem.png";
            default -> color + "totem.png";
        };
    }
}
