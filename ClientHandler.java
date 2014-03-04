import java.io.*;
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
            try (
                    DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                    DataOutputStream outputFromServer = new DataOutputStream(socket.getOutputStream())
                    ) {


                // service clients
                while (true) {

                    String inputChars = readChars(inputFromClient);


                    System.out.println("The string sent to me was " + inputChars + " from "
                            + "client " + clientNo);
                    System.out.println();


                    // send back the command output.
                    writeString(outputFromServer, getProcessOutput(inputChars));


                }  // end service to clients
            }
        } catch (IOException e) {
            e.printStackTrace();

        } // end try-with-resources


    }  // end thread


    /**
     *  Read the byte length of the string first and then read in the bytes.
     *  Completes with bounds checking.
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String readChars(DataInputStream inputStream) throws IOException {

        int length = inputStream.readInt();
        System.out.println("readInt has: " + length);
        byte[] bytes = new byte[length];

        inputStream.readFully(bytes);

        // return a string with encoding for Big Endian, which DIS uses.
        return new String(bytes, "UTF-16BE");

    }   // end readString

    /**
     *  Write the string's length as an integer to the outbound socket.
     *
     * @param outputStream
     * @param string
     * @throws IOException
     */
    private void writeString(DataOutputStream outputStream, String string) throws IOException {

        if (string.length() > Integer.MAX_VALUE / 2)
            throw new IllegalArgumentException("String is too long!");

        outputStream.writeInt(string.length() * 2);
        outputStream.writeChars(string);
        outputStream.flush();
    } // end writeString


    /** Execute the command given by the string sent to the server.
     * and return the command output to the calling method.
     *
     * @param inString
     * @return
     * @throws IOException
     */
    private String getProcessOutput(String inString) throws IOException {


        char[] chars = new char[32768];

        // ProcessBuilder needs an array like "ls", "-al", etc..
        ProcessBuilder processBuilder = new ProcessBuilder(inString.split("\\s+"));
        Process pr = processBuilder.start();


        StringBuilder commandOutput = new StringBuilder();
        Reader reader = new InputStreamReader(pr.getInputStream());


        while (true) {

            int length;
            try {

                length = reader.read(chars);

                if (length == -1)
                    break;

            } catch (EOFException e) {
                break;
            }


            commandOutput.append(chars, 0, length);

        }

        // turn the command output into a string to be sent back to the client.
        reader.close();
        return commandOutput.toString();
    }  // end getProcessOutput

}   // end class ClientHandler