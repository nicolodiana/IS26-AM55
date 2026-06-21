package it.polimi.ingsw.am55.view.cli;

/**
 * ANSI color constants used by the command-line renderer.
 * <p>
 * The constants keep terminal styling out of the rendering logic and provide a
 * single mapping between Mesos totem colors and terminal colors.
 */
public final class ConsoleColor {

    public static final String RESET = "\033[0m";

    public static final String BLACK = "\033[0;30m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String WHITE = "\033[0;37m";
    public static final String ORANGE = "\033[38;5;208m";
    public static final String ORANGE_BOLD = "\033[1;38;5;208m";
    public static final String BLACK_BOLD = "\033[1;30m";
    public static final String RED_BOLD = "\033[1;31m";
    public static final String GREEN_BOLD = "\033[1;32m";
    public static final String YELLOW_BOLD = "\033[1;33m";
    public static final String BLUE_BOLD = "\033[1;34m";
    public static final String PURPLE_BOLD = "\033[1;35m";
    public static final String CYAN_BOLD = "\033[1;36m";
    public static final String WHITE_BOLD = "\033[1;37m";

    public static final String BLACK_BRIGHT = "\033[0;90m";
    public static final String RED_BRIGHT = "\033[0;91m";
    public static final String GREEN_BRIGHT = "\033[0;92m";
    public static final String YELLOW_BRIGHT = "\033[0;93m";
    public static final String BLUE_BRIGHT = "\033[0;94m";
    public static final String PURPLE_BRIGHT = "\033[0;95m";
    public static final String CYAN_BRIGHT = "\033[0;96m";
    public static final String WHITE_BRIGHT = "\033[0;97m";

    private ConsoleColor() {
    }

    /**
     * Returns the terminal color used for a totem color name.
     *
     * @param color totem color stored in the DTO
     * @return ANSI color code for the totem
     */
    public static String totemColor(String color) {
        if (color == null) {
            return WHITE_BRIGHT;
        }

        return switch (color.trim().toLowerCase()) {
            case "blue" -> BLUE_BOLD;
            case "orange" -> ORANGE_BOLD;
            case "purple" -> PURPLE_BOLD;
            case "yellow" -> YELLOW_BOLD;
            case "white" -> WHITE_BOLD;
            default -> WHITE_BOLD;
        };
    }
}
