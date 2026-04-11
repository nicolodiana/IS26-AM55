package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Enum.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildingTypeTest {
//per verificare funzionamento dell'enum (poco utile ) ma la usiamo x coprire di piu
    @Test
    void enumValuesAndValueOfAreAvailable() {
        assertEquals(14, BuildingType.values().length);
        assertEquals(BuildingType.BUILDING1, BuildingType.valueOf("BUILDING1"));
        assertEquals(BuildingType.BUILDING14, BuildingType.values()[13]);
    }
}
