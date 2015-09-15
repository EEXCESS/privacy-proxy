package eu.eexcess;


/**
 * This class contains all the constants used in the project. 
 * @author Thomas Cerqueus
 *
 */
public class Cst {
		
	// Errors
	public static final String SPACE = " ";
	//public static final String EMPTY_ORIGIN = "empty";
	
	// Media type
	public static final String MEDIA_TYPE_IMAGE = "image/png";
	
	// Services
	public static final String PATH = "/requester";
	public static final String PATH_GET_REGISTERED_PARTNERS = "getRegisteredPartners";
	public static final String PATH_GET_PARTNER_FAVICON = "getPartnerFavIcon";
	public static final String PATH_GET_PREVIEW_IMAGE = "getPreviewImage";
	public static final String PATH_RECOMMEND = "recommend";
	public static final String PATH_GET_DETAILS = "getDetails";
	public static final String PATH_LOG = "log/{InteractionType}";
	public static final String PATH_DISAMBIGUATE = "disambiguate";
	public static final String PATH_GET_CLIQUES = "getMaximalCliques";
	public static final String PATH_GET_CO_OCCURRENCE_GRAPH = "getCoOccurrenceGraph";
	
	public static final String PARAM_ORIGIN = "origin";
	public static final String PARAM_INTERACTION_TYPE = "interactionType";
	public static final String PARAM_PARTNER_ID = "partnerId";
	public static final String PARAM_IMAGE_TYPE = "type";
	
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

	// Issuer
	public static final String ISSUER_LABEL = Config.getValue("issuer.label");
	private static final String ISSUER_URL = Config.getValue("issuer.url");
	private static final String ISSUER_API_URL = ISSUER_URL + Config.getValue("issuer.path");

	// Services
	public static final String SERVICE_RECOMMEND = ISSUER_API_URL + PATH_RECOMMEND;
	public static final String SERVICE_GET_DETAILS = ISSUER_API_URL + PATH_GET_DETAILS;
	public static final String SERVICE_GET_REGISTERED_PARTNERS = ISSUER_API_URL + PATH_GET_REGISTERED_PARTNERS;
	public static final String SERVICE_GET_PARTNER_FAVICON = ISSUER_API_URL + PATH_GET_PARTNER_FAVICON;
	public static final String SERVICE_GET_PREVIEW_IMAGE = ISSUER_API_URL + PATH_GET_PREVIEW_IMAGE;
	public static final String SERVICE_LOG = ISSUER_API_URL + PATH_LOG;
	public static final String SERVICE_GET_MAXIMAL_CLIQUES = ISSUER_API_URL + PATH_GET_CLIQUES;
	public static final String SERVICE_GET_CO_OCCURRENCE_GRAPH = ISSUER_API_URL + PATH_GET_CO_OCCURRENCE_GRAPH;
	
}
