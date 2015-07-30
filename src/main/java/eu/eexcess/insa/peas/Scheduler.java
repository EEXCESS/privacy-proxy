package eu.eexcess.insa.peas;

import java.util.Timer;

import eu.eexcess.Config;

public class Scheduler {
	
	private static Timer timerCaches = null;
	private static Timer timerQueryLog = null;
	
	public Scheduler(){}
	
	public static void addCachesTasks(){
		if (Scheduler.timerCaches == null){
			timerCaches = new Timer(true); // XXX Try with false
			CachingTask task = new CachingTask();
			Long delay = Long.valueOf(Config.getValue(Config.CACHE_DELAY));
			timerCaches.scheduleAtFixedRate(task, 0, delay);
		}
	}
	
	public static void flushOutQueryLogTask(){
		if (Scheduler.timerQueryLog == null){
			timerQueryLog = new Timer(true); // XXX Try with false
			QueryLogFlushOutTask task = new QueryLogFlushOutTask();
			Long delay = Long.valueOf(Config.getValue(Config.QUERY_LOG_DELAY));
			timerQueryLog.scheduleAtFixedRate(task, 0, delay);
		}
	}

}
