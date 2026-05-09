package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.io.Serializable;

public interface ServerCommand extends Serializable {

    void execute(ServerApplication serverApplication, VirtualView sender) throws Exception;
}