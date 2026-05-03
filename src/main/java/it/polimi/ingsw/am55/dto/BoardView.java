package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.SharedBoard.Board;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.Board;

import java.io.Serializable;

public class BoardView implements Serializable {

    private final String printableBoard;

    public BoardView(Board sharedBoard) {
        this.printableBoard = sharedBoard != null
                ? sharedBoard.toString()
                : "Board non disponibile";
    }

    public String getPrintableBoard() {
        return printableBoard;
    }

    @Override
    public String toString() {
        return printableBoard;
    }
}