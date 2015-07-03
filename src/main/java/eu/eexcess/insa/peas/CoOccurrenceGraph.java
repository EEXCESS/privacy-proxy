package eu.eexcess.insa.peas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import eu.eexcess.Config;
import eu.eexcess.Cst;
import eu.eexcess.JsonUtil;

/**
 * A co-occurrence graph is a weighted graph. 
 * Vertices are words, and edges represent the usage frequency between two words. 
 * This implementation is somehow limited to the format used in the AOL dataset. 
 * Each line of the file looks like: {@code 6205206	443567	breathing wheezing	0}. 
 * The fields correspond to the query ID, the user ID, a list of keywords, and a tag (not relevant here).  
 * @author Thomas Cerqueus
 *
 */
public class CoOccurrenceGraph extends WeightedGraph {

	private static final long serialVersionUID = 1L;
	private static final String COLUMN_SEPARATOR = "\t";
	private static final String KEYWORDS_SEPARATOR = " ";
	private static final String MATCHING_CRITERION = "^\\w{3,}$"; // Words with at least 3 characters

	/**
	 * Default constructor. 
	 * It creates the group profile from the history file defined in the configuration file.  
	 */
	public CoOccurrenceGraph(){
		super();
		String path = Config.getValue(Config.HISTORY_LOCATION);
		init(path);
	}

	/**
	 * Constructor. Creates the group profile from {@code path}. 
	 * @param path Location of the history file.  
	 */
	public CoOccurrenceGraph(String path){
		super();
		init(path);
	}

	private void init(String path){
		if (!path.startsWith(File.separator)){
			path = File.separator + path;
		}
		try {
			InputStream in = getClass().getResourceAsStream(path); 
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
			String currentLine;
			while ((currentLine = bufferReader.readLine()) != null) {
				// That's where the class gets AOL-like specific: 
				String[] arrayLine = currentLine.split(COLUMN_SEPARATOR);
				String query = arrayLine[2];
				String[] keywords = query.split(KEYWORDS_SEPARATOR);
				List<String> filteredKeywords = new ArrayList<String>();
				// Filtering the keywords
				for (int i = 0 ; i < keywords.length ; i++){
					if (keywords[i].matches(MATCHING_CRITERION)){ 
						filteredKeywords.add(keywords[i].toLowerCase());
					}
				}
				if (filteredKeywords.size() > 1){
					// Add the keywords to the graph
					for (int i = 0 ; i < filteredKeywords.size() ; i++){
						for (int j = (i+1) ; j < filteredKeywords.size() ; j++){
							String w1 = filteredKeywords.get(i);
							String w2 = filteredKeywords.get(j);
							if (!w1.equals(w2)){
								this.incrementWeight(w1, w2);
							}
						}
					}
				}
			}
			bufferReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Uses a cache to save computation time and resources. 
	 * @return A list of maximal cliques. 
	 */
	public List<Clique> getMaximalCliques(){
		List<Clique> listMaximalCliques = new ArrayList<Clique>();
		String cacheLocation = Config.getValue(Config.CLIQUES_CACHE_LOCATION);
		File f = new File(cacheLocation);
		Boolean inCache = f.exists();
		if (inCache){
			// If the cliques are cached, we just have to load the content of the cache
			listMaximalCliques = readCliquesCache(f);
		} else {
			// If the cliques are not cached, we have to do the computation and cache the result
			listMaximalCliques = super.getMaximalCliques();
			writeCliquesCache(f, listMaximalCliques);
		}
		return listMaximalCliques;
	}

	private void writeCliquesCache(File f, List<Clique> listMaximalCliques) {
		String jsonCliques = "";
		for (Clique clique : listMaximalCliques){
			jsonCliques += clique.toJsonString() + JsonUtil.CS;
		}
		if (jsonCliques.endsWith(JsonUtil.CS)){
			jsonCliques = jsonCliques.substring(0, jsonCliques.length() - JsonUtil.CS.length());
		}
		jsonCliques = JsonUtil.sBrackets(jsonCliques);
		try {
			f.createNewFile();
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(jsonCliques);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<Clique> readCliquesCache(File f) {
		List<Clique> listCliques = new ArrayList<Clique>();
		try {
			BufferedReader bufferReader = new BufferedReader(new FileReader(f));
			String jsonString = ""; 
			String currentLine;
			while ((currentLine = bufferReader.readLine()) != null) {
				jsonString += currentLine;
			}
			JSONArray cliques = new JSONArray(jsonString);
			for (int i = 0 ; i < cliques.length() ; i++){
				listCliques.add(JsonToClique(cliques.getJSONArray(i)));
			}
			bufferReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listCliques;
	}

	private Clique JsonToClique(JSONArray jsonClique) {
		Clique clique = new Clique();
		for (int i = 0 ; i < jsonClique.length() ; i++){
			JSONObject entry = jsonClique.getJSONObject(i);
			String term1 = entry.getString(Cst.TAG_TERM);
			JSONArray frequencyEntries = entry.getJSONArray(Cst.TAG_FREQUENCIES);
			for (int j = 0 ; j < frequencyEntries.length() ; j++){
				JSONObject frequencyEntry = frequencyEntries.getJSONObject(j);
				String term2 =  frequencyEntry.getString(Cst.TAG_TERM);
				Double frequency = frequencyEntry.getDouble(Cst.TAG_FREQUENCY);
				clique.addEdge(term1, term2, frequency);
			}
		}
		return clique;
	}

}
