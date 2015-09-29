package eu.eexcess;


/**
 * This class contains all the constants used in the project. 
 * @author Thomas Cerqueus
 */
public class Cst {
	
	//*************
	//** GENERAL ** 
	//*************
	
	public static final String COLUMN_SEPARATOR = "\t";
	public static final String KEYWORDS_SEPARATOR = Cst.SPACE;
	public static final String MATCHING_CRITERION = "^\\w{3,}$"; // Words with at least 3 characters
	public static final String LINE_BREAK = System.getProperty("line.separator"); 
	public static final String MEDIA_TYPE_IMAGE = "image/png";	
	public static final String TMP_FILE_PREFIX = "tmp-";
	
	// Path
	public static final String CATALINA_BASE = System.getProperty("catalina.base");
	public static final String PRIVACY_PROXY_URL = Config.getValue(Config.PRIVACY_PROXY_URL);
	
	// Errors
	public static final String SPACE = " ";
	public static final String EMPTY_ORIGIN = "empty";

	//public static final String PARAM_ORIGIN = "origin";
	public static final String PARAM_INTERACTION_TYPE = "interactionType";
	public static final String PARAM_PARTNER_ID = "partnerId";
	public static final String PARAM_IMAGE_TYPE = "type";
	
	//**********
	//** TAGS **
	//**********
	
	// Exchange formats
	public static final String TAG_ORIGIN = "origin";
	public static final String TAG_IP = "ip";
	public static final String TAG_ID = "id";
	public static final String TAG_UUID = "uuid";
	public static final String TAG_USER_ID = "userID";
	public static final String TAG_CONTEXT = "context";
	public static final String TAG_CONTEXT_KEYWORDS = "contextKeywords";
	public static final String TAG_QUERY = "query";
	public static final String TAG_DETAILS_QUERY = "detailsQuery";
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
	public static final String TAG_PARTNER_RESPONSE_STATE = "partnerResponseState";
	public static final String TAG_PARTNERS = "partner";
	public static final String TAG_PARTNER_ID = "systemId";
	public static final String TAG_CLIENT_TYPE = "clientType"; 
	public static final String TAG_CLIENT_VERSION = "clientVersion";
	public static final String TAG_MODULE = "module";
	
	// Dictionary and Co-occurence graph
	public static final String TAG_TERM = "term";
	public static final String TAG_FREQUENCIES = "frequencies";
	public static final String TAG_FREQUENCY = "frequency";
	
	// For logging
	public static final String TAG_INTERACTION_TYPE = "interactionType";
	public static final String TAG_TIMESTAMP = "timestamp";
	public static final String TAG_CONTENT = "content";
	public static final String TAG_MODULE_NAME = "name";
	public static final String TAG_DURATION = "duration";
	public static final String TAG_RATING = "rating";
	
	//********************
	//** ACCESS CONTROL **
	//********************
	
	private static final String ACA = "Access-Control-Allow-";
	public static final String ACA_ORIGIN_KEY = ACA + "Origin";
	public static final String ACA_ORIGIN_VALUE = "*";
	public static final String ACA_METHODS_KEY = ACA + "Methods";
	public static final String ACA_POST = "POST";
	public static final String ACA_GET = "GET";
	public static final String ACA_OPTIONS = "OPTIONS";
	public static final String ACA_HEADERS_KEY = ACA + "Headers";
	public static final String ACA_HEADERS_VALUE = "Origin, Content-Type, Accept";
	
	//*************
	//** LOGGING **
	//*************
	
	// Implicit interactions
	public static final String INTERACTION_QUERY = "query";
	public static final String INTERACTION_DETAILS_QUERY = "detailsQuery";
	public static final String INTERACTION_RESPONSE = "response";
	public static final String INTERACTION_DETAILS_RESPONSE = "detailsResponse";
	
	// Explicit interactions
	public static final String INTERACTION_MODULE_OPENED = "moduleOpened";
	public static final String INTERACTION_MODULE_CLOSED = "moduleClosed";
	public static final String INTERACTION_MODULE_STATISTICS_COLLECTED = "moduleStatisticsCollected";
	public static final String INTERACTION_ITEM_OPENED = "itemOpened";  
	public static final String INTERACTION_ITEM_CLOSED = "itemClosed";
	public static final String INTERACTION_ITEM_CITED_AS_TEXT = "itemCitedAsText";
	public static final String INTERACTION_ITEM_CITED_AS_IMAGE = "itemCitedAsImage";
	public static final String INTERACTION_ITEM_CITED_AS_HYPERLINK = "itemCitedAsHyperlink";
	public static final String INTERACTION_ITEM_RATED = "itemRated";
	public static final String INTERACTION_ITEM_BOOKMARKED = "itemBookmarked";
	
	public static final String[] VALID_INTERACTIONS = {Cst.INTERACTION_QUERY, Cst.INTERACTION_DETAILS_QUERY, 
		Cst.INTERACTION_RESPONSE, Cst.INTERACTION_DETAILS_RESPONSE, 
		Cst.INTERACTION_MODULE_OPENED, Cst.INTERACTION_MODULE_CLOSED, 
		Cst.INTERACTION_MODULE_STATISTICS_COLLECTED, Cst.INTERACTION_ITEM_OPENED, 
		Cst.INTERACTION_ITEM_CLOSED, Cst.INTERACTION_ITEM_CITED_AS_TEXT, 
		Cst.INTERACTION_ITEM_CITED_AS_IMAGE, Cst.INTERACTION_ITEM_CITED_AS_HYPERLINK, 
		Cst.INTERACTION_ITEM_RATED, Cst.INTERACTION_ITEM_BOOKMARKED}; 

	//**************
	//** SERVICES **
	//**************
	
	// Services provided by the privacy proxy
	public static final String PATH = "/issuer/";
	public static final String PATH_RECOMMEND = "recommend";
	public static final String PATH_GET_DETAILS = "getDetails";
	public static final String PATH_GET_REGISTERED_PARTNERS = "getRegisteredPartners";
	public static final String PATH_GET_PARTNER_FAVICON = "getPartnerFavIcon";
	public static final String PATH_GET_PREVIEW_IMAGE = "getPreviewImage";
	public static final String PATH_LOG = "log";
	public static final String PATH_GET_CLIQUES = "getMaximalCliques";
	public static final String PATH_GET_CO_OCCURRENCE_GRAPH = "getCoOccurrenceGraph";
	public static final String PATH_SUGGEST_CATEGORIES = "suggestCategories";
	public static final String PATH_RECOGNIZE_ENTITY = "recognizeEntity";
	
	// Services provided by the recommender
	public static final String SERVICE_RECOMMEND = Config.getValue(Config.RECOMMENDER_URL) + PATH_RECOMMEND;
	public static final String SERVICE_GET_DETAILS = Config.getValue(Config.RECOMMENDER_URL) + PATH_GET_DETAILS;
	public static final String SERVICE_GET_REGISTERED_PARTNERS = Config.getValue(Config.RECOMMENDER_URL) + PATH_GET_REGISTERED_PARTNERS;
	public static final String SERVICE_GET_PARTNER_FAVICON = Config.getValue(Config.RECOMMENDER_URL) + PATH_GET_PARTNER_FAVICON;
	public static final String SERVICE_GET_PREVIEW_IMAGE = Config.getValue(Config.RECOMMENDER_URL) + PATH_GET_PREVIEW_IMAGE;
	
	// Services provided by external providers
	public static final String SERVICE_SUGGEST_CATEGORIES = Config.getValue(Config.SUGGEST_CATEGORY);
	public static final String SERVICE_RECOGIZE_ENTITY = Config.getValue(Config.RECOGIZE_ENTITY);
	
}
