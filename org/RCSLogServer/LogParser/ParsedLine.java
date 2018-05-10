/** LogParser.java in the package org.RCSLogServer.LogParser of the RCSLogServer project.
	Originally created 16-Apr-08
    
    Copyright (C) 2008  Edgar Acosta

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


package org.RCSLogServer.LogParser;

import java.util.*;


/**  This class is a container for a RoboCup Simulation agent log line.
 *
 * @author Edgar Acosta
 * @since 0.2
 *
 */
public class ParsedLine
{
    //types: message, command
    protected String type, subtype;

    protected HashMap<String,String> parameters;

    protected int cycle;

    /** ParsedLine constructor
     *
     * 
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public ParsedLine()
    {
	type="";
	subtype="";
	cycle=-1;
	parameters=new HashMap<String,String>();
    }

    /** ParsedLine constructor
     *
     * @param c the cycle number on this log line 
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public ParsedLine(int c)
    {
	type="";
	subtype="";
	cycle=c;
	parameters=new HashMap<String,String>();
    }

    /** ParsedLine constructor
     *
     * @param c the cycle number (game time) on this log line 
     * @param t the message type (sensor, control, error)
     * @param s the main message (i.e. first word in the log line)
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public ParsedLine(int c, String t, String s)
    {
	type=t;
	subtype=s;
	cycle=c;
	parameters=new HashMap<String,String>();
    }

    /** Resets the object
     *
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public void reset()
    {
	type="";
	subtype="";
	cycle=-1;
	parameters.clear();
    }

    /** Resets the object
     *
     * @param c the game time on this log line 
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public void reset(int c)
    {
	type="";
	subtype="";
	cycle=c;
	parameters.clear();
    }
    /** Sets the type of message
     *
     * @param t the message type (sensor, control, error)
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public void setType(String t)
    {
	type=t;
    }

    /** Returns the type of message
     *
     * @return the message type (sensor, control, error)
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public String getType()
    {
	return type;
    }

    /** Sets the type of message to "sensor"
     *
     * @param message main message (see, sense_body, etc.)
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public void setSensor(String message)
    {
	type="sensor";
	subtype=message;
    }

    /** Sets the type of message to "control"
     *
     * @param message main message (dash, turn, etc.)
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public void setControl(String message)
    {
	type="control";
	subtype=message;
    }

    /** Sets the main message
     *
     * @param s main message (see, dash, turn, etc.)
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public void setSubtype(String s)
    {
	subtype=s;
    }

    /** Returns the main message
     *
     * @return the main message (see, dash, turn, etc.)
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public String getSubtype()
    {
	return subtype;
    }

    /** Sets a message parameter value
     *
     * @param p the parameter
     * @param v the value
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public void setParam(String p, String v)
    {
	parameters.put(p,v);
    }

    /** Returns a message parameter value
     *
     * @param p the parameter
     * @return the parameter value
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public String getValue(String p)
    {
	return parameters.get(p);
    }

    /** Checks if the parsed message contains a parameter value
     *
     * @param p the parameter
     * @return true if there is a value for this parameter, false otherwise
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public boolean contains(String p)
    {
	return parameters.containsKey(p);
    }

    /** Checks if the parsed message has parameters
     *
     * @return true if there are parameter, false otherwise
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public boolean hasParams()
    {
	return !parameters.isEmpty();
    }

    /** Returns the number of parameters in the parsed message
     *
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public int nParams()
    {
	return parameters.size();
    }

    /** Returns the set of parameters in the parsed message
     *
     * @return a Set<String> contaning the set of parameters of the parsed message
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public Set<String> params()
    {
	return parameters.keySet();
    }

    /** Sets the game time
     *
     * @param c the game time
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public void setCycle(int c)
    {
	cycle=c;
    }

    /** Returns the game time
     *
     * @return the game time
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public int getCycle()
    {
	return cycle;
    }
}
