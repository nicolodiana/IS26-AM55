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

    public EndGameResultView(
            List<ResolveEventView> resolvedEvents,
            List<EndGameEffectView> endGameEffects,
            Map<String, Integer> winners
    ) {
        this.resolvedEvents = resolvedEvents;
        this.endGameEffects = endGameEffects;
        this.winners = winners;
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
}