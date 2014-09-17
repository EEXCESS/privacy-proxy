package eu.eexcess;

/**
 * This class contains all the constants that are used in the project. 
 * @author Thomas
 *
 */
public class Cst {
	
	public static final String TYPE_APPLICATION = "application/json";
	public static final String SPACE = " ";
	
	// Errors
	public static final String ERR_MSG_PARSE_JSON = "Unable to parse the JSON";
	public static final String ERR_MSG_NOT_REST_API = "not a valid REST API";
	
	// Status
	public static final Integer WS_200 = 200;
	public static final Integer WS_201 = 201;
	public static final Integer WS_404 = 404;
	public static final Integer WS_500 = 500;
	
	// Tags
	public static final String TAG_ID = "id";
	public static final String TAG_UUID = "uuid";
	public static final String TAG_USER_ID = "userID";
	public static final String TAG_CONTEXT = "context";
	public static final String TAG_CONTEXT_KEYWORDS = "contextKeywords";
	public static final String TAG_QUERY = "query";
	public static final String TAG_RESULT = "result";
	public static final String TAG_RESULTS = "results";
	public static final String TAG_FACETS = "facets";
	public static final String TAG_PROVIDER = "provider";
	public static final String TAG_ORIGIN = "origin";
	public static final String TAG_HTTP_ERR_CODE = "HTTPErrorCode";
	public static final String TAG_IP = "ip";

}
