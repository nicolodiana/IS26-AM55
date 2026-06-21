package it.polimi.ingsw.am55.view.gui.assets;

import javafx.scene.image.Image;

import java.net.URL;
import java.util.Locale;

/**
 * Centralized loader for JavaFX image resources.
 */
public class ImageResources {

    /**
     * Loads a card image by card id.
     *
     * @param cardId card identifier
     * @return image instance, or {@code null} when the resource is missing
     */
    public Image loadCard(int cardId) {
        return load(CardAssetResolver.resolveCardPath(cardId));
    }

    /**
     * Loads a bidding ticket image by trail placement letter.
     *
     * @param trailPlacement bidding trail placement letter
     * @return image instance, or {@code null} when the resource is missing
     */
    public Image loadBiddingTicket(char trailPlacement) {
        char normalized = Character.toUpperCase(trailPlacement);
        return load("/it/polimi/ingsw/am55/images/bidding/biddingticket" + normalized + ".png");
    }

    /**
     * Loads a totem image by color name.
     *
     * @param totemColor color name stored in the DTO
     * @return image instance, or {@code null} when the resource is missing
     */
    public Image loadTotem(String totemColor) {
        String file = resolveTotemFilename(totemColor);
        return file == null ? null : load("/it/polimi/ingsw/am55/images/totems/" + file);
    }

    /**
     * Loads a turn ticket image for a given number of players.
     *
     * @param numPlayers player count
     * @return image instance, or {@code null} when the resource is missing
     */
    public Image loadTurnTicket(int numPlayers) {
        if (numPlayers < 2 || numPlayers > 5) {
            return null;
        }
        return load("/it/polimi/ingsw/am55/images/turn/turnticket" + numPlayers + ".png");
    }

    /**
     * Loads any classpath image resource.
     *
     * @param resourcePath absolute classpath resource path
     * @return image instance, or {@code null} when the resource is missing
     */
    public Image load(String resourcePath) {
        URL url = ImageResources.class.getResource(resourcePath);
        if (url == null) {
            return null;
        }
        return new Image(url.toExternalForm());
    }

    /**
     * Converts a totem color to the matching image file name.
     */
    private String resolveTotemFilename(String rawColor) {
        if (rawColor == null) {
            return null;
        }

        String color = rawColor.trim().toLowerCase(Locale.ROOT);
        return switch (color) {
            case "blue" -> "bluetotem.png";
            case "white" -> "whitetotem.png";
            case "yellow" -> "yellowtotem.png";
            case "orange" -> "orangetotem.png";
            case "pink", "purple" -> "purpletotem.png";
            default -> color + "totem.png";
        };
    }
}
