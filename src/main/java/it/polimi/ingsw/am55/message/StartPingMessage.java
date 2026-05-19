package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.network.ClientConnectionControl;

/**
 * Messaggio tecnico inviato dal server dopo una create/join riuscita.
 * Quando il client lo riceve, è autorizzato ad avviare il ping periodico.
 */
public class StartPingMessage implements MessageToClient {

    private static final long serialVersionUID = 1L;

    @Override
    public void update(ClientModel model) {
        // Messaggio tecnico: non modifica il model visibile alla view.
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.sendTo(playerId, this);
    }

    @Override
    public void executeClientNetworkAction(ClientConnectionControl client) throws Exception {
        client.startPing();
    }
    @Override
    public boolean shouldUpdateModel() {return  false;}
}
