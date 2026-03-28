package it.polimi.ingsw.am55.MesosModel;

public class CharacterCard extends TribeCard{
    private int numPlayer;

    public CharacterCard(int id, int era) {
        super(id, era);
    }

    public void addInRightRow(Row upperRow, Row lowerRow){
        lowerRow.addCharacterCard(this);
    }

    public void addInRightList(Row row){
        row.addCharacterCard(this);
    }


}
