package protocol;

import java.net.*;
import java.io.*;
import java.util.HashMap;

/**
 * An implementation of the David Paul Protocol, named after
 * my Networking lecturer.
 * All messages accepted must be terminated by the newline character.
 * The protocol only accepts ASCII characters up to 127.  
 * 
 * The connect method must be called first, after instantation, and passed
 * 'CONNECT clientID' where the clientID is a username which does not contain a newline.
 * Otherwise, an IOException is thrown.
 * ClientId cannot be "" or only "\r" - this will cause the server to drop the connection.
 * key-value storage is then intialised for the client on the server, which the client
 * interacts with as follows:
 * 
 * PUT key
 * the server does not response to this message, but waits for another message
 * containing the value.
 * If the key-value pair cannot be stored, the server returns PUT: ERROR.
 * 
 * GET key
 * the server returns a previously stored key, or returns GET: ERROR.
 * 
 * DELETE
 * the server tries to delete the given key and returns
 * DELETE: OK if successful or 
 * DELETE: ERROR if unsuccessful, or if the key did not exists
 * 
 * DISCONNECT
 * the server gracefully closes the connection, returning the message
 * DISCONNECT: OK.
 * 
 * All other messages cause the server to close the socket without 
 * response.
 * 
 */ 
public class DavidPaulProtocol {

    private enum Command {
        CONNECT {
            public String toString() {
                return "CONNECT ";
            }
        },
        PUT {
            public String toString() {
                return "PUT ";
            }
        },
        GET {
            public String toString() {
                return "GET ";
            }
        },
        DELETE {
            public String toString() {
                return "DELETE ";
            }
        },
        DISCONNECT {
            public String toString() {
                return "DISCONNECT";
            }
        }
    }
 
    private final HashMap<String, String> storage = new HashMap<>();
    private String newKey;
    private boolean waitingForPutValue = false;
    private boolean isConnected;
    private boolean isDisconnect;
    
    /**
     * Must be instantiated before use.
     */
    public DavidPaulProtocol() {

    }

    /**
     * This method must be called first, otherwise IOException will the thrown. 
     * The input string must be of the form CONNECT <clientID>\n
     * This initialises key-value storage for the connected client.
     * @param input
     * @return
     * @throws IOException
     */
    public String connect(String input) throws IOException {
        if (input == null) { throw new IOException(); }
        if (isConnected) { throw new IOException(); }
        if (input.length() < 9) { throw new IOException("Input does not meet the required format."); }
        // parse the string - must start with CONNECT, followed by a space, and then the ClientID excluding newline
        if (!input.substring(0,8).equals(Command.CONNECT.toString())) {
            throw new IOException();
        }
        if (input.replace("\r","").length() == Command.CONNECT.toString().length()) {
            throw new IOException();
        }
        input = input.replace("\r", "");
        for (int i = 0; i < input.length(); i++) {
            System.out.println((int) input.charAt(i));
        }
        isConnected = true;
        return input.substring(8, input.length());
    }

    /**
     * This is the main loop for the protocol. 
     * Once the client has successfully connected, all input lines from the 
     * client are sent to this function. The function should return the 
     * correct message to send back to the client as per the protocol.
     * If the return string is null, nothing is sent to the client.
     * @param input
     * @return String -- the message to return to the client, null if nothing should be sent.
     */
    public String processInput(String input) throws IOException {
        if (input == null) { throw new IOException(); }
        if (!isConnected) { throw new IOException(); }

        String output = null;
        isDisconnect = false;
        // PUT VALUE
        if (waitingForPutValue == true) {
            System.out.println("Putting new key value pair: " + newKey + " " + input);
            storage.put(this.newKey, input);
            newKey = null;
            waitingForPutValue = false;
            output = "PUT: OK";
        } else {
            // PUT
            if (input.substring(0, 4).equals(Command.PUT.toString())) {
                
                System.out.println("FIRST PUT COMMAND OK");
                this.newKey = input.substring(4, input.length());
                System.out.println("The new key is: " + newKey + " of length " + newKey.length());
                this.waitingForPutValue = true;
                for (int i = 0; i < newKey.length(); i++) {
                    char ch = newKey.charAt(i);
                    int as_int = ch;
                    System.out.println(as_int);
                }
                System.out.println("Waiting for put = " + waitingForPutValue);
                return null;
            }    
            // GET
            else if (input.substring(0, 4).equals(Command.GET.toString())) {
                System.out.println("Client getting key");
                String key = input.substring(4, input.length());
                System.out.println("GET key = " + key);
                for (int i = 0; i < key.length(); i++) {
                    char ch = key.charAt(i);
                    int as_int = ch;
                    System.out.println(as_int);
                }
                if (storage.containsKey(key)) {
                    output = storage.get(key);
                } else {
                    output = "GET: ERROR";
                }
            }
            // DELETE 
            else if (input.substring(0, 7).equals(Command.DELETE.toString())) {
                System.out.println("Client deleting key");
                String key = input.substring(7, input.length());
                if (storage.containsKey(key)) {
                    storage.remove(key);
                    output = "DELETE: OK";
                } else {
                    output = "DELETE: ERROR";
                }
            }
            // DISCONNECT
            else if (input.substring(0, 10).equals(Command.DISCONNECT.toString())) {
                System.out.println("client disconnecting.");
                this.isDisconnect = true;
                output = "DISCONNECT: OK";
            }
            
            // No commands matched - error
            else {
                // throw new IOException();
            }
        }
        return output;
    }

    /**
     * Checks to see if the the last message was a disconnect message.
     * @return boolean -- true if the last message was a disconnect message
     */
    boolean isDisconnect() {
        return this.isDisconnect;
    }
}
