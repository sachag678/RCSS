/** ServerClientComm.java in the package org.RCSLogServer of the RCSLogServer project.
    Originally created 23-Oct-01
    
    Copyright (C) 2001  Paul Marlow

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
import java.util.*;

/** Establishes the communication with the Soccer Server
 *
 * @author NMAI Lab
 * @since 0.2
 *
 */
class ServerClientComm extends Thread
{

    // Private members
    private LogMemory log_memory;
    private boolean stopthread = false;


    /** constructor method
     *
     * @param memory the object storing the data stream
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public ServerClientComm(LogMemory memory)
    {
	log_memory = memory;
	start();
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

    /** This thread stop initialization
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized void initiateStop()
    {
	stopthread = true;
    }

    /** Stop this thread
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized boolean stopThread()
    {
	return stopthread;
    }

    /** Used to run the Soccer Server side communications
     *
     * @Override
     *
     * @author NMAI Lab
     * @since 0.2
     */
    public void run()
    {
	byte[] buffer = new byte[log_memory.getPacketSize()];
	DatagramPacket packet = new DatagramPacket(buffer, log_memory.getPacketSize());

	try {
	    log_memory.server_socket.receive(packet);
	} catch (IOException e) {
	    System.err.println("Socket receiving error " + e);
	}

	// Route packet to actual destination
	String record = (new String(buffer)).trim();

	// Packet received - check to see if server and/or client port has been
	// initialized, and intialize if it has not been
	log_memory.setServerHost(packet.getAddress());
	log_memory.setServerPort(packet.getPort());
	log_memory.parseServerInit(record);

	send(record);
	//log_memory.log.addRecord(record);

	// Continually loop receiving/sending messages until thread is stopped
	while (!stopThread()) {
	    record = receive();
	    send(record);
	    log_memory.log.addRecord(record);
	}
    }

    /** Sends via socket message to the Soccer Server
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    private void send(String message)
    {
	byte[] buffer = new byte[log_memory.getPacketSize()];
	buffer=Arrays.copyOf(message.getBytes(),log_memory.getPacketSize());
	DatagramPacket packet = new DatagramPacket(buffer, log_memory.getPacketSize(), log_memory.client_host, log_memory.client_port);
	try {
	    log_memory.client_socket.send(packet);
	} catch (IOException e) {
	    System.err.println("Socket sending error " + e);
	}
    }

    /** Waits for new messages from the Soccer client
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    private String receive() 
    {
	byte[] buffer = new byte[log_memory.getPacketSize()];
	DatagramPacket packet = new DatagramPacket(buffer, log_memory.getPacketSize());

	try {
	    log_memory.server_socket.receive(packet);
	} catch (IOException e) {
	    System.err.println("Socket receiving error " + e);
	}

	return (new String(buffer)).trim();
    }
}
