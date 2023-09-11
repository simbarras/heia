/**
 * @(#)SessionTimer.java
 *
 *
 * @author
 * @version 1.00 2008/5/23
 */
package ch.epfl.javanco.remote;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.base.Javanco;


class SessionTimer extends TimerTask{

	//timer interval in milisecond
	public static int timerinteval = 30;

	//var sessionTimers contains all session name and conresponding  Timer
	public  static HashMap<String, SessionTimer> sessionTimers =new HashMap<String, SessionTimer>();

	//Used to store the time of the first connection
	private Calendar iternalTime;

	//Used to store the time of the new access
	private Calendar updatedTime;

	private static final Logger logger = new Logger(SessionTimer.class);
	private String sessionID;
	private Timer  timer;



	public SessionTimer(String sessionID) {
		/// to load value contained in preference (properties) file
		timerinteval = Integer.parseInt(Javanco.getProperty("ch.epfl.javanco.remote.sessionExpirationTime"));
		this.sessionID = sessionID;
		this.iternalTime = Calendar.getInstance();
		this.updatedTime = Calendar.getInstance();
		logger.info("New timer created for session " + sessionID);
	}




	public static void addSessionTimer(String sessionID){
		SessionTimer sessionTimer = new SessionTimer(sessionID);
		sessionTimer.startTimer();
		SessionTimer.sessionTimers.put(sessionID, sessionTimer);
	}
	/**
	 *Remove SessionTimer corresponding to the session with ID sessionID  and  and store it in the HashMap
	 *@param sessionID
	 */
	public static void removeSessionTimer(String sessionID){
		SessionTimer.sessionTimers.remove(sessionID);
	}
	/**
	 *Update the last  access time  corresponding to the session with ID sessionID
	 *@param sessionID
	 */
	public static  void updateSessionTime(String sessionID){
		SessionTimer sessionTimer = SessionTimer.sessionTimers.get(sessionID);
		Calendar cal = sessionTimer.updatedTime;
		cal.setTime(Calendar.getInstance().getTime());
		logger.debug("Session timer updated " + cal.getTime());
	}



	private  void startTimer(){
		this.timer = new Timer();
		this.timer.schedule(this,1000*SessionTimer.timerinteval,1000*SessionTimer.timerinteval);
	}



	private long  computeGap(Calendar calendar){
		long gap = 0 ;
		if(!this.iternalTime.equals(calendar)){
			gap=( calendar.getTimeInMillis() - this.iternalTime.getTimeInMillis());
			if(gap < 3000) {
				gap = 0;
			}
		}
		return gap;
	}


	@Override
	public void run() {
		try{
			long gap = 0;
			synchronized (this.updatedTime) {
				gap = computeGap(this.updatedTime);
			}
			if(gap == 0){
				WrapperFactory.getWrapperFactory().killHandler(this.sessionID);
				this.cancel();
				LoadCheck.removeSession(this.sessionID); // remove session at the LoadCheck class
				logger.info("Session " + sessionID + " is timeout and has been deleted");
			}
			else{
				logger.debug("Session " + sessionID + " is not timed out (remaining : " + gap +")");
				this.iternalTime = Calendar.getInstance();
			}
		}
		catch(Exception e) {
			logger.error("Problem while trying to delete session : " + sessionID, e);
		}
	}
}