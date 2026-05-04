package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.ClientCards.HuntEventView;


import java.util.List;

public class HuntEventCard extends EventCard {
    private final int numPP;

    public HuntEventCard(int id, int era, int numPP) {
        this.numPP = numPP;
        super(id, era);
    }

    //evento caccia
    public void activateEvent(List<Player> players) {
        for (Player p : players) {
            int numHunters = p.countByType(CharacterType.HUNTER);
            int bonusPerBuilding8 = p.hasBuilding(BuildingType.BUILDING8) ? numHunters : 0;

            p.addPP(numHunters * numPP + bonusPerBuilding8);
            p.addFood(numHunters + bonusPerBuilding8);
        }
    }

    public int getOrder(){
        return 0;
    }

    public HuntEventView toView() { return new HuntEventView(getId(), this.era, this.numPP); }
}

