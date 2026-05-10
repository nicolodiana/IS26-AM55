package it.polimi.ingsw.am55.dto.endgame;

public class EndGameView {
    private final GameView gameView;
    private final List<ResolveEventView> resolvedEvents;
    private final List<EndGameEffectView> endGameEffects;
    private final List<PlayerView> ranking;

    public EndGameView(
            GameView gameView,
            List<ResolveEventView> resolvedEvents,
            List<EndGameEffectView> endGameEffects,
            List<PlayerView> ranking
    ) {
        this.gameView = gameView;
        this.resolvedEvents = resolvedEvents;
        this.endGameEffects = endGameEffects;
        this.ranking = ranking;
    }

    public GameView getGameView() {
        return gameView;
    }

    public List<ResolveEventView> getResolvedEvents() {
        return resolvedEvents;
    }

    public List<EndGameEffectView> getEndGameEffects() {
        return endGameEffects;
    }

    public List<PlayerView> getRanking() {
        return ranking;
    }
}
