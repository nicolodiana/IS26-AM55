package it.polimi.ingsw.am55.controller;

public interface UserActionHandler {

    void onCreateGameSelected(String playerId, String totem, int numPlayers);

    void onJoinGameSelected(String playerId, String totem);

    void onPickCardSelected(String playerId, int cardId);

    void onPickSpecialSelected(String playerId, int cardId);

    void onPlaceTotemSelected(String playerId,int index);

    void onQuitGameSelected(String playerId);

}