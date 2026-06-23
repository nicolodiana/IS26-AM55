package it.polimi.ingsw.am55.network;

import it.polimi.ingsw.am55.utility.*;
import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.network.middleware.ServerStub;
import it.polimi.ingsw.am55.virtualview.VirtualServer;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Factory responsible for creating the network client according to the selected
 * communication technology.
 *
 * <p>The application bootstrap does not need to know how an RMI client or a
 * socket client is initialized: it receives a ready-to-use {@link ClientImpl}.</p>
 */
public class ClientImplFactory {

    /**
     * Name used to retrieve the game server from the RMI registry.
     */
    private static final String SERVER_NAME = "GameServer";


    /**
     * Creates a client connected through the technology specified by the configuration.
     *
     * @param config client connection configuration
     * @param model local client model associated with the created client
     * @return the initialized client implementation
     * @throws ClientStartupException if the client cannot connect to or initialize the selected server
     */
    public static ClientImpl create(ClientConfig config, ClientModel model) throws ClientStartupException {
        return switch (config.getConnectionTechnology()) {
            case RMI -> createRmiClient(config.getHost(), config.getRmiPort(), model);
            case SOCKET -> createSocketClient(config.getHost(), config.getSocketPort(), model);
        };
    }

    /**
     * Creates a client connected to the server registered in the specified RMI registry.
     *
     * @param host host of the RMI registry
     * @param port port of the RMI registry
     * @param model local client model associated with the created client
     * @return the initialized RMI client
     * @throws ClientStartupException if the registry or registered server cannot be used to initialize the client
     */
    private static ClientImpl createRmiClient(String host, int port, ClientModel model) throws ClientStartupException {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            VirtualServer server = (VirtualServer) registry.lookup(SERVER_NAME);

            return new ClientImpl(server, model);

        } catch (RemoteException e) {
            throw new ClientStartupException("Unable to connect to RMI server on " + host + ":" + port + ".", e);

        } catch (NotBoundException e) {
            throw new ClientStartupException("The RMI registry is reachable, but the server is not registered under the name" + SERVER_NAME + ".", e
            );

        } catch (ClassCastException e) {
            throw new ClientStartupException("The object found in the RMI registry is not a valid VirtualServer.", e);
        }
    }

    /**
     * Creates a socket client and starts its server-message listener.
     *
     * @param host host of the socket server
     * @param port port of the socket server
     * @param model local client model associated with the created client
     * @return the initialized socket client
     * @throws ClientStartupException if the connection or client initialization fails
     */
    private static ClientImpl createSocketClient(String host, int port, ClientModel model)
            throws ClientStartupException {
        ServerStub serverStub = null;

        try {
            serverStub = new ServerStub(host, port);

            ClientImpl client = new ClientImpl(serverStub, model);
            serverStub.startListener(client);

            return client;

        } catch (RemoteException e) {
            if (serverStub != null) {
                serverStub.close();
            }
            throw new ClientStartupException("Error creating local client.", e);

        } catch (IOException e) {
            throw new ClientStartupException("Unable to connect to Socket server at " + host + ":" + port + ".", e);
        }
    }
}
