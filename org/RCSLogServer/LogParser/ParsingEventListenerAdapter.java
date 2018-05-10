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

/**
 * Adapter for ParsingEventListeners.
 *
 * This adapter contains all the methods for listeners of Parsing
 * Events as indicated on the ParsingEventListener interface.
 *
 * Use it as a frame for building a ParsingEventListeners.
 *
 * @author Edgar Acosta
 * @since 0.3
 *
 */
public class ParsingEventListenerAdapter implements ParsingEventListener
{
    //Server messages
    public void ServerInit(ParsingEvent pe){}
    public void Hear(ParsingEvent pe){}
    public void Score(ParsingEvent pe){}
    public void SeeReceived(ParsingEvent pe){}
    public void SeeParsed(ParsingEvent pe){}
    public void SenseBody(ParsingEvent pe){}
    public void Error(ParsingEvent pe){}
    public void ServerParams(ParsingEvent pe){}
    public void Controling(ParsingEvent pe){}
    public void Sensing(ParsingEvent pe){}

       //Objects in the field
    //public void TeamMate(ParsingEvent pe){}
    //public void Opponent(ParsingEvent pe){}
    public void Player(ParsingEvent pe){}
    public void Ball(ParsingEvent pe){}
    public void Flag(ParsingEvent pe){}
    public void Line(ParsingEvent pe){}
    public void Goal(ParsingEvent pe){}

    //Client messages
    public void ClientInit(ParsingEvent pe){}
    public void Catch(ParsingEvent pe){}
    public void ChangeView(ParsingEvent pe){}
    public void Dash(ParsingEvent pe){}
    public void Kick(ParsingEvent pe){}
    public void Move(ParsingEvent pe){}
    public void Say(ParsingEvent pe){}
    public void Turn(ParsingEvent pe){}
    public void TurnNeck(ParsingEvent pe){}
    public void SimpleControl(ParsingEvent pe){} //sense_body, score, bye
    public void OtherControl(ParsingEvent pe){}
}
