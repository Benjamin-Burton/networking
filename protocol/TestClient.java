package protocol;

/**
 * Test suite for the Server/Client. 
 * 
 * Connects to the Server running on localhost on port 7.
 * 
 * Runs correctness tests and concurrency tests for 
 * connections to the server and adherance to the 
 * DavidPaulProtocol.
 */
public class TestClient {

    /**
     * Tests for ensuring that multiple clients can connect and
     * that multiple clients with the same username cannot
     * simultaneously connect.
     */
    public static void concurrencyTests() {
        // check 10 clients can simultaneously connect
        Client[] clients = new Client[10];

        // concurrency test 1 - connect 10 clients simultaneously
        String response;
        for (int i = 0; i < 10; i++) {
            clients[i] = new Client("localhost", 7);
            response = clients[i].send("CONNECT " + "testClient" + i);
            if (!response.equals("CONNECT: OK")) {
                System.out.println("Client connection error. Test client " + i);
            }
        }
        System.out.println("Concurrency test 1 succeeded.");

        // concurrency test 2 - try to connect client again with the same clientID
        Client c = new Client("localhost", 7);
        response = c.send("CONNECT testClient5");
        if (response.equals("CONNECT: ERROR")) {
            System.out.println("Concurrency test 2 succeeded.");
        } else {
            System.out.println("Concurrency test 2 failed.");
        }

    }
    /**
     * A series of tests for a Server running the
     * DavidPaulProtocol.
     * @param args
     */
    public static void main (String[] args) {
        // test #1 - connect
        Client c = new Client("localhost", 7);

        String response = c.send("CONNECT Ben");

        if (response.equals("CONNECT: OK")) {
            System.out.println("test 1 succeeded");
        } else {
            System.out.println("test 1 failed.");
        }

        response = null;

        // test #2 - put - follows from test 1

        // response = c.putKV("KEY","VAL");
        // System.out.println("Response: "+ response);

        c.sendNoResponse("PUT daughter");
        response = c.send("petra");
        if (response.equals("PUT: OK")) {
            System.out.println("test 2 succeeded");
        } else if (response.equals("PUT: ERROR")) {
            System.out.println("test 2 returned error: investigation required.");
        } else {
            System.out.println("test 2 failed");
        }

        // test #3 - get - follows from test #2

        response = c.send("GET daughter");
        if (response.equals("petra")) {
            System.out.println("test 3 succeeded");
        } else {
            System.out.println(response);
            System.out.println("test 3 failed");
        }

        // test #4 - get with key that doesn't exist - follows from test #2

        response = c.send("GET rival");
        if (response.equals("GET: ERROR")) {
            System.out.println("test 4 succeeded");
        } else {
            System.out.println("test 4 failed");
        }

        // test #5 - delete - follows from test #2

        response = c.send("DELETE daughter");
        if (response.equals("DELETE: OK")) {
            System.out.println("test 5 succeeded");
        } else {
            System.out.println("test 5 failed");
            System.out.println(response);
        }

        // test #6 - delete with key that doesn't exist - follows from test #2

        response = c.send("DELETE rival");
        if (response.equals("DELETE: ERROR")) {
            System.out.println("test 6 succeeded");
        } else {
            System.out.println("test 6 failed");
        }

        // test #7 - disconnect - follows from test #1
        response = c.send("DISCONNECT");
        if (response.equals("DISCONNECT: OK")) {
            System.out.println("test 7 succeeded");
        } else {
            System.out.println("test 7 failed");
        }

        // test #8 - connect and then send invalid message
        c = new Client("localhost", 7);
        try {
            response = c.send("invalid message from client");
            if (response != null) {
                System.out.println("test 8 failed - recieved response and shouldn't have.");
                System.out.println(response);
            } else {
                System.out.println("test 8 succeeded");
            }
        } catch (Exception e) {
            System.out.println("exception.");
        }

        // test #9 - include spaces in clientID
        c = new Client("localhost", 7);
        response = c.send("CONNECT Test Client 123");
        if (response.equals("CONNECT: OK")) {
            System.out.println("test 9 succeeded");
        } else {
            System.out.println("test 9 failed");
        }

        // test #10 - send a null clientID (on windows this is sending as a clientId which is just "\r")

        c = new Client("localhost", 7);
        response = c.send("CONNECT ");
        if (response != null) {
            // should be connect: error
            System.out.println("test 10 failed");
        } else {
            System.out.println("test 10 passed");
        }

        // test suite for concurrent clients
        concurrencyTests();
    }
}