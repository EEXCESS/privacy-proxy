package eu.eexcess;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class is the configuration file manager. 
 * It implements the Singleton design pattern. 
 * @author Thomas Cerqueus
 */
public class Config {
	
	public static final String LOG_DIRECTORY = "log.directory";
	
	public static final String RECOMMENDER_URL = "recommender.url";
	public static final String PRIVACY_PROXY_URL = "privacyProxy.url";
	
	public static final String DATA_DIRECTORY = "data.directory";
	public static final String CACHE_DIRECTORY = "cache.directory";
	
	public static final String QUERY_LOG = "data.queryLog";
	public static final String QUERY_LOG_DELAY = "data.queryLog.delay";
	public static final String QUERY_LOG_WINDOW = "data.queryLog.window";
	public static final String CO_OCCURRENCE_GRAPH_FILE = "cache.coOccurrenceGraph";
	public static final String CLIQUES_FILE = "cache.cliques";
	public static final String CACHE_DELAY = "cache.delay";
	
	public static final String SUGGEST_CATEGORY = "services.doser.suggestCategories";
	public static final String RECOGIZE_ENTITY = "services.doser.recognizeEntity";
	
	// Configuration file
	private static final String PROP_FILE_NAME = "config.properties";
	
	private static Properties prop = null;

	/**
	 * Initialization of the configuration file manager. 
	 */
	public Config(){
		Config.prop = new Properties();
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(Config.PROP_FILE_NAME);
		try {
			if (inputStream != null) {
				prop.load(inputStream);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the value associate with a given key. 
	 * @param key Key of the variable. 
	 * @return Value corresponding to the key.  
	 */
	public static String getValue(String key) {
		String value = null;
		if (Config.prop == null){
			new Config();
		}
		value = prop.getProperty(key);
		return value;
	}

}
