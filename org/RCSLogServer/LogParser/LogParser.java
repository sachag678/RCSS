/** LogParser.java in the package org.RCSLogServer.LogParser of the RCSLogServer project.
	Originally created 4-Apr-08
    
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

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.reflect.Method;


/**
 * Class definition of the LogParser.
 *
 * This class creates an object that parses RoboCup Simulation log
 * file lines, one at a time. Parsing a line returns a ParsedLine
 * object and sends parsing events to registered parsing event 
 * listeners (preferred).
 *
 * This class also defines a number of static Regex Patterns that can
 * be used without creating an instance of this class.
 *
 * @author Edgar Acosta
 * @since 0.2
 *
 */
public class LogParser
{
    private ParsedLine parsed;
    private int cycle;
    private ArrayList<ParsingEventListener> PEListeners;
    private String lastType;

    /** LogParser constructor
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public LogParser()
    {
	parsed = new ParsedLine();
	cycle=-1;
	PEListeners = new ArrayList<ParsingEventListener>();
	lastType="sensor";
    }

    /** Registers a new ParsingEventListener
     *
     * @param pel the Parsing Event Listener
     *
     * @author Edgar Acosta
     * @since 0.3
     *
     */
    public void addPEListener(ParsingEventListener pel)
    {
	PEListeners.add(pel);
    }

    /** Removes a ParsingEventListener
     *
     * @param pel the Parsing Event Listener
     *
     * @author Edgar Acosta
     * @since 0.3
     *
     */
    public void removePEListener(ParsingEventListener pel)
    {
	PEListeners.remove(pel);
    }

    /** Parses a log line
     * 
     * This parser casts ParsingEvents to the ParsingEventListeners
     *
     * @param line a String containing a line of the log file.
     * @return a ParsedLine.
     * @throws IOException when the line cannot be parsed
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    public ParsedLine parse(String line)
    throws IOException
    {
	parsed.reset(cycle); //clear the content of the parsed
	//recognize the type of message: control(command) or sensor
	m=line_pattern.matcher(line);
	String message,params,type;
	if(m.matches()){ // if it is not a simple control
	    message=m.group(1);
	    params=m.group(2);
	    if(options.matcher(message).matches())
		parseOptions(message,params);//it is an options message
	    else if(init.matcher(message).matches())
		parseInit(message,params);//it is an init message
	    else if(controls.matcher(message).matches())
		parseControl(message,params); // it is a control message
	    else if(sensors.matcher(message).matches())
		parseSensor(message,params); // it is a sensor message
	    else if(errors.matcher(message).matches())
		parseError(message,params); // it is a sensor message
	    else
		throw new IOException("Not recognized message:\n"+line);
	} else{ //it should be a simple control: sense_body, score, or bye
	    n=simple_control_pattern.matcher(line);
	    if(!n.matches()){
		throw new IOException("Not recognized simple control message:\n"+line);
	    }
	    message=n.group(1);
	    if(simple_controls.matcher(message).matches()){
		if(lastType.compareTo("sensor") == 0){
		    //if this is the first control in the row
		    ParsingEvent peSwitch=new ParsingEvent(this);
		    for(ParsingEventListener pel:PEListeners) pel.Controling(peSwitch);
		}
		lastType="control";
		parsed.setControl(message);
		ParsingEvent pe=new ParsingEvent(this,parsed);
		for(ParsingEventListener pel:PEListeners) pel.SimpleControl(pe);
	    }
	    else
		throw new IOException("Not a simple control:\n"+line);
	}

	//fill the parsed
	return parsed;  //return the parsed
    }

    /** Parses a initialization message
     *
     * @param message the init message
     * @param params a string containing the parameters of this message
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    private void parseInit(String message, String params)
    { //parse an initialization command
	//System.out.println("DEBUG: init message");
	parsed.setCycle(-1);
	cycle=-1;
	n=token_pattern.matcher(params); //this obtains each first level token
	ArrayList<String> parms= new ArrayList<String>();
	//String[] parms= new String[];
	//int parmCounter=0;
	while(n.find()) {// while there are tokens
	    for(int i=1;i<=n.groupCount();i++){
		String tmp;
		if((tmp = n.group(i)) != null)
		    parms.add(tmp);
		//System.out.println("DEBUG: "+tmp);
		//parms[parmCounter++]=tmp;//save the token
	    }
	}
	n=p_lr.matcher(parms.get(0));
	if(n.matches()){ //if the first param is the team side
	    //then this is the init server message
	    if(lastType.compareTo("control") == 0){
		//if this is the first control in the row
		ParsingEvent peSwitch=new ParsingEvent(this);
		for(ParsingEventListener pel:PEListeners) pel.Sensing(peSwitch);
	    }
	    lastType="control";
	    parsed.setSensor(message);
	    parsed.setParam("team_side",parms.get(0));
	    if(message.compareTo("init") == 0){ //if it is the "init" message
		parsed.setParam("player_number",parms.get(1));
		parsed.setParam("play_mode",parms.get(2));
	    } else { // otherwise it is the "reconnect" message
		parsed.setParam("play_mode",parms.get(1));
	    }
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.ServerInit(pe);
	} else{ //otherwise it is the client init control command
	    if(lastType.compareTo("sensor") == 0){
		//if this is the first control in the row
		ParsingEvent peSwitch=new ParsingEvent(this);
		for(ParsingEventListener pel:PEListeners) pel.Controling(peSwitch);
	    }
	    lastType="control";
	    parsed.setControl(message);
	    parsed.setParam("team_name",p_quote.matcher(parms.get(0)).replaceAll(""));
	    //System.out.println("DEBUG 2: "+parms.get(0));
	    if(message.compareTo("init") == 0){ //if it is the "init" message
		parsed.setParam("goalie","0");
		for(int i=1; i<parms.size();i++){
		    o=simple_control_pattern.matcher(parms.get(i));
		    p=var_value_p.matcher(parms.get(i));
		    //System.out.println("DEBUG 3: "+parms.get(i));
		    if(o.matches()) {// if it is the goalie flag
			if(o.group(1).compareTo("goalie") == 0)
			    parsed.setParam("goalie","1");
		    }
		    else if(p.matches()){
			if(p.group(1).compareTo("version") == 0)
			    parsed.setParam("protocol_version",p.group(2));
		    }
		}
	    } else { // otherwise it is the "reconnect" message
		parsed.setParam("player_number",parms.get(1));
	    }
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.ClientInit(pe);
	}
    }

    /** Parses a control command
     *
     * @param message the control action
     * @param params a string containing the parameters of this message
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    private void parseControl(String message, String params)
    { //parse a control command
	if(lastType.compareTo("sensor") == 0){
	    //if this is the first control in the row
	    ParsingEvent peSwitch=new ParsingEvent(this);
	    for(ParsingEventListener pel:PEListeners) pel.Controling(peSwitch);
	}
	lastType="control";
	parsed.setControl(message);
	n=token_pattern.matcher(params); //this obtains each token
	ArrayList<String> parms= new ArrayList<String>();
	while(n.find()) {// while there are tokens
	    for(int i=1;i<=n.groupCount();i++){
		String tmp;
		if((tmp = n.group(i)) != null)
		    parms.add(tmp);
		    //parms[parmCounter++]=tmp;//save the token
	    }
	}
	if(message.compareTo("catch") == 0){ //if it is the "catch" command
	    parsed.setParam("direction",parms.get(0));
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.Catch(pe);
	} else if(message.compareTo("change_view") == 0){ //if it is the "change_view" commadn
	    parsed.setParam("change_view_w",parms.get(0));
	    parsed.setParam("change_view_q",parms.get(1));
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.ChangeView(pe);
	} else if(message.compareTo("dash") == 0){ //if it is the "dash" command
	    parsed.setParam("power",parms.get(0));
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.Dash(pe);
	} else if(message.compareTo("kick") == 0){ //if it is the "kick" command
	    parsed.setParam("power",parms.get(0));
	    parsed.setParam("direction",parms.get(1));
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.Kick(pe);
	} else if(message.compareTo("move") == 0){ //if it is the "move" command
	    parsed.setParam("x",parms.get(0));
	    parsed.setParam("y",parms.get(1));
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.Move(pe);
	} else if(message.compareTo("say") == 0){ //if it is the "say" command
	    parsed.setParam("say",parms.get(0));
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.Say(pe);
	} else if(message.compareTo("turn") == 0){ //if it is the "turn" command
	    parsed.setParam("direction",parms.get(0));
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.Turn(pe);
	} else if(message.compareTo("turn_neck") == 0){ //if it is the "turn_neck" command
	    parsed.setParam("neck_angle",parms.get(0));
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.TurnNeck(pe);
	} else {/*other control messages: attentionto*/
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.OtherControl(pe);
	}

    }

    /** Parses a sensorial information message
     *
     * @param message the sensor action
     * @param params a string containing the parameters of this message
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    private void parseSensor(String message, String params)
    throws IOException
    { //parse a sensor message
	if(lastType.compareTo("control") == 0){
	    //if this is the first sensor in the row
	    ParsingEvent peSwitch=new ParsingEvent(this);
	    for(ParsingEventListener pel:PEListeners) pel.Sensing(peSwitch);
	}
	lastType="sensor";
	parsed.setSensor(message);
	n=sensor_params_p.matcher(params);
	if(!n.matches())
	    if(message.compareTo("ok") !=0)
		throw new IOException("No time on sensor message:\n"+message+" "+params);
	    else
		return;
	cycle=Integer.parseInt(n.group(1));
	String info=n.group(2);
	parsed.setCycle(cycle);
	if(message.compareTo("hear") == 0){ //if it is a "hear" sensor
	    n=hear_pattern.matcher(info); //this obtains each token
	    ArrayList<String> parms=new ArrayList<String>();
	    while(n.find()) {// while there are tokens
		for(int i=1;i<=n.groupCount();i++){
		    String tmp;
		    if((tmp = n.group(i)) != null)
			parms.add(tmp);//save the token
		}
	    }
	    parsed.setParam("who",parms.get(0));
	    parsed.setParam("what",parms.get(1));
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.Hear(pe);
	} else if(message.compareTo("score") == 0){ //if it is a "score" sensor
	    n=token_pattern.matcher(info); //this obtains each token
	    ArrayList<String> parms=new ArrayList<String>();
	    while(n.find()) {// while there are tokens
		for(int i=1;i<=n.groupCount();i++){
		    String tmp;
		    if((tmp = n.group(i)) != null)
			parms.add(tmp);//save the token
		}
	    }
	    parsed.setParam("our_score",parms.get(0));
	    parsed.setParam("their_score",parms.get(1));
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.Score(pe);
	} else if(message.compareTo("see") == 0){ //if it is a "see" sensor
	    String[] objects=split_objects_p.split(info);
	    int objectsCounter=0;
	    ParsingEvent pePre=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.SeeReceived(pePre);
	    for(int i=0;i<objects.length;i++){
		ParsedLine parsedObject=new ParsedLine();
		n=objects_p.matcher(objects[i]);
		if(n.matches()){
		    objectsCounter++;
		    String objectNumber=Integer.toString(objectsCounter);
		    String objectName=n.group(1);
		    String objectInfo=n.group(2);
		    //get the name parts
		    String[] nameParts=sep_p.split(objectName,2);
		    parsed.setParam("type_"+objectNumber,nameParts[0]);
		    parsedObject.setParam("type",nameParts[0]);
		    if(nameParts.length > 1){
			if(p_player.matcher(nameParts[0]).matches()){
			    String[] playerParts=sep_p.split(nameParts[1]);
			    switch(playerParts.length){
			    case 3: if(playerParts[2].compareTo("goalie") == 0){
				parsed.setParam("goalie_"+objectNumber,"1");
				parsedObject.setParam("goalie","1");
			    }
			    case 2: parsed.setParam("player_number_"+objectNumber,playerParts[1]);
				parsedObject.setParam("player_number",playerParts[1]);
			    default:Matcher m_quote=p_quote.matcher(playerParts[0]);
				parsed.setParam("team_"+objectNumber,m_quote.replaceAll(""));
				parsedObject.setParam("team",m_quote.replaceAll(""));
			    }
			} else if(p_flag.matcher(nameParts[0]).matches()){ //if it is a flag
			    String[] flagTags=sep_p.split(nameParts[1]);
			    switch(flagTags.length){
			    case 1: //it is just the "c"
				parsedObject.setParam("hp","c");
				parsedObject.setParam("vp","c");
				break;
			    case 2:
				if(flagTags[0].compareTo("c") == 0){
				    parsedObject.setParam("hp","c");
				    parsedObject.setParam("vp",flagTags[1]);
				} else if(p_lr.matcher(flagTags[0]).matches()){ //if it is l or r
				    parsedObject.setParam("hp",flagTags[0]);
				    if(p_tb.matcher(flagTags[1]).matches()){ //if the second token is t or b
					parsedObject.setParam("vp",flagTags[1]);
				    } else { //the second token is a zero
					parsedObject.setParam("vp","c");
					parsedObject.setParam("flag_number","0");
				    }
				} else { //it is t or b followed by a zero
				    parsedObject.setParam("hp",flagTags[0]);
				    parsedObject.setParam("vp","c");
				    parsedObject.setParam("flag_number","0");
				}
				break;
			    case 3:
				if(p_pg.matcher(flagTags[0]).matches()){ //if it is in a box
				    parsedObject.setParam("Box",flagTags[0]);
				    parsedObject.setParam("hp",flagTags[1]);
				    parsedObject.setParam("vp",flagTags[2]);
// 				} else if(p_lr.matcher(flagTags[0]).matches()){ //if it is l or r
// 				    parsedObject.setParam("hp",flagTags[0]);
// 				    parsedObject.setParam("vp",flagTags[1]);
// 				    parsedObject.setParam("flag_number",flagTags[2]);
				} else { //it is t or b followed by l or r and a number
				    parsedObject.setParam("hp",flagTags[0]);
				    parsedObject.setParam("vp",flagTags[1]);
				    parsedObject.setParam("flag_number",flagTags[2]);
				}
			    }
			}
			else
			    parsed.setParam("name_"+objectNumber,nameParts[1]);
			    parsedObject.setParam("name",nameParts[1]);
		    }
		    //get the info parts
		    String[] infoParts=sep_p.split(objectInfo);
		    switch(infoParts.length){
		    case 6: parsed.setParam("head_dir_"+objectNumber,infoParts[5]);
			parsedObject.setParam("head_dir",infoParts[5]);
		    case 5: parsed.setParam("body_dir_"+objectNumber,infoParts[4]);
			parsedObject.setParam("body_dir",infoParts[4]);
		    case 4: parsed.setParam("dir_change_"+objectNumber,infoParts[3]);
			parsedObject.setParam("dir_change",infoParts[3]);
		    case 3: parsed.setParam("dist_change_"+objectNumber,infoParts[2]);
			parsedObject.setParam("dist_change",infoParts[2]);
		    case 2: parsed.setParam("direction_"+objectNumber,infoParts[1]);
			parsedObject.setParam("direction",infoParts[1]);
			parsed.setParam("distance_"+objectNumber,infoParts[0]);
			parsedObject.setParam("distance",infoParts[0]);
			break;
		    default: parsed.setParam("direction_"+objectNumber,infoParts[0]);
			parsedObject.setParam("direction",infoParts[0]);
			break;
		    }
		    //cast the corresponding event
		    ParsingEvent peObj=new ParsingEvent(this,parsedObject);
		    if(p_player.matcher(nameParts[0]).matches())
			for(ParsingEventListener pel:PEListeners) pel.Player(peObj);
		    if(p_ball.matcher(nameParts[0]).matches())
			for(ParsingEventListener pel:PEListeners) pel.Ball(peObj);
		    if(p_flag.matcher(nameParts[0]).matches())
			for(ParsingEventListener pel:PEListeners) pel.Flag(peObj);
		    if(p_line.matcher(nameParts[0]).matches())
			for(ParsingEventListener pel:PEListeners) pel.Line(peObj);
		    if(p_goal.matcher(nameParts[0]).matches())
			for(ParsingEventListener pel:PEListeners) pel.Goal(peObj);
		} else {}
	    }
	    parsed.setParam("objects",Integer.toString(objectsCounter)); //number of seen objects
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.SeeParsed(pe);

	} else if(message.compareTo("sense_body") == 0){ //if it is a "sense_body" sensor
	    String[] attr=split_objects_p.split(info); //this may fail because the sense_body has more attributes than specified. Hopefully, it won't matter
	    for(int i=0; i<attr.length; i++) {
		String[] splitted=sep_p.split(attr[i]);
		if(splitted[0].compareTo("view_mode") == 0){ //
		    parsed.setParam("view_q",splitted[1]);
		    parsed.setParam("view_w",splitted[2]);
		} else if(splitted[0].compareTo("stamina") == 0){ //
		    parsed.setParam("stamina",splitted[1]);
		    parsed.setParam("effort",splitted[2]);
		} else if(splitted[0].compareTo("speed") == 0){ //
		    parsed.setParam("speed",splitted[1]);
		    parsed.setParam("direction",splitted[2]);
		} else if(splitted[0].compareTo("head_angle") == 0){ //
		    parsed.setParam("head_direction",splitted[1]);
		} else {//ignore anything else
		}
	    }
	    ParsingEvent pe=new ParsingEvent(this,parsed);
	    for(ParsingEventListener pel:PEListeners) pel.SenseBody(pe);
	} else {}
    }

    /** Parses match options sent by the server
     *
     * @param message the option message
     * @param params a string containing the parameters of this message
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    private void parseOptions(String message, String params)
    { //parse an initialization info
	parsed.setSensor(message);
	parsed.setParam("params",params);
	parsed.setCycle(-1);
	ParsingEvent pe=new ParsingEvent(this,parsed);
	for(ParsingEventListener pel:PEListeners) pel.ServerParams(pe);
    }

    /** Parses errors sent by the server
     *
     * @param message the error message
     * @param params a string containing the parameters of this message
     *
     * @author Edgar Acosta
     * @since 0.2
     *
     */
    private void parseError(String message, String params)
    { //parse an error
	parsed.setSensor(message);
	parsed.setParam("error",params);
	parsed.setCycle(-2);
	ParsingEvent pe=new ParsingEvent(this,parsed);
	for(ParsingEventListener pel:PEListeners) pel.Error(pe);
    }

    //===========================================================================
    // Dictionaries
    // TODO document these
    public static Pattern controls = Pattern.compile("^(?:dash|kick|turn|move|catch|say|change_view|turn_neck|attentionto|ear|clang|change_player_type)$");
    public static Pattern simple_controls= Pattern.compile("^(?:sense_body|score|bye)$");
    public static Pattern sensors = Pattern.compile("^(?:hear|see|sense_body|score|ok)$");
    public static Pattern init = Pattern.compile("^(?:init|reconnect)$");
    public static Pattern options = Pattern.compile("^(?:server_param|player_param|player_type)$");
    public static Pattern errors = Pattern.compile("^(?:error|warning)$");


    //===========================================================================
    // Patterns
    public static Pattern line_pattern = Pattern.compile("^\\((\\w+?)\\s+(.*)\\)$"); //(message other_info)
    public static Pattern simple_control_pattern = Pattern.compile("^\\((\\w+?)\\)$"); //(mesage)
    public static Pattern token_pattern = Pattern.compile("\\s*([\\w\\.-]+)\\b\\s*|.*?((?:\\([\\.\\(\\w-].*?[\\.\\w\\)-]\\))+?)"); //token|(token token_info+)
    public static Pattern hear_pattern = Pattern.compile("\\s*([^\\s]+)\\b"); //token
    public static Pattern var_value_p = Pattern.compile("^\\s*\\(([\\w-]+)\\s+([\\w\\.]+)\\)\\s*$"); //(var value)
    public static Pattern sensor_params_p = Pattern.compile("\\s*(\\d+)\\s*(.*)"); //time info
    public static Pattern split_objects_p = Pattern.compile("^\\(|\\)\\s+\\(|\\)$"); //(|) (|)
    public static Pattern objects_p = Pattern.compile("\\((.*?)\\)\\s+(.*)"); //(Object_Name) Object_Info
    public static Pattern sep_p = Pattern.compile("\\s");
    public static final int p_flags = Pattern.CASE_INSENSITIVE;
    public static Pattern p_player = Pattern.compile("^(player|p)$",p_flags);
    public static Pattern p_ball = Pattern.compile("^(ball|b)$",p_flags);
    public static Pattern p_goal = Pattern.compile("^(goal|g)$",p_flags);
    public static Pattern p_flag = Pattern.compile("^(flag|f)$",p_flags);
    public static Pattern p_line = Pattern.compile("^(line|l)$",p_flags);
    public static Pattern p_quote = Pattern.compile("\"");
//     public static Pattern p_type = Pattern.compile("^(p|g)$");
//     public static Pattern p_number = Pattern.compile("^\\d{2}$");
    public static Pattern p_lr = Pattern.compile("^(l|r)$");
    public static Pattern p_tb = Pattern.compile("^(t|b)$");
    public static Pattern p_pg = Pattern.compile("^(p|g)$");
//     public static Pattern p_lrc = Pattern.compile("^(l|r|c)$");
    //===========================================================================
    // Other private members
    Matcher m,n,o,p;
}
