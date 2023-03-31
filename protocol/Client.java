package protocol;
import java.io.*;
import java.net.*;
  
 public class Client {

    private String hostName;
    private int portNumber;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;

        
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

    public void sendNoResponse(String userInput) {
        out.println(userInput);

    }
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
            // this is to account for lack of response on a PUT 
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