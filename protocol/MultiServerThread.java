package protocol;

import java.net.*;
import java.io.*;

/**
 * A server thread which represents a session with a particular client, 
 * connected on a socket. 
 * This thread uses the DavidPaulProtocol, getting the clientID from 
 * the protocol and ensuring that multiple clients are not connected
 * with the same ID.
 * For ensure this, the list of clientIDs is kept in 
 * AssignmentMultiServer in a synchronised Set data
 * structure. Each thread will acquire a lock on the
 * shared set before reading and modifying it, to ensure
 * correctness.
 * 
 * This thread then passes all messages between the client and the 
 * DavidPaulProtocol until the session ends, at which point the 
 * thread is terminated.
 * 
 */
public class MultiServerThread extends Thread {
    private Socket socket = null;
    private String clientID;
    private DavidPaulProtocol dpp;
 
    public MultiServerThread(Socket socket) {
        super("MultiServerThread");
        this.socket = socket;
    }

    public void run() {
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
            try {
                String outputLine, inputLine;
                String firstInput = getNextLine(in);
                Boolean exitFlag = false;
                
                this.dpp = new DavidPaulProtocol();
                
                // get client id from protocol
                this.clientID = dpp.connect(firstInput);
                System.out.println("clientID: " + clientID);

                // acquire lock on shared connected clients list and ensure
                // a client with this username is not already connected
                // send connection accepted message if no errors
                synchronized (MultiServer.connectedClients) {
                    if (MultiServer.connectedClients.contains(clientID)) {
                    exitFlag = true;
                    } else {
                        MultiServer.connectedClients.add(clientID);
                        out.print("CONNECT: OK\n");
                        out.flush();
                        System.out.println("Num of clients now connected: " + MultiServer.connectedClients.size());
                        System.out.println("Client " + clientID + " now connected");
                    }
                }
            
                if (exitFlag) {
                    // there was already a client with this clientID logged in  
                    out.print("CONNECT: ERROR\n");
                    out.flush();
                    socket.close();
                    return; // this kills the thread
                }

                // the client has successfully connected - enter the main
                // input/output loop
                while ((inputLine = getNextLine(in)) != null) {
                    System.out.println("INPUT: " + inputLine);
                    outputLine = dpp.processInput(inputLine);
                    if (outputLine != null) {
                        System.out.println("OUTPUT: " + outputLine);
                        out.print(outputLine + "\n");
                        out.flush();
                    } 
                    // check for disconnect -- destroy thread if so
                    if (dpp.isDisconnect()) {
                        break;
                    }
                }

            } catch (SocketException e) {
                // client unexpectedly closed socket or something else went wrong
                // just let the thread die
            } catch (IOException e) {
                System.out.println("Client sent unsupported message - closing connection.");
                // e.printStackTrace();
            } finally {
                // we want to make sure the client doesn't stay logged in under any circumstance
                MultiServer.connectedClients.remove(clientID);
                    System.out.println("Number of clients now connected: " + MultiServer.connectedClients.size());
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the next line from the input stream. 
     * Returns null if nothing was read in. 
     * 
     * @param in - the input stream
     * @return - the input as a string, not including the newline character
     * @throws IOException - rethrows from the input stream
     */
    private String getNextLine(BufferedReader in) throws IOException {
        StringBuilder nextLine = new StringBuilder();
        boolean emptyString = false;
        try {
            int nextChar = in.read();
                    if (nextChar == -1 && !emptyString) {
                        emptyString = true;
                    }
                    while (nextChar != -1) {
                        // keep getting chars until we see a newline
                        if (nextChar == 10) {
                            break;
                        }
                        if (nextChar > 128) { throw new IOException(); }
                        // uncomment to prevent /r - our protocol doesn't use them but
                        // sometimes they get added in by Windows.] 
                        // if (nextChar == 13) {
                        //     nextChar = in.read();
                        //     continue;
                        // }
                        nextLine.append((char) nextChar);
                        nextChar = in.read();
                    }
        } catch (IOException e) {
            throw e;
        }
        if (emptyString) return null;
        return nextLine.toString();
    }
}
