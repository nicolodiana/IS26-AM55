package it.polimi.ingsw.am55.dto.endgame;

import it.polimi.ingsw.am55.dto.endgame.EndGameEffectView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EndGameResultView implements Serializable {

    private final List<ResolveEventView> resolvedEvents;
    private final List<EndGameEffectView> endGameEffects;
    private final Map<String, Integer> winners;
    private List<LeaderBoardEntryView> leaderBoard;

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