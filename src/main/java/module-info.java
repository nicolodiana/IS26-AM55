module it.polimi.ingsw.am55 {
    requires java.rmi;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;

    exports it.polimi.ingsw.am55.network.rmi.server;
    exports it.polimi.ingsw.am55.network.rmi.client;

    exports it.polimi.ingsw.am55.message;
    exports it.polimi.ingsw.am55.dto;
    exports it.polimi.ingsw.am55.dto.ClientCards;
    exports it.polimi.ingsw.am55.ClientModel;
    exports it.polimi.ingsw.am55.controller;

    exports it.polimi.ingsw.am55.view.cli;
    exports it.polimi.ingsw.am55.virtualview;

    opens it.polimi.ingsw.am55.network.rmi.server to java.rmi;
    opens it.polimi.ingsw.am55.network.rmi.client to java.rmi;

    opens it.polimi.ingsw.am55.message to java.rmi;
    opens it.polimi.ingsw.am55.dto to java.rmi;
    opens it.polimi.ingsw.am55.dto.ClientCards to java.rmi;
    opens it.polimi.ingsw.am55.ClientModel to java.rmi;


}