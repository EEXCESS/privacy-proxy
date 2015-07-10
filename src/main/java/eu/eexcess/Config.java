package eu.eexcess;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Implements the Singleton design pattern. 
 * @author Thomas Cerqueus
 */
public class Config {
	
	public static final String LOG_DIRECTORY = "proxy.service.log.directory";
	
	public static final String RECOMMENDER_URL = "recommender.url";
	public static final String RECOMMENDER_PATH = "recommender.path";
	public static final String RECOMMENDER_LABEL = "recommender.label";
	
	public static final String DISAMBIGUATER_URL = "disambiguater.url";
	public static final String DISAMBIGUATER_PATH = "disambiguater.path";
	public static final String DISAMBIGUATER_SERVICE_CATEGORY_SUGGESTION = "disambiguater.service.categorySuggestion";
	
	public static final String LOGGER_PRIVACY_PROXY = "logging.privacyProxy";
	public static final String LOGGER_INTERACTION = "logging.interaction";
	public static final String LOGGER_FACET_SCAPE = "logging.interaction";
	
	public static final String DATA_DIRECTORY = "data.directory";
	public static final String CACHE_DIRECTORY = "cache.directory";
	
	public static final String INIT_QUERY_LOG = "init.queryLog";
	
	public static final String QUERY_LOG = "data.queryLog";
	public static final String CO_OCCURRENCE_GRAPH_FILE = "cache.coOccurrenceGraph";
	public static final String CLIQUES_FILE = "cache.cliques";
	
	// Configuration file
	private static final String PROP_FILE_NAME = "config.properties";
	
	private static Properties prop = null;

	/**
	 * 
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
	 * TODO
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
