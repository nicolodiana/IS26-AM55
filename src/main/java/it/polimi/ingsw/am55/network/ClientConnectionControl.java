package it.polimi.ingsw.am55.network;

/**
 * Interfaccia usata dai messaggi tecnici server -> client.
 * Evita controlli con instanceof: il messaggio concreto decide
 * quale azione di rete deve essere eseguita sul client.
 */
public interface ClientConnectionControl {
    void startPing() throws Exception;
}
