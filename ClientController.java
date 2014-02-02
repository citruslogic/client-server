import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.DateFormat;



/* ClientController is just a basic client application for a server.
   The server is to run as many as 6 different utility programs and
   to deliver their outputs to their clients.

   The protocol is very simple. ClientSpawner is used here to make as
    many clients as desired.

    1. Each client thread will send a byte to
    the server.

    2. The server receives the byte, treated as an operation code.

    3. The server should interpret this operation code as a command to be
       executed on its system.

    4. Its output from the program (that is to be run according to the
        op-code) is sent back as an Object, treated as a String object
        by the client application.

    5. The client application shows the transmitted program output to
       the user.
  */

class ClientController {




	public static void main(String[] args) throws InterruptedException, IOException {

        byte moption;
        short nclients;



        String server;

        // try to get the server address from the command line.
        if (args.length < 1) {

            System.err.println("No server address given as argument. Quitting..");
            System.exit(1);
        }

        server = args[0];



	    // get user menu selection.

        Scanner kb = new Scanner(System.in);

                while (true) {


				
				System.out.println("Enter your selection. \n"
                    + "=============================\n"
				    + "1. Host Current Date and Time\n"
				    + "2. Host uptime\n"
                    + "3. Host memory use\n"
                    + "4. Host Netstat\n"
                    + "5. Host current users\n"
                    + "6. Host running processes\n"
				    + "7. Quit\n"
				    + "\n");




                // grab the first byte for the menu option.
                try {


                     moption = kb.nextByte();

                    // user wants to break the loop.
                    if (moption == 7) {

                       System.out.println("quitting..");
                       System.exit(0);
                    }

                    // menu options that are out of bounds.
                    if (moption > 7 || moption < 1) {
                        System.out.println("\nMenu option is out of bounds. Try again with" +
                                " any menu option, 1 through 7.\n");
                        continue;
                    }

                } catch (InputMismatchException e) {

                    // the user should try again.
                    System.out.println("\nInput is invalid! Try again with a single, numeric" +
                            " menu option instead.\n");
                    continue;
                }


			
				System.out.print("Number of clients to spawn? ");

                 // get the number of clients to spawn for testing.
                 try {
                     nclients = kb.nextShort();

                 } catch (InputMismatchException e) {

                     // the user should try again from the menu.
                     System.err.println("\nNot integer input! Try again with a number instead.\n");
                     continue;
                 }

                // store the clients in an array.
                ClientSpawner[] theClientSpawners = new ClientSpawner[nclients];



                // fill the array with spawners.
                for (int i = 0; i < nclients; i++)
                    theClientSpawners[i] = new ClientSpawner(moption, server);

                // wait for all of the threads to finish before returning to the menu.
                System.out.println("Connections are setting up...");

                for (ClientSpawner clients : theClientSpawners) {

                        clients.clientThread.join();


                }

                /*
                 *
                 * Store millisec for threads in file or keep internal? */


                DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
                Calendar cal = Calendar.getInstance();

                PrintWriter benchmark = new PrintWriter(new BufferedWriter(new FileWriter("benchmark.txt", true)));
                benchmark.println(dateFormat.format(cal.getTime()) + "[" + nclients + "]"
                         + "\tThread time (ms): "
                         + "\t"
                         + computeMeanTime(theClientSpawners, nclients)
                         + "\n\n");

                benchmark.close();

            } // end while

    } // end main

    public static long computeMeanTime(ClientSpawner[] theClientSpawners, short nclients) {

        long averageElapsedTime = 0;

        for (ClientSpawner clients : theClientSpawners)
            averageElapsedTime += clients.getElapsedTime();

        return averageElapsedTime / nclients;

        }   // end computeMeanTime



}


