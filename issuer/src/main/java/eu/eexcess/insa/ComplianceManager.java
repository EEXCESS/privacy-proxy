package eu.eexcess.insa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

import eu.eexcess.Config;
import eu.eexcess.Cst;


// TODO documentation
public class ComplianceManager {

	private String template = "";
	private final String MSG_BASE = "The request sent by the client was syntactically incorrect. ";
	private final String MSG_ORIGIN_MISSING = "An attribute \"" + Cst.TAG_ORIGIN + "\" (composed of \"" + Cst.TAG_USER_ID + "\", \"" + Cst.TAG_CLIENT_TYPE + "\", \"" + Cst.TAG_CLIENT_VERSION + "\" and \"" + Cst.TAG_MODULE + "\") must be provided.";
	private final String TAG_STATUS_CODE = "$statusCode$";
	private final String TAG_STATUS_REASON_PHRASE = "$statusName$";
	private final String TAG_MESSAGE = "$message$";

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
	
	public Boolean containsCompliantOrigin(JSONObject object){
		Boolean complient = false;
		if (object.has(Cst.TAG_ORIGIN)){
			JSONObject origin = object.getJSONObject(Cst.TAG_ORIGIN);
			complient = origin.has(Cst.TAG_USER_ID) && origin.has(Cst.TAG_CLIENT_TYPE) && origin.has(Cst.TAG_CLIENT_VERSION) && origin.has(Cst.TAG_MODULE);		
		}
		return complient;
	}
	
	public Response notCompliantRequestResponse(){
		String content = template;
		Integer statusCode = Status.BAD_REQUEST.getStatusCode();
		String statusReasonPhrase = Status.BAD_REQUEST.getReasonPhrase();
		content = content.replace(TAG_STATUS_CODE, statusCode.toString());
		content = content.replace(TAG_STATUS_REASON_PHRASE, statusReasonPhrase);
		content = content.replace(TAG_MESSAGE, MSG_BASE + Cst.SPACE + MSG_ORIGIN_MISSING);
		return Response.status(Status.BAD_REQUEST).entity(content).build();
	}
	
}
