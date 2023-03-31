package protocol;
import java.io.*;
import java.net.*;

/**
 * This class uses sockets to connect to the server process and then
 * send and recieves response, allowing the two processes to 
 * communicate according to some pre-defined protocol. 
 * The main() method is configured to work with the Server class
 * using the DavidPaulProtocol.
 */
 public class Client {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Constructor for client to connect to the Server on
     * the specificed port.
     * @hostname -- the name of the host, e.g. localhost
     * @portNumber -- the port number the server is listening on
     */
    public Client(String hostName, int portNumber) {        
        try {
            socket = new Socket(hostName, portNumber);
            out =
                new PrintWriter(socket.getOutputStream(), true);
            in =
                new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }             
    }

    /**
     * Sends a message to the server, waits for the response,
     * and returns it.
     * Note: if the server does not response, this method will
     * blocking waiting for a response.
     * @param userInput -- the message to be sent.
     * @return -- the response from the server.
     */
    public String send(String userInput) {
        String response = "";
        try {
            out.println(userInput);
            response = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unexpected Error.");
            System.exit(-1);
        }
        return response;
    }

    /**
     * Sends a message to the server and does not wait for a response.
     * Use this is send a message to the server which does not expect a response,
     * where the 'send' method would block waiting for the response.
     * @param userInput -- the message to send
     */
    public void sendNoResponse(String userInput) {
        out.println(userInput);
    }

    /**
     * Creates a client that connects to the specified host on the 
     * specified port and enters a request-response loop.
     * @param args -- <hostName> <pot>
     * @throws IOException
     */
     public static void main(String[] args) throws IOException {
          
         if (args.length != 2) {
             System.err.println(
                 "Usage: java AssignmentClient <host name> <port number>");
             System.exit(1);
         }
  
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        Client client = new Client(hostName, portNumber);
        
        BufferedReader stdIn =
               new BufferedReader(
                   new InputStreamReader(System.in));
        String userInput;
        while ((userInput = stdIn.readLine()) != null) {
            // this is to account for lack of response on a PUT, for testing the DavidPaulProtocol
            if (userInput.substring(0,4).equals("PUT ")) {
                client.sendNoResponse(userInput);
            } else {
                String response = client.send(userInput);    
                System.out.println("Response: " + response);  
            }
        }
        System.exit(-1);
     }
 }