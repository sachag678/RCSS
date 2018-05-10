/** RCSLogServer.java in the package org.RCSLogServer of the RCSLogServer project.
	Originally created 6-Jul-08
    
    Copyright (C) 2008  Network Management and Artificial Intelligence Lab, Carleton University

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

 * 
 */


package org.RCSLogServer;

import java.io.*;
import java.net.*;
import java.util.Vector;

/**
 * Executable class for the RCSLogServer package. Can be given command
 * line parameters, all which are optional.
 *
 * Note: This package was previously distributed as LogServer, and was
 * created by Paul Marlow (Oct. 2001.) The current distribution was
 * initiated by Edgar Acosta (Jul. 2008.)
 *
 * @param -s_host Soccer Server host address, defaults to localhost
 * @param -s_port the port number the Soccer server is listening, defaults to 6000
 * @param -c_port the port number this Log Server listens, defaults to 7000
 *
 * @author NMAI Lab
 * @since 0.2
 *
 */
public class RCSLogServer
{
    //===========================================================================
    // Private members
    private InetAddress s_host;  // Server host address
    private int s_init_port = 6000;  // Initialization port of Soccer Server
    private int s_port = 0;  // Server port number for communication
    private int c_port = 0;  // Client port number for communication
    private Vector<Thread> communication_list; // List of Server and Client Communications
    private DatagramSocket listen_socket;
    
    /** The executable method
     *
     * @throws IOException 
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public static void main(String a[]) throws IOException
    {
	String s_hostName = new String("localhost");
	int s_portNum = 6000;
	int c_portNum = 7000;

	try
	    {                                                                                
		// First look for parameters
		for( int c = 0 ; c < a.length ; c += 2 )
		    {
			if( a[c].compareTo("-s_host") == 0 )
			    {
				s_hostName = a[c+1];
			    }
			else if( a[c].compareTo("-s_port") == 0 )
			    {
				s_portNum = Integer.parseInt(a[c+1]);
			    }
			else if ( a[c].compareTo("-c_port") == 0 )
			    {
				c_portNum = Integer.parseInt(a[c+1]);
			    }
			else
			    {
				throw new Exception();
			    }
		    }
	    }
	catch(Exception e)
	    {
		System.err.println("");
		System.err.println("USAGE: java RCSLogServer [-parameter value]");
		System.err.println("");
		System.err.println("    Parameters  value        default");
		System.err.println("   ------------------------------------");
		System.err.println("    s_host      host_name    localhost");
		System.err.println("    s_port      port_number  6000");
		System.err.println("    c_port      port_number  7000");
		System.err.println("");
		System.err.println("    Example:");
		System.err.println("      java RCSLogServer -s_host 127.0.0.1 -s_port 6000 -c_port 7000");
		return;
	    }

	System.out.println("Initializing RCSLogServer ...");
	RCSLogServer logserver = new RCSLogServer(InetAddress.getByName(s_hostName), s_portNum, c_portNum);

	// enter main loop
	logserver.mainLoop();                                               
    }  

    /** RCSLogServer class constructor method
     *
     * @param host the Soccer Server host address
     * @param port the Soccer Server port
     * @param cPort this Log Server port
     *
     * @author NMAI Lab
     * @since 0.2
     */
    public RCSLogServer(InetAddress host, int port, int cPort)
    {
	s_host = host;
	s_init_port = port;
	communication_list = new Vector<Thread>();
	try {
	    listen_socket = new DatagramSocket(cPort);
	} catch (SocketException se) {
	    System.err.println("Unable to initialize socket connection on port "+Integer.toString(cPort)+"...aborting.");
	    System.exit(0);
	}
    }
																 
    /** RCSLogServer destructor method
     *  For future use, it is empty.
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public void finalize()
    {
    }

    /** Main Loop of the Log Server
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    protected void mainLoop()
    {
	LogMemory log_memory;

	ClientServerComm clientserver = null;
	ServerClientComm serverclient = null;

	// Start a communcation thread to for initialization between client and server
	System.out.println("Starting RCSLogServer ...");

	// Press ENTER key to stop
	System.out.println("\nAt any time, press ENTER key to stop.\n");
	boolean done = false;

	while (!done) {
	    try {
		log_memory = new LogMemory(s_host, s_init_port);
		serverclient = new ServerClientComm(log_memory);
		clientserver = new ClientServerComm(listen_socket, log_memory);

		// Loop until first socket connection has been made, and client port
		// has been determined in order to allow communication to the client
		System.out.println("Awaiting connection ... ");
		while (!log_memory.checkClient() && !done) {
		    Thread.sleep(1000);
		    done = (System.in.available() > 0);
		}

		communication_list.add(clientserver);
		communication_list.add(serverclient);

		while (!log_memory.checkServer() && !done) {
		    Thread.sleep(1000);
		    done = (System.in.available() > 0);
		}
	
		if (!done)
		    {
			System.out.println("Logging initiated for: " + log_memory.getPlayerName() + " -> " + log_memory.getFilename());
		    }
	    }
	    catch (IOException ioe) {
		System.err.println("Input error: " + ioe);
	    }
	    catch (InterruptedException inte) {
	  	System.err.println("Thread Interruption: inte");
	    }
	}

	System.out.println("Initiating stop of communication threads.");
	ClientServerComm temp_clientserver = null;
	ServerClientComm temp_serverclient = null;
	for (int i = 0; i < communication_list.size(); i+=2) {
	    temp_clientserver = (ClientServerComm)(communication_list.elementAt(i));
	    temp_serverclient = (ServerClientComm)(communication_list.elementAt(i+1));
	    temp_clientserver.initiateStop();
	    temp_serverclient.initiateStop();
	}
	System.exit(0);
    }

}
