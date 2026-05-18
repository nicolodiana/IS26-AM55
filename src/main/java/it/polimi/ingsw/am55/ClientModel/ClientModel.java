package it.polimi.ingsw.am55.ClientModel;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.view.ClientModelObserver;

import java.util.ArrayList;
import java.util.List;

public class ClientModel {
    private boolean lastMessageUpdatedGameView;
    private final Object lock = new Object();
    private EndGameResultView endGameResultView=null;
    private GameView gameView;
    private String stateRequest;
    private String lastError;
    private boolean gameStarted;
    private boolean gameEnded;
    private boolean gameCrashed;
    private boolean commandDone = false;

    private final List<ClientModelObserver> observers;
    private final List<CardView> myHand;

    public ClientModel() {
        this.gameView = null;
        this.stateRequest = null;
        this.lastError = null;
        this.gameStarted = false;
        this.observers = new ArrayList<>();
        this.myHand = new ArrayList<>();
       // this.gameEnded = false;
       // this.gameCrashed = false;
    }

    public void update(MessageToClient message) {
        /*
         * message.update(this) modificherà il ClientModel
         * chiamando setGameView, setStateRequest, setLastError, ecc.
         *
         * Dopo l'update notifico gli observer.
         */
        message.update(this);
        notifyObservers();
    }
    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }
    public void addObserver(ClientModelObserver observer) {
        synchronized (lock) {
            if (observer != null && !observers.contains(observer)) {
                observers.add(observer);
            }
        }
    }
    public void setGameCrashed(boolean gameCrashed) {
        this.gameCrashed = gameCrashed;
    }
    public boolean isGameCrashed() {
        synchronized (lock) {
            return gameCrashed;
        }
    }
    public void removeObserver(ClientModelObserver observer) {
        synchronized (lock) {
            observers.remove(observer);
        }
    }

    public void notifyObservers() {
        List<ClientModelObserver> observersCopy;

        synchronized (lock) {
            observersCopy = new ArrayList<>(observers);
        }

        for (ClientModelObserver observer : observersCopy) {
            observer.onModelChanged(this);
        }
    }
    public boolean isGameEnded() {
        synchronized (lock) {
            if (gameView.getState().equals(GameState.ENDED)) {
                return true;
            }
            return false;
        }
    }
    public GameView getGameView() {
        synchronized (lock) {
            return gameView;
        }
    }

    public void setGameView(GameView gameView) {
        synchronized (lock) {
            this.gameView = gameView;
        }
    }

    public String getStateRequest() {
        synchronized (lock) {
            return stateRequest;
        }
    }

    public void setStateRequest(String stateRequest) {
        synchronized (lock) {
            this.stateRequest = stateRequest;
        }
    }

    public String getLastError() {
        synchronized (lock) {
            return lastError;
        }
    }

    public void setLastError(String lastError) {
        synchronized (lock) {
            this.lastError = lastError;
        }
    }

    public void clearError() {
        synchronized (lock) {
            this.lastError = null;
        }
    }

    public boolean isGameStarted() {
        synchronized (lock) {
            return gameStarted;
        }
    }

    public void setGameStarted(boolean gameStarted) {
        synchronized (lock) {
            this.gameStarted = gameStarted;
        }
    }

    public boolean isCommandDone() {
        synchronized (lock) {
            return commandDone;
        }
    }

    public void setCommandDone(boolean commandDone) {
        synchronized (lock) {
            this.commandDone = commandDone;
        }
    }

    public List<CardView> getMyHand() {
        synchronized (lock) {
            return new ArrayList<>(myHand);
        }
    }

    public void setMyHand(List<CardView> cards) {
        synchronized (lock) {
            this.myHand.clear();

            if (cards != null) {
                this.myHand.addAll(cards);
            }
        }
    }

    public void addCard(CardView card) {
        synchronized (lock) {
            if (card != null) {
                this.myHand.add(card);
            }
        }
    }

    public void setLastMessageUpdatedGameView(boolean lastMessageUpdatedGameView) {
        synchronized (lock) {
            this.lastMessageUpdatedGameView = lastMessageUpdatedGameView;
        }
    }

    public boolean isLastMessageUpdatedGameView() {
        synchronized (lock) {
            return lastMessageUpdatedGameView;
        }
    }

    public EndGameResultView getEndGameResultView() {
        synchronized (lock) {
            return endGameResultView;
        }
    }

    public void setEndGameResultView(EndGameResultView endGameResultView) {
        synchronized (lock) {
            this.endGameResultView = endGameResultView;
        }
    }

    //------------------METODI CHE MODIFICANO IL MODEL----------------------

    public void addCard(int cardId) {
        synchronized (lock) {
            /*
             * Quando riattiverai CardLoader/CardFactory:
             *
             * ClientCard card = cardFactory.createCard(String.valueOf(cardId));
             * this.myHand.add(card);
             */
        }
    }
}