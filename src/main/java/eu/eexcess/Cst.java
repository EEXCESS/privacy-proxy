package eu.eexcess;

import org.apache.log4j.Logger;

import eu.eexcess.up.ProxyLogProcessor;

/**
 * This class contains all the constants that are used in the project. 
 * @author Thomas
 *
 */
public class Cst {
	
	// Errors
	public static final String ERR_MSG_NOT_REST_API = "not a valid REST API";
	public static final String SPACE = " ";
	public static final String EMPTY_ORIGIN = "empty";
	
	// Services
	public static final String VERSION = "/v1";
	public static final String PATH_RECOMMEND = "/recommend";
	public static final String PATH_LOG = "/log/{InteractionType}";
	public static final String PATH_DISAMBIGUATE = "/disambiguate";
	public static final String PATH_GET_REGISTERED_PARTNERS = "/getRegisteredPartners";
	public static final String PATH_CATEGORY_SUGGESTION = "/categorysuggestion";
	
	public static final String PARAM_ORIGIN = "origin";
	
	// Status
	public static final Integer WS_200 = 200;
	public static final Integer WS_201 = 201;
	public static final Integer WS_404 = 404;
	public static final Integer WS_500 = 500;
	
	// Tags
	public static final String TAG_ID = Config.getValue(Config.TAG_ID);
	public static final String TAG_UUID = Config.getValue(Config.TAG_UUID);
	public static final String TAG_USER_ID = Config.getValue(Config.TAG_USER_ID);
	public static final String TAG_CONTEXT = Config.getValue(Config.TAG_CONTEXT);
	public static final String TAG_CONTEXT_KEYWORDS = Config.getValue(Config.TAG_CONTEXT_KEYWORDS);
	public static final String TAG_QUERY = Config.getValue(Config.TAG_QUERY);
	public static final String TAG_QUERY_ID = Config.getValue(Config.TAG_QUERY_ID);
	public static final String TAG_RESULT = Config.getValue(Config.TAG_RESULT);
	public static final String TAG_RESULTS = Config.getValue(Config.TAG_RESULTS);
	public static final String TAG_FACETS = Config.getValue(Config.TAG_FACETS);
	public static final String TAG_PROVIDER = Config.getValue(Config.TAG_PROVIDER);
	public static final String TAG_PROVIDER_SHORT = Config.getValue(Config.TAG_PROVIDER_ABBREVIATION);
	public static final String TAG_ORIGIN = Config.getValue(Config.TAG_ORIGIN);
	public static final String TAG_HTTP_ERR_CODE = Config.getValue(Config.TAG_HTTP_ERROR_CODE);
	public static final String TAG_IP = Config.getValue(Config.TAG_IP);
	
	// Access control
	public static final String ACCESS_CONTROL_KEY = Config.getValue(Config.ACCESS_CONTROL_KEY);
	public static final String ACCESS_CONTROL_VALUE = Config.getValue(Config.ACCESS_CONTROL_VALUE);

	// Interaction types
	public static final String RATING = Config.getValue(Config.INTERACTION_TYPE_RATING);
	public static final String RESULT = Config.getValue(Config.INTERACTION_TYPE_RESULT);
	public static final String RESULT_CLOSE = Config.getValue(Config.INTERACTION_TYPE_RESULT_CLOSE);
	public static final String RESULT_VIEW = Config.getValue(Config.INTERACTION_TYPE_RESULT_VIEW);
	public static final String SHOW_HIDE = Config.getValue(Config.INTERACTION_TYPE_SHOW_HIDE);
	public static final String FACET_SCAPE = Config.getValue(Config.INTERACTION_TYPE_FACET_SCAPE);
	public static final String QUERY_ACTIVATED = Config.getValue(Config.INTERACTION_TYPE_QUERY_ACTIVATED);

	// Federated recommender
	private static final String FEDERATED_RECOMMENDER_URL = Config.getValue(Config.RECOMMENDER_URL);
	private static final String RECOMMENDER_API_URL = FEDERATED_RECOMMENDER_URL + Config.getValue(Config.RECOMMENDER_PATH);
	
	// Disambiguater
	private static final String DISAMBIGUATION_URL = Config.getValue(Config.DISAMBIGUATER_URL);
	private static final String DISAMBIGUATION_API_URL = DISAMBIGUATION_URL + Config.getValue(Config.DISAMBIGUATER_PATH);

	// Services
	public static final String SERVICE_RECOMMEND = RECOMMENDER_API_URL + PATH_RECOMMEND;
	public static final String SERVICE_GET_REGISTERED_PARTNERS = RECOMMENDER_API_URL + PATH_GET_REGISTERED_PARTNERS;
	public static final String SERVICE_DISAMBIGUATION = DISAMBIGUATION_API_URL + PATH_CATEGORY_SUGGESTION;

	// Loggers
	public static final String PATH_LOG_DIRECTORY = Config.getValue(Config.LOG_DIRECTORY);
	public static final Logger LOGGER_PRIVACY_PROXY = Logger.getLogger(Config.getValue(Config.LOGGER_PRIVACY_PROXY));
	public static final Logger LOGGER_FACET_SCAPE = Logger.getLogger(Config.getValue(Config.LOGGER_FACET_SCAPE));
	public static final Logger LOGGER_INTERACTION = Logger.getLogger(Config.getValue(Config.LOGGER_INTERACTION));
	
	// Log processor
	public static final ProxyLogProcessor LOG_PROCESSOR = new ProxyLogProcessor();
	
}
