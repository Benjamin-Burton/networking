package protocol;

import java.net.*;
import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Run this class to run the Server.
 * 
 * Accepts socket connections from clients and 
 * passes their socket to a new thread to handle
 * concurrent clients.
 * 
 * Holds a threadsafe set for storage of clientIds.
 * Currently set up to work with DavidPaulProtocol.java
 * as the protocol.
 * 
 * The server will run until it is sent a kill signal - 
 * e.g. Ctrl-c from the terminal.
 */
public class MultiServer {

    // A set of client IDs each representing a connected client
    // Two clients with the same clientID cannot connect simultaneously
    // Checking for this is done in AssignmentMultiServerThread
    public final static Set<String> connectedClientsUnsync = new HashSet<>();
    public final static Set<String> connectedClients = Collections.synchronizedSet(connectedClientsUnsync);
    public static void main(String[] args) throws IOException {
        
        if (args.length != 1) {
            System.err.println("Usage: java AssignmentMultiServer <port number>");
            System.exit(1);
        }
 
        int portNumber = -1;
        try {
            portNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Port number must be an integer");
            System.exit(-1);
        }
        boolean listening = true;
         
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
            while (listening) {
                MultiServerThread t = new MultiServerThread(serverSocket.accept());
                // TODO it may be possible for the socket to drop here already, causing an exception. 
                t.start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port.");
            System.exit(-1);
        }
    }
}

