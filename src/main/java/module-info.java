module it.polimi.ingsw.am55 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires it.polimi.ingsw.mesos;


    opens it.polimi.ingsw.am55 to javafx.fxml;
    exports it.polimi.ingsw.am55;
}