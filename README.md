client-server
=============
This is a classroom project. It is not meant to be used in the real world.

This Java command-line application is a client which sends a byte to a server and 
receives a String object. Then, the client application prints the String object 
contents to the terminal window.


To run, do:
	java ClientController your-host-here
	
The application requires one argument, the hostname. Otherwise, it will fail to run.
It also connects on port 15000 only at this time. I may change this later to allow 
any port to be used.




The application is very simple. The workhorse is the ClientSpawner class which 
is driven by its ClientController class. 


Presently, the protocol is (from a description in source file):

	1. Each client thread will send a string to
    the server.
 
	2. The server receives the string formatted UTF16-BE.
 
	3. The server should run this string as-is.
 
	4. Its output from the program is sent to the client as a UTF16-BE
	string.

	5. The client application shows the transmitted program output to
    the user.





