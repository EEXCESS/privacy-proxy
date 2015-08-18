package eu.eexcess;

import org.apache.log4j.Logger;

import eu.eexcess.up.ProxyLogProcessor;

/**
 * This class contains all the constants used in the project. 
 * @author Thomas Cerqueus
 *
 */
public class Cst {
	
	// Errors
	public static final String SPACE = " ";
	public static final String EMPTY_ORIGIN = "empty";
	
	// Services
	public static final String VERSION = "/v1";
	public static final String PATH_GET_REGISTERED_PARTNERS = "/getRegisteredPartners";
	public static final String PATH_RECOMMEND = "/recommend";
	public static final String PATH_GET_DETAILS = "/getDetails";
	public static final String PATH_LOG = "/log/{InteractionType}";
	public static final String PATH_DISAMBIGUATE = "/disambiguate";
	//public static final String PATH_CATEGORY_SUGGESTION = "/categorysuggestion";
	public static final String PATH_GET_CLIQUES = "/getMaximalCliques";
	public static final String PATH_GET_CO_OCCURRENCE_GRAPH = "/getCoOccurrenceGraph";
	
	public static final String PARAM_ORIGIN = "origin";
	
	// Tags
	public static final String TAG_ORIGIN = "origin";
	public static final String TAG_IP = "ip";
	public static final String TAG_ID = "id";
	public static final String TAG_UUID = "uuid";
	public static final String TAG_USER_ID = "userID";
	public static final String TAG_CONTEXT = "context";
	public static final String TAG_CONTEXT_KEYWORDS = "contextKeywords";
	public static final String TAG_QUERY = "query";
	public static final String TAG_QUERY_ID = "queryID";
	public static final String TAG_RESULT = "result";
	public static final String TAG_RESULTS = "results";
	public static final String TAG_NB_RESULTS = "totalResults";
	public static final String TAG_FACETS = "facets";
	public static final String TAG_PROVIDER = "provider";
	public static final String TAG_PROVIDER_SHORT = "p";
	public static final String TAG_QUERY_TEXT = "text";
	public static final String TAG_DOCUMENT_BADGE = "documentBadge";
	public static final String TAG_DETAIL = "detail";
	
	// Access control
	private static final String ACA = "Access-Control-Allow-";
	public static final String ACA_ORIGIN_KEY = ACA + "Origin";
	public static final String ACA_ORIGIN_VALUE = "*";
	public static final String ACA_METHODS_KEY = ACA + "Methods";
	public static final String ACA_POST = "POST";
	public static final String ACA_GET = "GET";
	public static final String ACA_OPTIONS = "OPTIONS";
	public static final String ACA_HEADERS_KEY = ACA + "Headers";
	public static final String ACA_HEADERS_VALUE = "Origin, Content-Type, Accept";
	
	// Interaction types
	public static final String RATING = "rating";
	public static final String RESULT = "result";
	public static final String RESULT_CLOSE = "rclose";
	public static final String RESULT_VIEW = "rview";
	public static final String SHOW_HIDE = "show_hide";
	public static final String FACET_SCAPE = "facetScape";
	public static final String QUERY_ACTIVATED = "query_activated";
	
	// Dictionary and Co-occurence graph
	public static final String TAG_TERM = "term";
	public static final String TAG_FREQUENCIES = "frequencies";
	public static final String TAG_FREQUENCY = "frequency";

	// Federated recommender
	public static final String RECOMMENDER_LABEL = Config.getValue(Config.RECOMMENDER_LABEL);
	private static final String RECOMMENDER_URL = Config.getValue(Config.RECOMMENDER_URL);
	private static final String RECOMMENDER_API_URL = RECOMMENDER_URL + Config.getValue(Config.RECOMMENDER_PATH);
	
	// Disambiguater
	private static final String DISAMBIGUATER_URL = Config.getValue(Config.DISAMBIGUATER_URL);
	private static final String DISAMBIGUATER_API_URL = DISAMBIGUATER_URL + Config.getValue(Config.DISAMBIGUATER_PATH);

	// Services
	public static final String SERVICE_RECOMMEND = RECOMMENDER_API_URL + PATH_RECOMMEND;
	public static final String SERVICE_GET_DETAILS = RECOMMENDER_API_URL + PATH_GET_DETAILS;
	public static final String SERVICE_GET_REGISTERED_PARTNERS = RECOMMENDER_API_URL + PATH_GET_REGISTERED_PARTNERS;
	public static final String SERVICE_DISAMBIGUATION = DISAMBIGUATER_API_URL + "/categorysuggestion";

	// Loggers
	public static final String PATH_LOG_DIRECTORY = Config.getValue(Config.LOG_DIRECTORY);
	public static final Logger LOGGER_PRIVACY_PROXY = Logger.getLogger(Config.getValue(Config.LOGGER_PRIVACY_PROXY));
	public static final Logger LOGGER_FACET_SCAPE = Logger.getLogger(Config.getValue(Config.LOGGER_FACET_SCAPE));
	public static final Logger LOGGER_INTERACTION = Logger.getLogger(Config.getValue(Config.LOGGER_INTERACTION));
	
	// Log processor
	public static final ProxyLogProcessor LOG_PROCESSOR = new ProxyLogProcessor();
	
	// 
	public static final String COLUMN_SEPARATOR = "\t";
	public static final String KEYWORDS_SEPARATOR = Cst.SPACE;
	public static final String MATCHING_CRITERION = "^\\w{3,}$"; // Words with at least 3 characters
	public static final String LINE_BREAK = System.getProperty("line.separator"); 
	
	public static final String TMP_FILE_PREFIX = "tmp-";
	
}
