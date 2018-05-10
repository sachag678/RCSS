/** ParsingEvent.java in the package org.RCSLogServer.LogParser of the RCSLogServer project.
    Originally created 2-Oct-08
    
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

 **/


package org.RCSLogServer.LogParser;

import java.util.EventObject;

/** Class definition of Parsing Events 
 * 
 * This class defines ParsingEvent objects. These events occuer when
 * the LogParser is parsing a Log file.
 *
 * @author Edgar Acosta
 * @since 0.3
 *
 */

public class ParsingEvent extends EventObject
{
    private ParsedLine line;

    /** ParsingEvent constructor
     *
     * @param source the object producing the Event
     *
     * @author Edgar Acosta
     *
     */
    public ParsingEvent(Object source)
    {
	super(source);
    }

    /** ParsingEvent constructor
     *
     * @param source the object producing the Event
     * @param pl a ParsedLine object containing the parsed line or a
     * representation of soccer objects.
     *
     * @author Edgar Acosta
     *
     */
    public ParsingEvent(Object source,ParsedLine pl)
    {
	super(source);
	line=pl;
    }

    /** Checks whether the ParsedLine enclosed contains a value for
     * certain key.
     *
     * It is used to test for information about the event.
     *
     * @param paramName the key name
     * @return true if there is a value associated with the key.
     *
     * @author Edgar Acosta
     *
     */
    public boolean contains(String paramName){
	return line.contains(paramName);
    }

    /** Gets the value associated with a given key in the ParsedLine
     * enclosed.
     *
     * It is used to get information about the event.
     *
     * @param paramName the key name
     * @return the value associated with the key.
     *
     * @author Edgar Acosta
     *
     */
    public String get(String paramName)
    {
	return line.getValue(paramName);
    }
}
