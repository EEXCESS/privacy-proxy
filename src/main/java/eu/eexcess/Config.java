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
	
	// Keys
	public static final String INTERACTION_TYPE_RATING = "interactionType.rating";
	public static final String INTERACTION_TYPE_RESULT = "interactionType.result";
	public static final String INTERACTION_TYPE_RESULT_CLOSE = "interactionType.resultClose";
	public static final String INTERACTION_TYPE_RESULT_VIEW = "interactionType.resultView";
	public static final String INTERACTION_TYPE_SHOW_HIDE = "interactionType.showHide";
	public static final String INTERACTION_TYPE_FACET_SCAPE = "interactionType.facetScape";
	public static final String INTERACTION_TYPE_QUERY_ACTIVATED = "interactionType.queryActivated";
	
	public static final String TAG_ID = "tag.id";
	public static final String TAG_UUID = "tag.uuid";
	public static final String TAG_USER_ID = "tag.userId";
	public static final String TAG_CONTEXT = "tag.context";
	public static final String TAG_CONTEXT_KEYWORDS = "tag.contextKeywords";
	public static final String TAG_QUERY = "tag.query";
	public static final String TAG_QUERY_ID = "tag.queryId";
	public static final String TAG_RESULT = "tag.result";
	public static final String TAG_RESULTS = "tag.results";
	public static final String TAG_FACETS = "tag.facets";
	public static final String TAG_PROVIDER = "tag.provider";
	public static final String TAG_PROVIDER_ABBREVIATION = "tag.provider.abbreviation";
	public static final String TAG_ORIGIN = "tag.origin";
	public static final String TAG_HTTP_ERROR_CODE = "tag.HttpErrorCode";
	public static final String TAG_IP = "tag.ip";
	
	public static final String ACCESS_CONTROL_KEY = "accessControl.key";
	public static final String ACCESS_CONTROL_VALUE = "accessControl.value";
	
	public static final String LOG_DIRECTORY = "proxy.service.log.directory";

	public static final String RECOMMENDER_URL = "recommender.url";
	public static final String RECOMMENDER_PATH = "recommender.path";
	
	public static final String DISAMBIGUATER_URL = "disambiguater.url";
	public static final String DISAMBIGUATER_PATH = "disambiguater.path";
	public static final String DISAMBIGUATER_SERVICE_CATEGORY_SUGGESTION = "disambiguater.service.categorySuggestion";
	
	public static final String LOGGER_PRIVACY_PROXY = "logging.privacyProxy";
	public static final String LOGGER_INTERACTION = "logging.interaction";
	public static final String LOGGER_FACET_SCAPE = "logging.interaction";
	
	// Configuration file
	private static final String PROP_FILE_NAME = "config.properties";
	
	private static Properties prop = null;

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

	public static String getValue(String key) {
		String value = null;
		if (Config.prop == null){
			new Config();
		}
		value = prop.getProperty(key);
		return value;
	}

}
