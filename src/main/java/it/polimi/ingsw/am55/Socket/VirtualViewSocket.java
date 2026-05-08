package it.polimi.ingsw.am55.Socket;

import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.virtualview.VirtualView;

public interface VirtualViewSocket extends VirtualView {
    void onMessage(MessageToClient message) throws Exception;
}
