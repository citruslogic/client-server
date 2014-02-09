import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class Server {

   public static void main(String[] args) throws IOException {

    try (ServerSocket server = new ServerSocket(15000)) {

        int clientNo = 1;

        System.out.println("Starting server: listening on non-privileged port 15000");
        System.out.println();



        while (true) {
            Socket socket = server.accept();  // ready to accept new connections.



            System.out.println("Starting thread for client " + clientNo + " at "
            + new Date() + '\n');

            // get client's host name and address
            InetAddress inetaddress = socket.getInetAddress();
            System.out.println("Client " + clientNo + "'s hostname is "
                    + inetaddress.getHostName() + "\n");
            System.out.println("Client " + clientNo + "'s IP address is "
                    + inetaddress.getHostAddress() + "\n");

            // start a new thread for the incoming connection
            ClientHandler task = new ClientHandler(socket, clientNo);

            // start the thread now
            new Thread(task).start();

            // increment # of clients
            clientNo++;


        } // end while
    } // end try-with-resources
   } // end main
} // end Server class
