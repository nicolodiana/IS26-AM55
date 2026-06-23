module it.polimi.ingsw.am55 {
    requires java.rmi;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires java.sql;

    exports it.polimi.ingsw.am55;

    exports it.polimi.ingsw.am55.network;
    exports it.polimi.ingsw.am55.network.command;
    exports it.polimi.ingsw.am55.network.middleware;

    exports it.polimi.ingsw.am55.virtualview;

    exports it.polimi.ingsw.am55.message;
    exports it.polimi.ingsw.am55.dto;
    exports it.polimi.ingsw.am55.dto.ClientCards;
    exports it.polimi.ingsw.am55.ClientModel;
    exports it.polimi.ingsw.am55.controller;

    exports it.polimi.ingsw.am55.view.cli;
    exports it.polimi.ingsw.am55.view.gui;

    opens it.polimi.ingsw.am55.network to java.rmi;
    opens it.polimi.ingsw.am55.network.command to java.rmi;
    opens it.polimi.ingsw.am55.network.middleware to java.rmi;
    opens it.polimi.ingsw.am55.virtualview to java.rmi;

    opens it.polimi.ingsw.am55.message to java.rmi;
    opens it.polimi.ingsw.am55.dto to java.rmi;
    opens it.polimi.ingsw.am55.dto.ClientCards to java.rmi;
    opens it.polimi.ingsw.am55.ClientModel to java.rmi;

    opens it.polimi.ingsw.am55.view.gui to javafx.graphics;
    opens it.polimi.ingsw.am55.view.gui.scene to javafx.fxml;
    exports it.polimi.ingsw.am55.utility;
    exports it.polimi.ingsw.am55.network.client;
    opens it.polimi.ingsw.am55.network.client to java.rmi;
    exports it.polimi.ingsw.am55.network.server;
    opens it.polimi.ingsw.am55.network.server to java.rmi;
    exports it.polimi.ingsw.am55.network.networkException;
    opens it.polimi.ingsw.am55.network.networkException to java.rmi;
}