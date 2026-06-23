package it.polimi.ingsw.am55.dto.endgame;

import it.polimi.ingsw.am55.dto.endgame.EndGameEffectView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Serializable DTO containing all final results shown at the end of a game.
 * <p>It groups resolved events, end-game effects, winners, and leaderboard entries for the final client scene.
 */
public class EndGameResultView implements Serializable {
    /**
     * List of the event resolved computed for the final result view.
     */
    private final List<ResolveEventView> resolvedEvents;
    /**
     * DTO field carrying the end game effects value for client-side rendering.
     */
    private final List<EndGameEffectView> endGameEffects;
    /**
     * DTO field carrying the winners value for client-side rendering.
     */
    private final Map<String, Integer> winners;
    /**
     * DTO field carrying the leader board value for client-side rendering.
     */
    private List<LeaderBoardEntryView> leaderBoard;

    /**
     * Creates a end game result view from model data that can be sent to the client.
     *
     * @param resolvedEvents the resolved events value
     * @param endGameEffects the per-player effect values produced by the event
     * @param winners the winners value
     * @param leaderBoard the leader board value
     */
    public EndGameResultView(List<ResolveEventView> resolvedEvents, List<EndGameEffectView> endGameEffects,
                             Map<String, Integer> winners,List<LeaderBoardEntryView> leaderBoard
    ) {
        this.resolvedEvents = resolvedEvents;
        this.endGameEffects = endGameEffects;
        this.winners = winners;
        this.leaderBoard = leaderBoard;
    }

    public List<ResolveEventView> getResolvedEvents() {
        return resolvedEvents;
    }

    public List<EndGameEffectView> getEndGameEffects() {
        return endGameEffects;
    }

    public Map<String, Integer> getWinners() {
        return winners;
    }

    public List<LeaderBoardEntryView> getLeaderBoard() {
        return leaderBoard;
    }
}