/** LogMemory.java in the package org.RCSLogServer of the RCSLogServer project.
    Originally created 23-Oct-01
    
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
import java.util.regex.*;

/** LogMemory class receives the data streams and send them to the Log File
 *
 * @author NMAI Lab
 * @since 0.2
 *
 */
class LogMemory
{
    //===========================================================================
    // Private members
    public InetAddress server_host;  // Server host address
    public InetAddress client_host;  // Client host address
    public int server_port = 0;  // Server port number for communication
    public int client_port = 0;  // Client port number for communication
    public DatagramSocket server_socket;  // Socket for communicating with Server
    public DatagramSocket client_socket;  // Socket for communicating with Client
    public Logger log;  // Log writing
    private String logfile; 
    private String playername; // Name of the player - teamname + player num
    private String teamname; // Name of the team the player plays for
    private String playernum; // Uniform number of the player
    private String clientInitString;
    private String serverInitString;
    private int packet_size;
    private static Pattern init_pattern = Pattern.compile("^\\(init\\s+(\\w+)\\s*(.*)\\)$"); //(init first_arg other_info)

    /** Constructor method. Opens socket for connection with Soccer Server
     *
     * @param s_host the address of the Soccer Server host
     * @param s_port the port number of the Soccer Server
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
	public LogMemory(InetAddress s_host, int s_port)
    {
	server_host = s_host;
	server_port = s_port;
	playername = "";
	teamname = "";
	playernum = "";
	logfile = "";
	client_port = 0;
	client_host = null;
	log = null;
	packet_size = 4096;
	try {
	    server_socket = new DatagramSocket();
	    client_socket = new DatagramSocket();
	} catch (SocketException se) {
	    System.err.println("Unable to create sockets for communication: " + se);
	    System.exit(0);
	}
    }
																 
    /** destructor method
     *
     * @Overrides
     *
     */
    public void finalize()
    {
    }

    /** Checks for the Soccer Client connection
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized boolean checkClient()
    {
	return (client_port > 0);
    }

    /** Sets the Soccer Client connection port
     *
     * @param port the port number for the client connection
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized void setClientPort(int port)
    {
	client_port = port;
    }

    /** Sets the Soccer Client connection host
     *
     * @param host the address of the client
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized void setClientHost(InetAddress host)
    {
	client_host = host;
    }

    /** Returns the Soccer Client connection port
     *
     * @return a port number
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized int getClientPort()
    {
	return client_port;
    }

    /** Returns the Soccer Client connection host
     *
     * @return an address
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized InetAddress getClientHost()
    {
	return client_host;
    }

    /** Checks for the Soccer Server connection
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized boolean checkServer()
    {
	return (server_port != 6000);
    }

    /** Sets the Soccer Server connection port
     *
     * @param port the port number for the server connection
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized void setServerPort(int port)
    {
	server_port = port;
    }

    /** Sets the Soccer Server connection host
     *
     * @param host the address of the client
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized void setServerHost(InetAddress host)
    {
	server_host = host;
    }

    /** Returns the Soccer Server connection port
     *
     * @return a port number
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized int getServerPort()
    {
	return server_port;
    }

    /** Returns the Soccer Client connection host
     *
     * @return an address
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized InetAddress getServerHost()
    {
	return server_host;
    }

    /** Sets this log player number
     *
     * @param p_num the player number
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized void setPlayerNumber(String p_num)
    {
	playernum = p_num;
	playername = teamname + "_" + playernum;
	createOutputFile();
    }

    /** Sets this log player team name
     *
     * @param t_name the player team name
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized void setTeamname(String t_name)
    {
	teamname = t_name;
	playername = teamname + "_" + playernum;
	createOutputFile();
    }

    /** Returns this log player name
     *
     * @return the player team name and player number
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized String getPlayerName()
    {
	return playername;
    }

    /** Returns the socket packet size
     *
     * @return the socket packet size
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized int getPacketSize()
    {
	return packet_size;
    }

    /** Sets the socket packet size
     *
     * @param p_size the socket packet size
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized void setPacketSize(int p_size)
    {
	packet_size = p_size;
    }

    /** Parse init commands from client to obtain Team name
     *
     * @param init a string containing a initialization message
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized void parseClientInit(String init)
    {
	double version_number = 3.00;
	Matcher m=init_pattern.matcher(init);
	if(m.matches()){
	    clientInitString=init;
	    setTeamname(m.group(1));
	    if(m.groupCount()>1){
		Pattern version_pattern = Pattern.compile("\\(version\\s+([\\w\\.]+)\\)"); //(version value)
		Matcher n=version_pattern.matcher(m.group(2));
		if(n.find())
		    version_number=Double.parseDouble(n.group(1));
		// Depending on the version number, the packet message size must be changed.
		if (version_number >= 6.00)
		    setPacketSize(4096);
		else 
		    setPacketSize(1024);
	    }
	}
    }

    /** Parse init commands from server to obtain Player Number (Uniform Number).
     *
     * @param init a string containing a initialization message
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized void parseServerInit(String init)
    {
	Matcher m=init_pattern.matcher(init);
	if(m.matches()){
	    serverInitString=init;
	    if(m.groupCount()>1){
		Pattern team_pattern = Pattern.compile("^\\s*(\\w+?)\\s+.*$"); //(teamName other_info);
		Matcher n=team_pattern.matcher(m.group(2));
		if(n.matches())
		    setPlayerNumber(n.group(1));
	    }
	}
    }

    /** Returns the log file name.
     *
     * @return the log file name.
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public String getFilename()
    {
	return logfile;
    }

    /** Creates the log file (Logger)
     *
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    private void createOutputFile()
    {
	if (teamname.compareTo("") != 0 && playernum.compareTo("") != 0 && log == null) {
	    logfile = playername + ".lsf";
	    log = new Logger(logfile);
	    log.addRecord(clientInitString); //insert the initialization messages
	    log.addRecord(serverInitString);
	}
    }
}
