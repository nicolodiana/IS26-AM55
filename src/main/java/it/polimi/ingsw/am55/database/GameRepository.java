package it.polimi.ingsw.am55.database;

import it.polimi.ingsw.am55.dto.endgame.LeaderBoardEntryView;

import java.util.List;
import java.util.Map;

public interface GameRepository {
    void registerPlayer(String gameId, String playerNickname, int ppPoint, int foodPoint);
    void registerGame(String gameID, int numPlayers);
    List<LeaderBoardEntryView> getGeneralClassification(int numPlayers);

}
