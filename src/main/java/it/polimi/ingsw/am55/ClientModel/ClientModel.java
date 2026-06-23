package it.polimi.ingsw.am55.ClientModel;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.LobbyView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.view.ClientModelObserver;

import java.util.ArrayList;
import java.util.List;


 /** Client-side state holder updated by messages received from the server.
  * <p>It stores the latest lobby, game, and terminal states, then notifies registered observers
  * so the CLI or GUI can render the new state consistently.
  */
public class ClientModel implements  ClientModelUpdater {
     /**
      * Field storing the last message updated game view value used by client model.
      */
     private boolean lastMessageUpdatedGameView;
     /**
      * Field storing the lock value used by client model.
      */
     private final Object lock = new Object();
     /**
      * Final scoring snapshot displayed at the end of the game.
      */
     private EndGameResultView endGameResultView=null;
     /**
      * Latest game snapshot available to the client-side view.
      */
     private GameView gameView;
     /**
      * Field storing the state request value used by client model.
      */
     private String stateRequest;
     /**
      * Field storing the last error value used by client model.
      */
     private String lastError;
     /**
      * Field storing the game started value used by client model.
      */
     private boolean gameStarted;
     /**
      * Field storing the game ended value used by client model.
      */
     private boolean gameEnded;
     /**
      * Field storing the game crashed value used by client model.
      */
     private boolean gameCrashed;
     /**
      * Field storing the in lobby value used by client model.
      */
     private boolean inLobby = true;
     /**
      * Latest lobby snapshot available to the client-side view.
      */
     private LobbyView lobbyView;
     /**
      * Field storing the last message value used by client model.
      */
     private MessageToClient lastMessage;

     /**
      * List storing observers used by client model.
      */
     private final List<ClientModelObserver> observers;
     /**
      * Cards currently visible in the local player hand.
      */
     private final List<CardView> myHand;

    /**
     * Creates a new client model instance and initializes its internal state.
     */
    public ClientModel() {
        this.gameView = null;
        this.stateRequest = null;
        this.lastError = null;
        this.gameStarted = false;
        this.observers = new ArrayList<>();
        this.myHand = new ArrayList<>();
        this.gameEnded = false;
        this.gameCrashed = false;
    }


    /**
     * Handles the update workflow.
     *
     * @param message the detail message associated with the exception or response
     */
    @Override
    public void handleUpdate(MessageToClient message) {
        /*
         * message.update(this) update the ClientModel
         * calling setGameView, setStateRequest, setLastError, ecc.
         *
         * After the update it notifies the observers
         */
        synchronized (lock) {
            this.lastMessage = message;
        }
        message.update(this);
        notifyObservers();
    }

    /**
     * Returns the latest informational message received by the client model.
     *
     * @return the last message value
     */
    public MessageToClient getLastMessage() {
        synchronized (lock) {
            return lastMessage;
        }
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

     /**
      * Adds the provided observer to this client model.
      *
      * @param observer the observer value
      */
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


     /**
      * Checks whether this client model satisfies the is game crashed condition.
      *
      * @return the gameCrashed's value
      */
    public boolean isGameCrashed() {
        synchronized (lock) {
            return gameCrashed;
        }
    }

     /**
      * Removes the selected observer from this client model.
      *
      * @param observer the observer value
      */
    public void removeObserver(ClientModelObserver observer) {
        synchronized (lock) {
            observers.remove(observer);
        }
    }

     /**
      * Notifies all registered observers that the client model state has changed.
      */
    public void notifyObservers() {
        List<ClientModelObserver> observersCopy;

        synchronized (lock) {
            observersCopy = new ArrayList<>(observers);
        }

        for (ClientModelObserver observer : observersCopy) {
            observer.onModelChanged(this);
        }
    }

     /**
      * Checks whether this client model satisfies the is game ended condition.
      *
      * @return true if the condition is satisfied; false otherwise
      */
    public boolean isGameEnded() {
        synchronized (lock) {
            return gameEnded;
        }
    }

     /**
      * Returns the latest game view snapshot stored by the client model.
      *
      * @return the game view value
      */
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

     /**
      * Returns the latest client action state requested by the server update.
      *
      * @return the state request value
      */
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

     /**
      * Returns the latest error message received by the client model.
      *
      * @return the last error value
      */
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

     /**
      * Clears the error data stored by this client model.
      */
    public void clearError() {
        synchronized (lock) {
            this.lastError = null;
        }
    }

     /**
      * Checks whether this client model satisfies the is game started condition.
      *
      * @return true if the condition is satisfied; false otherwise
      */
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


     /**
      * Returns the cards currently visible in the local player hand.
      *
      * @return the local player's myHand value
      */
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

     /**
      * Checks whether this client model satisfies the is last message updated game view condition.
      *
      * @return true if the condition is satisfied; false otherwise
      */
    public boolean isLastMessageUpdatedGameView() {
        synchronized (lock) {
            return lastMessageUpdatedGameView;
        }
    }

     /**
      * Returns the final scoring result stored after game resolution.
      *
      * @return the end game result view value
      */
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

     /**
      * Checks whether this client model satisfies the is in lobby condition.
      *
      * @return true if the condition is satisfied; false otherwise
      */
    public boolean isInLobby() {
        synchronized (lock) {
            return inLobby;
        }
    }

    public void setInLobby(boolean inLobby) {
        synchronized (lock) {
            this.inLobby = inLobby;
        }
    }

    public LobbyView getLobbyView() {
        synchronized (lock) {
            return lobbyView;
        }
    }

    public void setLobbyView(LobbyView lobbyView) {
        synchronized (lock) {
            this.lobbyView = lobbyView;
        }
    }

    /**
     * call the gameView placeTotem
     * @param playerId player identifier
     * @param index bidding ticket identifier
     */
    public void placeTotem(String playerId, int index) {
        synchronized (lock) {
            gameView.placeTotem(playerId, index);
        }
    }

    /**
     * call the pickCard in gameView
     * @param playerId player identifier
     * @param index card identifier
     * @param newFood num of player's food after pickcard
     * @param newPp num of player's pp after pickcard
     */
    public void pickCard(String playerId, int index, int newFood, int newPp) {
        synchronized (lock) {
            gameView.pickCard(playerId, index, newFood, newPp);
        }
    }

    public void setCurrentPlayer(String playerd) {
        synchronized (lock) {
            this.gameView.setCurrentPlayer(playerd);
        }
    }

    public void setCurrentGameState(GameState state) {
        synchronized (lock) {
            this.gameView.setState(state);
        }
    }
}
