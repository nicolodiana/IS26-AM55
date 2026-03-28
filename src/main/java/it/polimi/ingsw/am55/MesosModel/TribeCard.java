package it.polimi.ingsw.am55.MesosModel;

public abstract class  TribeCard extends Card {


    protected TribeCard(int id, int era) {
        super(id, era);
    }

    public abstract void addInRightRow(Row upperRow, Row lowerRow);

    public abstract void addInRightList(Row row);
}
