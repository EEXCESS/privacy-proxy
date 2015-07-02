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
import eu.eexcess.JsonUtil;

/**
 * A dictionary is a list of words. 
 * This class is mainly used for tests. 
 * The final version of PEAS should consider the group profile. 
 * @author Thomas Cerqueus
 *
 */
public class Dictionary {

	protected List<String> entries = new ArrayList<String>();
	
	/**
	 * Default constructor. 
	 * It creates the dictionary from the file specified in the configuration file. 
	 */
	public Dictionary(){
		String path = Config.getValue(Config.DICTIONARY_LOCATION);
		init(path);
	}
	
	/**
	 * Default constructor. 
	 * It creates the dictionary from {@code filePath}. 
	 * @param filePath File path of a dictionary (file with 1 word per line). 
	 */
	public Dictionary(String filePath){
		init(filePath);
	}
	
	private void init(String filePath){
		if (!filePath.startsWith(File.separator)){
			filePath = File.separator + filePath;
		}
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
	
	/**
	 * Converts a dictionary into a JSON string. 
	 * The result looks like: [{"term": "t1"}, ..., {"term": "tN"}]
	 * @return A JSON string representing the dictionary. 
	 */
	public String toJsonString(){
		String jsonStr = "";
		for (String entry : entries){
			if (!entry.contains(JsonUtil.QM)){
				jsonStr += JsonUtil.cBrackets(JsonUtil.keyColonValue(JsonUtil.quote(Cst.TAG_TERM), JsonUtil.quote(entry))) + JsonUtil.CS;
			}
		}
		if (jsonStr.endsWith(JsonUtil.CS)){
			// To remove the last comma introduced in the loop
			jsonStr = jsonStr.substring(0, jsonStr.length() - JsonUtil.CS.length()); 
		}
		jsonStr = JsonUtil.sBrackets(jsonStr);
		return jsonStr;
	}
	
}
