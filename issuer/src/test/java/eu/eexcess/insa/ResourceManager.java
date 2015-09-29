package eu.eexcess.insa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

public class ResourceManager {

	public ResourceManager(){}
	
	public JSONObject getContent(String fileName){
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
	
}
