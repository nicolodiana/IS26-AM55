package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.network.ClientConnectionControl;

import java.io.Serializable;

public abstract class MessageToClient implements Serializable {
//SERVE PER fare dispatch dinamico sul cli model , in modo da capire il tipo di messaggio a runtime senza instanceof
//e permettere alla classe a runtime di scegliere i metodi da usare nel model per aggiornarlo, senza che sia il model client a
// capire come agire in base al tipo runtime
    public void update(ClientModel model){}
//serve per l'RMI Server per capire se il messaggio concreto che riceve , deve avere una consegna broadcast o unicast
    public abstract void deliver(String playerId, MessageDelivery context);

    //ENTRAMBI SONO IMPLEMENTATI DENTRO I MESSAGGI
    /**
     * Hook per i messaggi tecnici di rete.
     * Default: nessuna azione tecnica lato client.
     */
    public void executeClientNetworkAction(ClientConnectionControl client) throws Exception {}

    /**
     * Usato solo nella fase create/join.
     * Se il controller restituisce un ErrorMessage, il server NON deve registrare
     * la VirtualView nella mappa clients e NON deve autorizzare il ping.
     */
    public boolean isConnectionSetupSuccessful() {
        return true;
    }
    public boolean shouldUpdateModel() {return  true;}
}
