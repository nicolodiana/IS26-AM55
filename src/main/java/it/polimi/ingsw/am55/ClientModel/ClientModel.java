package it.polimi.ingsw.am55.ClientModel;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.view.cli.ClientModelObserver;

import java.util.ArrayList;
import java.util.List;

public class ClientModel {

    // PER CREARE LE CARTE
    //CardLoader loader = CardLoader.loadFromJson();
    //CardFactory cardFactory = new CardFactory(loader);
    //
    private GameView gameView;
    private String stateRequest;
    private String lastError;
    private boolean gameStarted;

    private final List<ClientModelObserver> observers;
    private List<CardView> myHand = new ArrayList<>();

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

    public List<CardView> getMyHand() {
        return myHand;
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

    //------------------METODI CHE MODIFICANO IL MODEL----------------------
    public void addCard(int cardId) {
        //ClientCard card = cardFactory.createCard(String.valueOf(cardId));
        //this.myHand.add(card);
    }
}