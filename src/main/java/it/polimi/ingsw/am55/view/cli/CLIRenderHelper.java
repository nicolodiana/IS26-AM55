package it.polimi.ingsw.am55.view.cli;

import it.polimi.ingsw.am55.dto.BiddingTicketView;
import it.polimi.ingsw.am55.dto.BoardView;
import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.dto.PlayerView;
import it.polimi.ingsw.am55.dto.ClientCards.*;

import java.security.cert.CertPathValidatorException;
import java.util.ArrayList;
import java.util.List;

public class CLIRenderHelper {

    private static final int CARD_WIDTH = 36;
    private static final int CARDS_PER_LINE = 2;

    private static final int TICKET_WIDTH = 32;
    private static final int TICKETS_PER_LINE = 5;

    private String boardData;

    public CLIRenderHelper() {
        this.boardData = null;
    }

    public void printBoard(BoardView boardView) {
        cliBoard(boardView);
        System.out.println(boardData);
    }
    public void printPersonalDeck(List<CardView> cards) {
        System.out.println();

        if (cards == null || cards.isEmpty()) {
            System.out.println(ConsoleColor.YELLOW_BOLD
                    + "La tua mano è vuota."
                    + ConsoleColor.RESET);
            return;
        }

        System.out.println(ConsoleColor.GREEN_BOLD
                + "========== MY HAND =========="
                + ConsoleColor.RESET);

        System.out.println(renderCardRow(
                "PERSONAL DECK",
                cards,
                ConsoleColor.GREEN_BOLD
        ));

        System.out.println(ConsoleColor.GREEN_BOLD
                + "============================="
                + ConsoleColor.RESET);
    }

    private void cliBoard(BoardView boardView) {
        if (boardView == null) {
            boardData = ConsoleColor.RED_BOLD + "Board non disponibile." + ConsoleColor.RESET;
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append(ConsoleColor.CYAN_BOLD);
        sb.append("╔════════════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                                  BOARD                                     ║\n");
        sb.append("╚════════════════════════════════════════════════════════════════════════════╝\n");
        sb.append(ConsoleColor.RESET);

        sb.append(renderTurnTicket(boardView.getTurnTicket()));
        sb.append("\n");

        sb.append(renderCardRow("UPPER ROW", boardView.getUpperRow(), ConsoleColor.BLUE_BOLD));
        sb.append("\n");

        sb.append(renderBiddingTrail(boardView.getBiddingTrail()));
        sb.append("\n");

        sb.append(renderCardRow("LOWER ROW", boardView.getLowerRow(), ConsoleColor.GREEN_BOLD));
        sb.append("\n");

        boardData = sb.toString();
    }

    private String renderTurnTicket(List<PlayerView> turnTicket) {
        StringBuilder sb = new StringBuilder();

        sb.append(ConsoleColor.YELLOW_BOLD);
        sb.append("╔════════════════════════════ TURN TICKET ═════════════════════════════════╗\n");
        sb.append(ConsoleColor.RESET);

        if (turnTicket == null || turnTicket.isEmpty()) {
            sb.append("║ ").append(padRight("[ empty ]", 70)).append(" ║\n");
        } else {
            sb.append("║ ");

            for (int i = 0; i < turnTicket.size(); i++) {
                PlayerView player = turnTicket.get(i);

                if (player == null) {
                    sb.append(ConsoleColor.BLACK_BRIGHT)
                            .append("[")
                            .append(i)
                            .append(": empty]")
                            .append(ConsoleColor.RESET);
                } else {
                    sb.append("[")
                            .append(i)
                            .append(": ")
                            .append(ConsoleColor.totemColor(player.getTotemColor()))
                            .append(player.getNickname())
                            .append(" / ")
                            .append(player.getTotemColor())
                            .append(ConsoleColor.RESET)
                            .append("]");
                }

                if (i < turnTicket.size() - 1) {
                    sb.append("  ");
                }
            }

            sb.append("\n");
        }

        sb.append(ConsoleColor.YELLOW_BOLD);
        sb.append("╚═══════════════════════════════════════════════════════════════════════════╝\n");
        sb.append(ConsoleColor.RESET);

        return sb.toString();
    }

    private String renderCardRow(String title, List<CardView> cards, String color) {
        StringBuilder sb = new StringBuilder();

        sb.append(color);
        sb.append("╔════════════════════════════ ")
                .append(title)
                .append(" ═════════════════════════════╗\n");
        sb.append(ConsoleColor.RESET);

        if (cards == null || cards.isEmpty()) {
            sb.append("  ")
                    .append(ConsoleColor.BLACK_BRIGHT)
                    .append("[ empty row ]")
                    .append(ConsoleColor.RESET)
                    .append("\n");

            sb.append(color);
            sb.append("╚═══════════════════════════════════════════════════════════════════════════╝\n");
            sb.append(ConsoleColor.RESET);

            return sb.toString();
        }

        List<String[]> boxes = new ArrayList<>();

        for (CardView card : cards) {
            boxes.add(renderCardBox(card));
        }

        for (int i = 0; i < boxes.size(); i += CARDS_PER_LINE) {
            int end = Math.min(i + CARDS_PER_LINE, boxes.size());
            List<String[]> group = boxes.subList(i, end);

            int maxLines = maxLines(group);

            for (int line = 0; line < maxLines; line++) {
                for (String[] box : group) {
                    if (line < box.length) {
                        sb.append(box[line]);
                    } else {
                        sb.append(repeat(" ", CARD_WIDTH + 2));
                    }

                    sb.append("  ");
                }

                sb.append("\n");
            }

            sb.append("\n");
        }

        sb.append(color);
        sb.append("╚═══════════════════════════════════════════════════════════════════════════╝\n");
        sb.append(ConsoleColor.RESET);

        return sb.toString();
    }

    private String[] renderCardBox(CardView card) {
        List<String> lines = new ArrayList<>();

        String category = getCardCategory(card);
        String header = category + " #" + card.getId();
        String borderColor = getCardColor(card);

        lines.add(borderColor + "┌" + repeat("─", CARD_WIDTH) + "┐" + ConsoleColor.RESET);
        lines.add(borderColor + "│" + ConsoleColor.RESET + center(header, CARD_WIDTH) + borderColor + "│" + ConsoleColor.RESET);
        lines.add(borderColor + "├" + repeat("─", CARD_WIDTH) + "┤" + ConsoleColor.RESET);
        lines.add(borderColor + "│" + ConsoleColor.RESET + " " + padRight("Era: " + card.getEra(), CARD_WIDTH - 1) + borderColor + "│" + ConsoleColor.RESET);

        addCardDetails(lines, card, borderColor);

        lines.add(borderColor + "└" + repeat("─", CARD_WIDTH) + "┘" + ConsoleColor.RESET);

        return lines.toArray(new String[0]);
    }

    private void addCardDetails(List<String> lines, CardView card, String borderColor) {
        if (card instanceof ArtistCardView) {
            appendDetail(lines, borderColor, "Type", "Artist");
            appendDetail(lines, borderColor, "Effect", "Artist card");
            return;
        }

        if (card instanceof BuilderCardView builder) {
            appendDetail(lines, borderColor, "Type", "Builder");
            appendDetail(lines, borderColor, "PP", String.valueOf(builder.getNumPP()));
            appendDetail(lines, borderColor, "Discount", String.valueOf(builder.getPickbuildingdiscount()));
            return;
        }

        if (card instanceof CollectorCardView) {
            appendDetail(lines, borderColor, "Type", "Collector");
            appendDetail(lines, borderColor, "Effect", "Food discount");
            return;
        }

        if (card instanceof HunterCardView hunter) {
            appendDetail(lines, borderColor, "Type", "Hunter");
            appendDetail(lines, borderColor, "Icon", String.valueOf(hunter.getIcon()));
            return;
        }

        if (card instanceof InventorCardView inventor) {
            appendDetail(lines, borderColor, "Type", "Inventor");
            appendDetail(lines, borderColor, "Icon", inventor.getIconInvention());
            return;
        }

        if (card instanceof ShamanCardView shaman) {
            appendDetail(lines, borderColor, "Type", "Shaman");
            appendDetail(lines, borderColor, "Stars", String.valueOf(shaman.getNumStars()));
            return;
        }

        if (card instanceof BuildingCardView building) {
            appendDetail(lines, borderColor, "Type", "Building");
            appendDetail(lines, borderColor, "Info", building.toString());
            return;
        }

        if (card instanceof HuntEventView hunt) {
            appendDetail(lines, borderColor, "Type", "Hunt Event");
            appendDetail(lines, borderColor, "Info", hunt.toString());
            return;
        }

        if (card instanceof PaintingsEventView paintings) {
            appendDetail(lines, borderColor, "Type", "Paintings Event");
            appendDetail(lines, borderColor, "Info", paintings.toString());
            return;
        }

        if (card instanceof ShamanRitualEventView ritual) {
            appendDetail(lines, borderColor, "Type", "Shaman Ritual");
            appendDetail(lines, borderColor, "Info", ritual.toString());
            return;
        }

        if (card instanceof SustenanceEventView sustenance) {
            appendDetail(lines, borderColor, "Type", "Sustenance Event");
            appendDetail(lines, borderColor, "Info", sustenance.toString());
            return;
        }

        appendDetail(lines, borderColor, "Type", "Unknown");
        appendDetail(lines, borderColor, "Info", card.toString());
    }

    private String renderBiddingTrail(List<BiddingTicketView> biddingTrail) {
        StringBuilder sb = new StringBuilder();

        sb.append(ConsoleColor.PURPLE_BOLD);
        sb.append("╔════════════════════════════ BIDDING TRAIL ═══════════════════════════════╗\n");
        sb.append(ConsoleColor.RESET);

        if (biddingTrail == null || biddingTrail.isEmpty()) {
            sb.append("  ")
                    .append(ConsoleColor.BLACK_BRIGHT)
                    .append("[ empty bidding trail ]")
                    .append(ConsoleColor.RESET)
                    .append("\n");

            sb.append(ConsoleColor.PURPLE_BOLD);
            sb.append("╚═══════════════════════════════════════════════════════════════════════════╝\n");
            sb.append(ConsoleColor.RESET);

            return sb.toString();
        }

        List<String[]> ticketBoxes = new ArrayList<>();

        for (BiddingTicketView ticket : biddingTrail) {
            ticketBoxes.add(renderBiddingTicketBox(ticket));
        }

        for (int i = 0; i < ticketBoxes.size(); i += TICKETS_PER_LINE) {
            int end = Math.min(i + TICKETS_PER_LINE, ticketBoxes.size());
            List<String[]> group = ticketBoxes.subList(i, end);

            int maxLines = maxLines(group);

            for (int line = 0; line < maxLines; line++) {
                sb.append("  ");

                for (String[] ticketBox : group) {
                    if (line < ticketBox.length) {
                        sb.append(ticketBox[line]);
                    } else {
                        sb.append(repeat(" ", TICKET_WIDTH + 2));
                    }

                    sb.append("  ");
                }

                sb.append("\n");
            }

            sb.append("\n");
        }

        sb.append(ConsoleColor.PURPLE_BOLD);
        sb.append("╚═══════════════════════════════════════════════════════════════════════════╝\n");
        sb.append(ConsoleColor.RESET);

        return sb.toString();
    }

    private String[] renderBiddingTicketBox(BiddingTicketView ticket) {
        List<String> lines = new ArrayList<>();

        String borderColor = ConsoleColor.PURPLE_BRIGHT;

        String title = "Ticket " + ticket.getTrailPlacement();
        String effect = renderTicketEffectPlain(ticket);
        String playerBox = renderTicketPlayerBoxPlain(ticket);

        lines.add(borderColor + "┌" + repeat("─", TICKET_WIDTH) + "┐" + ConsoleColor.RESET);
        lines.add(borderColor + "│" + ConsoleColor.RESET + center(title, TICKET_WIDTH) + borderColor + "│" + ConsoleColor.RESET);
        lines.add(borderColor + "├" + repeat("─", TICKET_WIDTH) + "┤" + ConsoleColor.RESET);
        lines.add(borderColor + "│" + ConsoleColor.RESET + " " + padRight("Effect: " + effect, TICKET_WIDTH - 1) + borderColor + "│" + ConsoleColor.RESET);
        lines.add(borderColor + "│" + ConsoleColor.RESET + " " + padRight("Totem: " + playerBox, TICKET_WIDTH - 1) + borderColor + "│" + ConsoleColor.RESET);
        lines.add(borderColor + "└" + repeat("─", TICKET_WIDTH) + "┘" + ConsoleColor.RESET);

        return lines.toArray(new String[0]);
    }

    private String renderTicketEffectPlain(BiddingTicketView ticket) {
        if (ticket.getFoodBonus() > 0) {
            return "+" + ticket.getFoodBonus() + " FOOD";
        }

        StringBuilder sb = new StringBuilder();

        if (ticket.getChooseUpperCard() > 0) {
            sb.append("↑ Upper x").append(ticket.getChooseUpperCard());
        }

        if (ticket.getChooseLowerCard() > 0) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }

            sb.append("↓ Lower x").append(ticket.getChooseLowerCard());
        }

        if (sb.isEmpty()) {
            return "No effect";
        }

        return sb.toString();
    }

    private String renderTicketPlayerBoxPlain(BiddingTicketView ticket) {
        if (ticket.getPlayer() == null) {
            return "[ empty ]";
        }

        PlayerView player = ticket.getPlayer();

        return player.getNickname() + " / " + player.getTotemColor();
    }
    private String getCardCategory(CardView card) {
        if (card instanceof BuildingCardView) {
            return "BUILDING";
        }

        if (card instanceof HuntEventView
                || card instanceof PaintingsEventView
                || card instanceof ShamanRitualEventView
                || card instanceof SustenanceEventView) {
            return "EVENT";
        }

        if (card instanceof ArtistCardView
                || card instanceof BuilderCardView
                || card instanceof CollectorCardView
                || card instanceof HunterCardView
                || card instanceof InventorCardView
                || card instanceof ShamanCardView) {
            return "CHARACTER";
        }

        return "CARD";
    }

    private String getCardColor(CardView card) {
        if (card instanceof BuildingCardView) {
            return ConsoleColor.YELLOW_BOLD;
        }

        if (card instanceof HuntEventView
                || card instanceof PaintingsEventView
                || card instanceof ShamanRitualEventView
                || card instanceof SustenanceEventView) {
            return ConsoleColor.RED_BOLD;
        }

        return ConsoleColor.CYAN_BOLD;
    }

    private void appendDetail(List<String> lines, String borderColor, String label, String value) {
        /*String text = label + ": " + safe(value);
        List<String> wrapped = wrap(text, CARD_WIDTH - 2);

        for (String line : wrapped) {
            lines.add(borderColor + "│" + ConsoleColor.RESET + " " + padRight(line, CARD_WIDTH - 1) + borderColor + "│" + ConsoleColor.RESET);
        }*/
        value = safe(value);

        String separator = label.equals("Info") ? " - " : ": ";
        String indent = repeat(" ", label.length() + separator.length());
        String[] parts = value.split("\\R");

        for(int i = 0; i < parts.length; i++) {
            String prefix = (i == 0) ? label + separator : "       ";
            String text = prefix + parts[i];

            List<String> wrapped = wrap(text, CARD_WIDTH - 2);

            for (String line : wrapped) {
                lines.add(borderColor + "│" + ConsoleColor.RESET +
                        " " + padRight(line, CARD_WIDTH - 1) +
                        borderColor + "│" + ConsoleColor.RESET);
            }
        }
    }

    private int maxLines(List<String[]> boxes) {
        int max = 0;

        for (String[] box : boxes) {
            if (box.length > max) {
                max = box.length;
            }
        }

        return max;
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }

    private String repeat(String value, int times) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < times; i++) {
            sb.append(value);
        }

        return sb.toString();
    }

    private String padRight(String text, int width) {
        if (text == null) {
            text = "";
        }

        if (text.length() > width) {
            return text.substring(0, width);
        }

        return text + repeat(" ", width - text.length());
    }

    private String center(String text, int width) {
        if (text == null) {
            text = "";
        }

        if (text.length() > width) {
            return text.substring(0, width);
        }

        int left = (width - text.length()) / 2;
        int right = width - text.length() - left;

        return repeat(" ", left) + text + repeat(" ", right);
    }

    private List<String> wrap(String text, int width) {
        List<String> result = new ArrayList<>();

        if (text == null) {
            result.add("");
            return result;
        }

        String remaining = text;

        while (remaining.length() > width) {
            int breakingPoint = remaining.lastIndexOf(" ", width);

            if (breakingPoint <= 0) {
                breakingPoint = width;
            }

            result.add(remaining.substring(0, breakingPoint).trim());
            remaining = remaining.substring(breakingPoint).trim();
        }

        result.add(remaining);

        return result;
    }
}