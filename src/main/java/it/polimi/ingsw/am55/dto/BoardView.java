package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.SharedBoard.Board;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.Board;

import java.io.Serializable;
import java.util.List;

public class BoardView implements Serializable {

    List<CardView> upperRow;
    List<CardView> lowerRow;
    List<BiddingTicketView> biddingTrail;


    //private final String printableBoard;

    public BoardView(List<CardView> upperRow, List<CardView> lowerRow, List<BiddingTicketView> biddingTrail) {
        this.upperRow = upperRow;
        this.lowerRow = lowerRow;
        //this.biddingTrail = board.getBiddingTrail().getTicketIds();
        this.biddingTrail = biddingTrail;

        /*this.printableBoard = sharedBoard != null
                ? sharedBoard.toString()
                : "Board non disponibile";*/
    }

    /*public String getPrintableBoard() {
        return printableBoard;
    }

    @Override
    public String toString() {
        return printableBoard;
    }*/
}