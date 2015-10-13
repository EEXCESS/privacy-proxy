package eu.eexcess.insa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

import eu.eexcess.Config;
import eu.eexcess.Cst;

/**
 * This class is used to check the compliance of messages. 
 * It also provides methods to generate adapted error responses. 
 * @author Thomas Cerqueus
 * @version 1.0
 */
public class ComplianceManager {

	private String template = "";
	private final String MSG_BASE = "The request sent by the client was syntactically incorrect. ";
	private final String MSG_ORIGIN_MISSING = "An attribute \"" + Cst.TAG_ORIGIN + "\" (composed of \"" + Cst.TAG_USER_ID + "\", \"" + Cst.TAG_CLIENT_TYPE + "\", \"" + Cst.TAG_CLIENT_VERSION + "\" and \"" + Cst.TAG_MODULE + "\") must be provided.";
	private final String MSG_QUERY_ID_MISSING = "An attribute \"" + Cst.TAG_QUERY_ID + " must be provided.";
	private final String TAG_STATUS_CODE = "$statusCode$";
	private final String TAG_STATUS_REASON_PHRASE = "$statusName$";
	private final String TAG_MESSAGE = "$message$";

	/**
	 * Default constructor. 
	 */
	public ComplianceManager(){
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(Config.getValue(Config.TEMPLATE_HTTP_ERROR));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = 0;
		try {
			while ((length = inputStream.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.template =  new String(baos.toByteArray());
	}
	
	/**
	 * Determines if a JSON object contains a well-formed {@code origin} attribute. 
	 * @param object The JSON object to check. 
	 * @return {@code true} if the JSON object contains a well-formed {@code origin} attribute; {@code false} otherwise. 
	 */
	public Boolean containsCompliantOrigin(JSONObject object){
		Boolean complient = false;
		if (object.has(Cst.TAG_ORIGIN)){
			JSONObject origin = object.getJSONObject(Cst.TAG_ORIGIN);
			complient = origin.has(Cst.TAG_USER_ID) && origin.has(Cst.TAG_CLIENT_TYPE) && origin.has(Cst.TAG_CLIENT_VERSION) && origin.has(Cst.TAG_MODULE);		
		}
		return complient;
	}
	

	/**
	 * Determines if a JSON object contains a {@code queryID} attribute. 
	 * @param object The JSON object to check. 
	 * @return {@code true} if the JSON object contains a {@code queryID} attribute; {@code false} otherwise. 
	 */
	public Boolean containsQueryId(JSONObject object){
		return object.has(Cst.TAG_QUERY_ID);
	}
	
	private Response missingAttributeRequestResponse(String msg){
		String content = template;
		Integer statusCode = Status.BAD_REQUEST.getStatusCode();
		String statusReasonPhrase = Status.BAD_REQUEST.getReasonPhrase();
		content = content.replace(TAG_STATUS_CODE, statusCode.toString());
		content = content.replace(TAG_STATUS_REASON_PHRASE, statusReasonPhrase);
		content = content.replace(TAG_MESSAGE, MSG_BASE + Cst.SPACE + msg);
		return Response.status(Status.BAD_REQUEST).entity(content).build();
	}
	
	/**
	 * Generates an adapted error request response for when the {@code origin} attribute is missing or malformed. 
	 * @return A request response.  
	 */
	public Response missingOriginRequestResponse(){
		return missingAttributeRequestResponse(MSG_ORIGIN_MISSING);
	}
	
	/**
	 * Generates an adapted error request response for when the {@code queryID} attribute is missing. 
	 * @return A request response.  
	 */
	public Response missingQueryIdRequestResponse(){
		return missingAttributeRequestResponse(MSG_QUERY_ID_MISSING);
	}
	
}
