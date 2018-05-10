/** ParsingEventListner.java in the package org.RCSLogServer.LogParser of the RCSLogServer project.
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

 * 
 */

package org.RCSLogServer.LogParser;

//import java.io.*;
//import java.util.*;


/** Interface for ParsingEventListener.
 *
 * This interface is for listeners of Parsing Events, which process
 * the incoming events.
 *
 * @author Edgar Acosta
 * @since 0.3
 *
 */
public interface ParsingEventListener {
    //Server messages

    /** Server Initialization message
     *
     * This method is called when a server initialization message has been parsed.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void ServerInit(ParsingEvent pe);

    /** Hear message
     *
     * This method is called when a hear message has been parsed.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void Hear(ParsingEvent pe);

    /** Score message
     *
     * This method is called when a server score message has been parsed.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void Score(ParsingEvent pe);

    /** See Received
     *
     * This method is called when a server initialization message has been received.
     *
     * @param pe The Parsing Event doesn't contain more information.
     *
     * @author Edgar Acosta
     *
     */
    public void SeeReceived(ParsingEvent pe);

    /** See Parsed
     *
     * This method is called when a see message has been parsed.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void SeeParsed(ParsingEvent pe);

    /** Sense Body message
     *
     * This method is called when a sense body message has been parsed.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void SenseBody(ParsingEvent pe);

    /** Server Error message
     *
     * This method is called when a server error message has been parsed.
     *
     * @param pe The Parsing Event contains the message.
     *
     * @author Edgar Acosta
     *
     */
    public void Error(ParsingEvent pe);

    /** Server Params message
     *
     * This method is called when a server params message has been parsed.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void ServerParams(ParsingEvent pe);

    /** Agent Control event
     *
     * This method is called when the client starts sending control messages.
     *
     * @param pe The Parsing Event doesn't contain further information.
     *
     * @author Edgar Acosta
     *
     */
    public void Controling(ParsingEvent pe);

    /** Agent Sensing event
     *
     * This method is called when the client starts receiving sensor messages.
     *
     * @param pe The Parsing Event doesn't contain further information.
     *
     * @author Edgar Acosta
     *
     */
    public void Sensing(ParsingEvent pe);

       //Objects in the field
    //public void TeamMate(ParsingEvent pe);
    //public void Opponent(ParsingEvent pe);

    /** Player seen
     *
     * This method is called when a player is sensed.
     *
     * @param pe The Parsing Event contains the available information about the player.
     *
     * @author Edgar Acosta
     *
     */
    public void Player(ParsingEvent pe);

    /** Ball seen
     *
     * This method is called when the ball is sensed.
     *
     * @param pe The Parsing Event contains the available information about the ball.
     *
     * @author Edgar Acosta
     *
     */
    public void Ball(ParsingEvent pe);

    /** Flag seen
     *
     * This method is called when a flag has been sensed.
     *
     * @param pe The Parsing Event contains the available information about the flag.
     *
     * @author Edgar Acosta
     *
     */
    public void Flag(ParsingEvent pe);

    /** Line seen
     *
     * This method is called when a line is sensed
     *
     * @param pe The Parsing Event contains the available information about the line.
     *
     * @author Edgar Acosta
     *
     */
    public void Line(ParsingEvent pe);

    /** Goal seen
     *
     * This method is called when a goal is sensed.
     *
     * @param pe The Parsing Event contains the available information about the goal.
     *
     * @author Edgar Acosta
     *
     */
    public void Goal(ParsingEvent pe);

    //Client messages

    /** Client Initialization message
     *
     * This method is called when a client initialization message has been parsed.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void ClientInit(ParsingEvent pe);

    /** Catch control
     *
     * This method is called when the client sends a catch message.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void Catch(ParsingEvent pe);

    /** Change View control
     *
     * This method is called when the client sends a change view message.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void ChangeView(ParsingEvent pe);

    /** Dash control
     *
     * This method is called when the client sends a dash message.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void Dash(ParsingEvent pe);

    /** Kick control
     *
     * This method is called when the client sends a kick message.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void Kick(ParsingEvent pe);

    /** Move control
     *
     * This method is called when the client sends a move message.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void Move(ParsingEvent pe);

    /** Say control
     *
     * This method is called when the client sends a say message.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void Say(ParsingEvent pe);

    /** Turn control
     *
     * This method is called when the client sends a turn message.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void Turn(ParsingEvent pe);

    /** Turn neck control
     *
     * This method is called when the client sends a turn neck message.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void TurnNeck(ParsingEvent pe);

    /** Simple control
     *
     * This method is called when the client sends a bye, score or sense_body message.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void SimpleControl(ParsingEvent pe); //sense_body, score, bye

    /** Other control
     *
     * This method is called when the client sends an unrecognized message.
     *
     * @param pe The Parsing Event contains a parsed version of the message.
     *
     * @author Edgar Acosta
     *
     */
    public void OtherControl(ParsingEvent pe);


}
