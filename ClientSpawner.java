/**
 * Created with IntelliJ IDEA.
 * User: Frank Butler
 * Date: 2/1/14
 * Time: 2:58 PM
 *
 * Spawn any number of clients provided by the user at command-line menu.
 * For the purpose of stress testing.
 *
 * Default operation: ClientSpawner creates 1 client connection to a server.
 *
 * The thread also keeps track of its own time for the comparison paper.
 */

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSpawner implements Runnable {

    private String cmdString;                  // string for the server to use as command + args
    final private String server;               // the host we connect to.
    public final Thread clientThread;

    private volatile long elapsedTime;         // timekeeping for mean response time.


    // constructor for ClientSpawner class.
    public ClientSpawner (String cmd, String servername) {

        this.server = servername;
        this.cmdString = cmd;

        clientThread = new Thread(this);
        clientThread.start();

    }



    // run a new thread as many times as needed.
    public void run() {

        try (Socket socket = new Socket(server, 15000)) {



            // Data*Stream objects expect UTF-16BE
            DataInputStream inputFromServer = new DataInputStream(socket.getInputStream());     // inbound.

            DataOutputStream outputFromClient = new DataOutputStream(socket.getOutputStream()); // outbound.

            long startTime = System.currentTimeMillis();



            /* we're not using bytes as operation codes anymore. instead, i'm sending a string to the
               server.

              */

            System.out.println("Sending command to be executed on host...");
            sendString(outputFromClient, cmdString);


            String programOutput = readString(inputFromServer); // receive the program output from server.
            System.out.println("Finished receiving program output.");

            System.out.println();
            printProgramOutput(programOutput);  // print the program output we received from server
                                                // to the user's screen.

            long stopTime = System.currentTimeMillis();

            elapsedTime = stopTime - startTime;



            System.out.println("Host finished task.");

        } catch (UnknownHostException e) {
            System.err.println("Unknown host name or unresolved host: " + server);

        } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);

        }




    } // end thread

    public long getElapsedTime() {

        return elapsedTime;
    }


    String readString(DataInputStream in) throws IOException {

        int length = in.readInt();

        byte[] bytes = new byte[length];

        in.readFully(bytes);

        // return a string with encoding for Big Endian, which DIS uses.
        return new String(bytes, "UTF-16BE");
    }   // end readString


    void sendString(DataOutputStream out, String st) throws IOException {

        // if the string length is more than half the value of the largest
        // integer, it is not a good string. toss it out.
        if (st.length() > Integer.MAX_VALUE / 2)
            throw new IllegalArgumentException("String is too long!");

        out.writeInt(st.length() * 2);
        System.out.println("client wrote length: " + st.length() * 2);
        out.writeChars(st);
        out.flush();
    }  // end sendString

    void printProgramOutput(String st) {

        System.out.println(st);
    }  // end printProgramOutput

}
