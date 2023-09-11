/**
 * @(#)LoadCheck.java
 *
 *
 * @author
 * @version 1.00 2008/5/30
 */
package ch.epfl.javanco.remote;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.base.Javanco;



public class LoadCheck {

	public  static  boolean loadCheckIsUsed;
	public static  int  maxsession;
	public static int maxNodes;

	//holds the total number of nodes
	public static int totalNodes = 0;
	//holds the total number of current sessions
	public static int  sessions = 0;
	//holds the total number of links
	public static int totalLinks = 0;

	//sessionNodes holds the number of nodes used by each session.
	public static HashMap<String, Integer> sessionNodes;
	//sessionNodes holds the maximum permited number of nodes for eachsession.
	public static HashMap<String, Integer> sessionNodesMaximum;
	//sessionNodes holds the number of links used by each session.
	public static HashMap<String, Integer> sessionLinks;

	private static final Logger logger = new Logger(LoadCheck.class);
	public static LoadCheck checker ;

	public static 	LoadCheck getLoadChacker(){
		if(checker == null ) {
			checker = new LoadCheck();
		}
		return checker;
	}

	public LoadCheck(){
		loadCheckIsUsed = Boolean.parseBoolean(Javanco.getProperty("ch.epfl.javanco.remote.useLoadCheckClass"));
		maxsession = Integer.parseInt(Javanco.getProperty("ch.epfl.javanco.remote.maximalConcurrentSessions"));
		maxNodes =Integer.parseInt(Javanco.getProperty("ch.epfl.javanco.remote.maximalnodeNumberperSession"));
		sessionNodes = new HashMap<String, Integer>();
		sessionNodesMaximum = new HashMap<String, Integer>();
		sessionLinks = new HashMap<String, Integer>();
	}

	/**@param  sessionId  the Session ID
	 *Informs the LoadCheck class of the coming new session with id sessionID.
	 *increments the sessions variable, add the new session in the sessionNodes HashMap.
	 **/
	public  static  void initNewSession(String sessionID){
		LoadCheck.sessions ++;
		LoadCheck.newMaxNodes();
		LoadCheck.updateNodeMax(LoadCheck.maxNodes);
		LoadCheck.initNodes(sessionID);
		LoadCheck.initLinks(sessionID);
		logger.debug("new session added with id : " +sessionID);
	}
	/**
	 *@param  sessionId  the Session ID
	 *@return true if the new session with id sessionId is accepted in the system,false otherwise
	 **/
	public static boolean canSessionAdded(String sessionId){
		int test= 0;
		if (LoadCheck.sessions < maxsession ){
			LoadCheck.evolution(LoadCheck.sessions + 1,test); // check if we can accept new session!
			if(test <= computeGapLoad()) {
				return true;   // there is enough space to accet the new session
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 *@param  sessionId  the Session ID
	 *@return  true if the new node request from session with id sessionId is accepted in the system, false otherwise
	 **/
	public static boolean nodeAdded(String sessionId){
		if(LoadCheck.sessionNodes.get(sessionId) != null) {
			if (Integer.parseInt(LoadCheck.sessionNodes.get(sessionId).toString())< Integer.parseInt(sessionNodesMaximum.get(sessionId).toString())  ) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	/**@param  sessionId  the Session ID
	 *@return true if the new link request from session with id sessionId is accepted in the system, false otherwise
	 **/
	public static boolean linksAdded(String sessionId){
		if(LoadCheck.sessionLinks.get(sessionId)!= null) {
			if (Integer.parseInt(LoadCheck.sessionLinks.get(sessionId).toString())< conputeMaxLinks( Integer.parseInt( LoadCheck.sessionNodes.get(sessionId).toString()) )) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	/**
	 *@param  sessionId  the Session ID
	 *Add the new node with id sessionId in the sessionNodes HasMap.
	 **/
	public static void initNodes(String sessionId){
		LoadCheck.sessionNodes.put(sessionId,0);
		LoadCheck.sessionNodesMaximum.put(sessionId,LoadCheck.maxNodes);
		logger.debug("new nodes added for session : " +sessionId);
	}

	/**
	 *@param  sessionId  the Session ID
	 *Add the new link with id sessionId in the sessionLinks HashMap.
	 **/
	public static void initLinks(String sessionId){
		LoadCheck.sessionLinks.put(sessionId,0);
		logger.debug("new link added for session : " +sessionId);
	}

	/**
	 *@param  sessionId  the Session ID
	 *Checks if the  the requets for a new node creation has been accepted.
	 **/
	public static void checkNodesCreation(String res, String sessionId){
		if(res.substring(0,13).equals("<root><point>")){
			int myNodes = Integer.parseInt(LoadCheck.sessionNodes.get(sessionId).toString());
			myNodes++;
			LoadCheck.sessionNodes.put(sessionId,myNodes);
			if(LoadCheck.sessionNodesMaximum.get(sessionId) == null) {
				LoadCheck.sessionNodesMaximum.put(sessionId,LoadCheck.maxNodes);
			}
			LoadCheck.totalNodes ++;
		}
	}

	/**
	 *@param  sessionId  the Session ID
	 *@param  res  message to be checked.
	 *Checks if the  the requets for a new link creation has been accepted.
	 **/
	public static void checkLinksCreation(String res,String sessionId){
		if(res.substring(0,12).equals("<root><link>")){
			int myLinks = Integer.parseInt(LoadCheck.sessionLinks.get(sessionId).toString()) ;
			LoadCheck.totalLinks++;
			LoadCheck.sessionLinks.put(sessionId,myLinks);
			LoadCheck.totalLinks ++;
		}
	}
	/**
	 *@param  sessionId  the Session ID
	 *@param  res  message to be checked.
	 *Checks if the  the requets for removal of a node has been accepted.
	 **/
	public static  void checkNodesRemoval(String res,String sessionId){
		if(res.equals("<root><status>1</status></root>")){
			int myNodes = Integer.parseInt(LoadCheck.sessionNodes.get(sessionId).toString()) ;
			if(myNodes !=0) {
				myNodes--;
			}
			LoadCheck.sessionNodes.put(sessionId,myNodes);
			if(LoadCheck.totalNodes !=0) {
				LoadCheck.totalNodes --;
			}
			logger.debug("node removed for session : " +sessionId);
		}
	}

	/**
	 *@param  sessionId  the Session ID
	 *@param  res  message to be checked.
	 *Checks if the  the requets for removal of a link has been accepted.
	 **/
	public static  void checkLinksRemoval(String res,String sessionId){
		if(res.equals("<root><status>1</status></root>")){
			int myLinks =Integer.parseInt( LoadCheck.sessionLinks.get(sessionId).toString());
			if(myLinks !=0) {
				myLinks--;
			}
			LoadCheck.sessionLinks.put(sessionId,myLinks);
			if(LoadCheck.totalLinks !=0) {
				LoadCheck.totalLinks --;
			}
			logger.debug("link removed for session : " +sessionId);
		}
	}

	/**
	 *@param  sessionId  the Session ID
	 *remove a session from the LoadCheck class.
	 **/
	public  static synchronized  void removeSession(String sessionId){
		if(LoadCheck.sessions !=0) {
			sessions --;
		}
		LoadCheck.totalNodes = LoadCheck.totalNodes - Integer.parseInt(LoadCheck.sessionNodes.get(sessionId).toString()) ;
		LoadCheck.totalLinks = LoadCheck.totalLinks   - Integer.parseInt(LoadCheck.sessionLinks.get(sessionId).toString()) ;
		LoadCheck.sessionNodes.remove(sessionId) ;
		LoadCheck.sessionNodesMaximum.remove(sessionId);
		LoadCheck.sessionLinks.remove(sessionId) ;
		logger.debug("session removed with id  : " +sessionId);
	}

	/**
	 *Update the maximun number of nodes for every session according to the new maximum.
	 *If the number of node created by a session is larger than the new Maximun that session will not
	 *update its maximun otherwise its new max. value is set to the new maximum.
	 *and it can no longueur create nodes  more than its own maximum.
	 **/
	public static void  updateNodeMax(int newNodeMax){
		Set<String> setKey = LoadCheck.sessionNodesMaximum.keySet();
		Iterator<String> it = setKey.iterator();
		while(it.hasNext()){
			String key = it.next();
			int lastSn = Integer.parseInt(LoadCheck.sessionNodes.get(key).toString()) ;
			if(lastSn < newNodeMax){
				LoadCheck.sessionNodesMaximum.put(key,newNodeMax);
				logger.debug("New maximum for teh number of nodes");
			}

		}
	}

	/**
	 *return the  difference between the fixed maximum number of nodes of the system and the current number of nodes.
	 **/
	private static  int computeGapLoad(){
		int maxLoad = 0;
		int actLoad = 0;
		Set<String> setKey = LoadCheck.sessionNodesMaximum.keySet();
		Iterator<String> it = setKey.iterator();
		while(it.hasNext()){
			String key = it.next();
			maxLoad = maxLoad + Integer.parseInt(LoadCheck.sessionNodesMaximum.get(key).toString()) ;
			actLoad = actLoad + Integer.parseInt(LoadCheck.sessionNodes.get(key).toString());
		}
		return (maxLoad - actLoad);
	}


	public  static void evolution(int sessions, int maxN){
		if(LoadCheck.sessions < 5) {
			maxN = 100;
		}
		if(LoadCheck.sessions >= 5 && LoadCheck.sessions < 20) {
			maxN = 60;
		}
		if(LoadCheck.sessions >= 20 && LoadCheck.sessions < 40) {
			maxN = 40;
		}
		if(LoadCheck.sessions >= 40 && LoadCheck.sessions < 100) {
			maxN = 30;
		}
		if(LoadCheck.sessions >= 1000 && LoadCheck.sessions < 200) {
			maxN = 20;
		}
	}

	/**
	 *
	 **/
	public  static void newMaxNodes(){
		if(LoadCheck.sessions < 5) {
			LoadCheck.maxNodes = 100;
		}
		if(LoadCheck.sessions >= 5 && LoadCheck.sessions < 20) {
			LoadCheck.maxNodes = 60;
		}
		if(LoadCheck.sessions >= 20 && LoadCheck.sessions < 40) {
			LoadCheck.maxNodes = 40;
		}
		if(LoadCheck.sessions >= 40 && LoadCheck.sessions < 100) {
			LoadCheck.maxNodes = 30;
		}
		if(LoadCheck.sessions >= 1000 && LoadCheck.sessions < 200) {
			LoadCheck.maxNodes = 20;
		}
	}

	/**
	 *return the maximum number of links given the number of nodes
	 **/
	private static  int  conputeMaxLinks(int maxNodes){
		if(maxNodes == 2) {
			return 1;
		}
		if(maxNodes < 2) {
			return 0;
		}
		int fac1 = myFactorial(maxNodes);
		int fac2 = myFactorial(maxNodes - 2);
		int res = fac1/(2*fac2);
		return res;
	}

	private static int myFactorial(int integer)
	{
		if( integer == 1) {
			return 1;
		} else
		{
			return(integer*(myFactorial(integer-1)));
		}
	}

}