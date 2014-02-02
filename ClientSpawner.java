/**
 * Created with IntelliJ IDEA.
 * User: Frank
 * Date: 2/1/14
 * Time: 2:58 PM
 *
 * Spawn any number of clients provided by the user at command-line menu.
 * For the purpose of stress testing.
 *
 * Default operation: ClientSpawner creates 1 client connection to a server.
 */

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSpawner implements Runnable {

    final private int opcode;                  // operation code for processing server-side command.
    final private String server;               // the host we connect to.
    public final Thread clientThread;

    private volatile long elapsedTime;         // timekeeping for mean response time.


    // constructor for ClientSpawner class.
    public ClientSpawner (short menuopt, String servername) {

      this.server = servername;
      this.opcode = menuopt;

        clientThread = new Thread(this);
        clientThread.start();

    }



    // run a new thread as many times as needed.
    public void run() {

        try {

            Socket socket = new Socket(server, 5200);

            ObjectInputStream inputFromServer = new ObjectInputStream(socket.getInputStream());     // inbound.

            DataOutputStream outputFromClient = new DataOutputStream(socket.getOutputStream()); // outbound.

            long startTime = System.currentTimeMillis();

            outputFromClient.writeByte(opcode);
            /*
             send out the operation code (opcode field) to the server. the server interprets the opcode
             to run the server-side command, and the output on the server side is sent back as an Object.

             the Object must be Serializable for this to work. this, in conjunction with StringBuilder on the
             server side, should give the client some sensible program output.

              */

             String programOutput = (String) inputFromServer.readObject(); // receive the program output as an object.
                                                                           // and cast as String.


            long stopTime = System.currentTimeMillis();

            elapsedTime = stopTime - startTime;

            System.out.println(programOutput);


            socket.close();

            System.out.println("Host finished procedure.");

        } catch (UnknownHostException e) {
            System.err.println("Unknown host name or unresolved host.");

        } catch (IOException e) {

            System.err.println("Bad IO or host cannot be reached. Check the hostname and try again.");
            System.exit(1);

        }  catch (ClassNotFoundException e) {

            System.err.println("Java class for receiving program output not found on this computer.");
            e.printStackTrace();
            System.exit(2);
        }




    } // end thread

    public long getElapsedTime() {

        return elapsedTime;
    }



}
