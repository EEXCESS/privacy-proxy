package eu.eexcess.insa.peas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import eu.eexcess.Config;
import eu.eexcess.Cst;
import eu.eexcess.Util;

public class Dictionary {

	protected List<String> entries = new ArrayList<String>();
	
	public Dictionary(){
		String path = Config.getValue(Config.DICTIONARY_LOCATION);
		if (!path.startsWith(File.separator)){
			path = File.separator + path;
		}
		init(path);
	}
	
	public Dictionary(String filePath){
		init(filePath);
	}
	
	public void init(String filePath){
		try {
			InputStream in = getClass().getResourceAsStream(filePath); 
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
			String currentLine;
			while ((currentLine = bufferReader.readLine()) != null) {
				entries.add(currentLine);
			}
			bufferReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toJsonString(){
		String jsonStr = "[";
		String comma = ", ";
		for (String entry : entries){
			if (!entry.contains("\"")){
				jsonStr += "{" + Util.quote(Cst.TAG_TERM) + ": " + Util.quote(entry) + "}" + comma;
			}
		}
		if (jsonStr.endsWith(comma)){
			// To remove the last comma introduced in the loop
			jsonStr = jsonStr.substring(0, jsonStr.length() - comma.length()); 
		}
		jsonStr += "]";
		return jsonStr;
	}
	
}
