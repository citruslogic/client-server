/**
 * Created with IntelliJ IDEA.
 * User: Frank Butler
 * Date: 2/1/14
 * Time: 2:58 PM
 *
 *  ClientController is just a basic client application for a server.
 *  The server is to run as many as 6 different utility programs and
 *  to deliver their outputs to their clients.
 *
 *  The protocol is very simple. ClientSpawner is used here to make as
 *   many clients as desired.
 *
 *   1. Each client thread will send a string to
 *   the server.
 *
 *   2. The server receives the string formatted UTF16-BE.
 *
 *   3. The server should run this string as-is.
 *
 *   4. Its output from the program is sent to the client as a UTF16-BE
 *   string.
 *
 *   5. The client application shows the transmitted program output to
 *      the user.
 */
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.DateFormat;

class ClientController {




	public static void main(String[] args) throws InterruptedException, IOException {

        byte moption;           // menu option
        short nclients;         // number of clients
        String cmdString;       // the command string to send to the server
        String server;          // the server address



        if (args.length < 1) {

            System.err.println("fatal: no server address given as argument. Quitting..");
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

                cmdString = makeCommand(moption);   // make the complete command string to send to the server later.

                // store the clients in an array.
                ClientSpawner[] theClientSpawners = new ClientSpawner[nclients];



                // fill the array with spawners. remember to send the complete string, with arguments.
                for (int i = 0; i < nclients; i++)
                    theClientSpawners[i] = new ClientSpawner(cmdString, server);

                // wait for all of the threads to finish before returning to the menu.
                System.out.println("Connections are setting up...");

                for (ClientSpawner clients : theClientSpawners) {

                        clients.clientThread.join();


                }

                /*
                 *
                 * Store millisec for threads in file or keep internal? */
                writeBenchmarkFile(theClientSpawners, nclients);



            } // end while


    } // end main

    public static long computeMeanTime(ClientSpawner[] theClientSpawners, short nclients) {

        long averageElapsedTime = 0;

        for (ClientSpawner clients : theClientSpawners)
            averageElapsedTime += clients.getElapsedTime();

        return averageElapsedTime / nclients;

        }   // end computeMeanTime


    public static void writeBenchmarkFile(ClientSpawner[] cs, short nc) throws IOException {
        /* writes to a text file the time in ms. in other words, the time elapsed for the
           threads in ClientSpawners to complete their tasks.
         */
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        PrintWriter benchmark = new PrintWriter(new BufferedWriter(new FileWriter("benchmark.txt", true)));
        benchmark.println(dateFormat.format(cal.getTime()) + "[" + nc + "]"
                + "\tThread time (ms): "
                + "\t"
                + computeMeanTime(cs, nc)
                + "\n\n");

        benchmark.close();

    } // end writeBenchmarkFile

    static String makeCommand(byte stcmd) throws IOException {

        String cmdname, args;


        switch (stcmd) {

            case 6:
                cmdname = "ps";
                break;
            case 5:
                cmdname = "who";
                break;
            case 4:
                cmdname = "netstat";
                break;
            case 3:
                cmdname = "free";
                break;
            case 2:
                cmdname = "uptime";
                break;
            case 1:
                cmdname = "date";
                break;
            default:
                throw new IllegalArgumentException("Not an accepted command.");


        }  // end switch on command

        System.out.println("Add any extra arguments for " + cmdname + ": ");

        Scanner getArgs = new Scanner(System.in);

        // get the list of arguments from the user.
        args = getArgs.nextLine();


        return stcmd + " " + args;
    } // end makeCommand


} // end ClientController class


