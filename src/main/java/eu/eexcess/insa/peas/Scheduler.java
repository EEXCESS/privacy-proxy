package eu.eexcess.insa.peas;

import java.util.Timer;

import eu.eexcess.Config;

/**
 * This class represents the scheduler in charge of managing the query log and the caches. 
 * @author Thomas Cerqueus
 * @version 1.0
 */
public class Scheduler {
	
	private static Timer timerCaches = null;
	private static Timer timerQueryLog = null;
	
	/**
	 * Default constructor. 
	 */
	public Scheduler(){}
	
	/**
	 * Adds a task to the scheduler. This task consists in updating the caches regularly. 
	 * The delay between the execution of two tasks in given in the configuration file. 
	 * The core of the method is executed only once (even if the method is called several times). 
	 */
	public static void addCachesTasks(){
		if (Scheduler.timerCaches == null){
			timerCaches = new Timer(true); 
			CachingTask task = new CachingTask();
			Long delay = Long.valueOf(Config.getValue(Config.CACHE_DELAY));
			timerCaches.scheduleAtFixedRate(task, 0, delay);
		}
	}
	
	/**
	 * Adds a task to the scheduler. This task consists in flushing out the query log regularly. 
	 * The delay between the execution of two tasks in given in the configuration file. 
	 * The core of the method is executed only once (even if the method is called several times). 
	 */
	public static void flushOutQueryLogTask(){
		if (Scheduler.timerQueryLog == null){
			timerQueryLog = new Timer(); 
			QueryLogFlushOutTask task = new QueryLogFlushOutTask();
			Long delay = Long.valueOf(Config.getValue(Config.QUERY_LOG_DELAY));
			timerQueryLog.scheduleAtFixedRate(task, 0, delay);
		}
	}

}
