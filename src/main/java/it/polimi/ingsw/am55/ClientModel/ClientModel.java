package it.polimi.ingsw.am55.ClientModel;

import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.view.ClientModelObserver;

import java.util.ArrayList;
import java.util.List;

public class ClientModel {

    private GameView gameView;
    private String stateRequest;
    private String lastError;
    private boolean gameStarted;

    private final List<ClientModelObserver> observers;

    public ClientModel() {
        this.gameView = null;
        this.stateRequest = null;
        this.lastError = null;
        this.gameStarted = false;
        this.observers = new ArrayList<>();
    }

    public void update(MessageToClient message) {
        message.update(this);
        notifyObservers();
    }

    public void addObserver(ClientModelObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(ClientModelObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (ClientModelObserver observer : observers) {
            observer.onModelChanged(this);
        }
    }

    public GameView getGameView() {
        return gameView;
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public String getStateRequest() {
        return stateRequest;
    }

    public void setStateRequest(String stateRequest) {
        this.stateRequest = stateRequest;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public void clearError() {
        this.lastError = null;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
}