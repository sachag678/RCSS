/** Logger.java in the package org.RCSLogServer of the RCSLogServer project.
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
import java.util.LinkedList;


/** Writes the log file
 *
 * @author NMAI Lab
 * @since 0.2
 *
 */
class Logger
{
    //===========================================================================
    // Private members
    private LinkedList logrecords;  // Records to log after game
    private String logfile;  // log filename
    private OutputStream fout = null;
    private OutputStream bout = null;
    private OutputStreamWriter out = null;

    /** constructor method
     * 
     * Prepares the log file
     *
     * @param filename the name of the file to be written
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public Logger(String filename)
    {
	logrecords = new LinkedList();
	try {
	    fout = new FileOutputStream(filename);
	    bout = new BufferedOutputStream(fout);
	    out = new OutputStreamWriter(bout);
	}
	catch (Exception e) {
	    System.err.println("Error creating file: " + e);
	}

	logfile = filename;
    }
																 
    /** destructor method
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public void finalize()
    {
	try {
	    out.flush();
	    out.close();
	}
	catch (Exception e) {
	    System.err.println("Unable to close log file: " + e);
	}
    }

    /** Writes a record in the log file
     *
     * @param record a String containing the record
     * 
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized void addRecord(String record)
    {
	if (record != null) {
	    //      logrecords.addLast(record);
	    try {
		out.write((record.trim() + '\n'));
		out.flush();
	    }
	    catch (Exception e) {
		System.err.println("Error writing to file.");
	    }
	}
    }

    /** Returns the log filename
     *
     * @return the log filename
     *
     * @author NMAI Lab
     * @since 0.2
     *
     */
    public synchronized String getFilename()
    {
	return logfile;
    }
}
