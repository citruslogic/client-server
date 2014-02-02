client-server
=============

This Java command-line application is a client which sends a byte to a server and 
receives a String object. Then, the client application prints the String object 
contents to the terminal window.


To run, do:
	java ClientController your-host-here
	
The application requires one argument, the hostname. Otherwise, it will fail to run.




The application is very simple. The workhorse is the ClientSpawner class which 
is driven by its ClientController class. 


Presently, the protocol is (from a description in source file):

    1. Each client thread will send a byte to
    	 the server.

    2. The server receives the byte, treated as an operation code.

    3. The server should interpret this operation code as a command to be
       executed on its system.

    4. Its output from the program (that is to be run according to the
        op-code) is sent back as an Object, treated as a String object
        by the client application.


 note: the client application should show the transmitted program output to
 the user neatly.


