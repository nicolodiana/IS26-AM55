package it.polimi.ingsw.am55.view.cli;

import java.util.List;

/**
 * CLI metadata exposed by card view DTOs.
 *
 * @param category human-readable card category
 * @param Color ANSI color used for the card border
 * @param details label/value rows printed inside the card box
 */
public record CliCardInfo(String category, String Color, List<CliCardDetails> details) {
}
