package eu.eexcess.insa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import eu.eexcess.Cst;

public class Util {

	public Util(){}
	
	public JSONObject getJsonContent(String fileName){
		String content = "";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
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
		content =  new String(baos.toByteArray());
		return new JSONObject(content);
	}
	
	public static JSONObject removeOrigin(JSONObject object){
		JSONObject objectTmp = object;
		objectTmp.remove(Cst.TAG_ORIGIN);
		return objectTmp;
	}
	
	public static JSONObject removeOriginUserId(JSONObject object){
		return removeOriginTag(object, Cst.TAG_USER_ID);
	}
	
	public static JSONObject removeOriginClientVersion(JSONObject object){
		return removeOriginTag(object, Cst.TAG_CLIENT_VERSION);
	}
	
	public static JSONObject removeOriginClientType(JSONObject object){
		return removeOriginTag(object, Cst.TAG_CLIENT_TYPE);
	}
	
	public static JSONObject removeOriginModule(JSONObject object){
		return removeOriginTag(object, Cst.TAG_MODULE);
	}
	
	private static JSONObject removeOriginTag(JSONObject object, String tag){
		JSONObject objectTmp = object;
		if (objectTmp.has(Cst.TAG_ORIGIN)){
			JSONObject origin = objectTmp.getJSONObject(Cst.TAG_ORIGIN);
			origin.remove(tag);
			objectTmp.put(Cst.TAG_ORIGIN, origin);
		}
		return objectTmp;
	} 
	
}
