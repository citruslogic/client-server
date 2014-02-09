import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket; // our connected socket.
    private volatile int clientNo; // our client number to identify.

    // constructor
    public ClientHandler(Socket socket, int client) {
        this.clientNo = client;
        this.socket = socket;

    }  // end ClientHandler constructor

    public void run()  {


         // duplicate the client's input and output streams
        try {
            try (DataInputStream inputFromClient = new DataInputStream(socket.getInputStream())) {
                DataOutputStream outputFromServer = new DataOutputStream(socket.getOutputStream());

                // service clients
                while (true) {

                    String inputChars = readChars(inputFromClient);


                    System.out.println("The string sent to me was " + inputChars + " from "
                            + "client " + clientNo);
                    System.out.println();


                    // just send it back. we don't care to execute this command for the time being.
                    writeString(outputFromServer, inputChars);

                    socket.close();
                }  // end service to clients
            }
        } catch (IOException e) {
            System.err.println(e);

        } // end try-with-resources


    }  // end thread




    private String readChars(DataInputStream inputStream) throws IOException {

        int length = inputStream.readInt();

        byte[] bytes = new byte[length];

        inputStream.readFully(bytes);

        // return a string with encoding for Big Endian, which DIS uses.
        return new String(bytes, "UTF-16BE");

    }   // end readString


    private void writeString(DataOutputStream outputStream, String string) throws IOException {

        if (string.length() > Integer.MAX_VALUE / 2)
            throw new IllegalArgumentException("String is too long!");

        outputStream.writeInt(string.length() * 2);
        outputStream.writeChars(string);

    } // end writeString

}   // end class ClientHandler