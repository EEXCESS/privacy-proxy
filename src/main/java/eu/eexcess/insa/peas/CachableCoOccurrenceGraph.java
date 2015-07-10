package eu.eexcess.insa.peas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

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
public class CachableCoOccurrenceGraph extends CoOccurrenceGraph {

	private static final long serialVersionUID = 1L;
	private static final String COLUMN_SEPARATOR = "\t";
	private static final String KEYWORDS_SEPARATOR = " ";
	private static final String MATCHING_CRITERION = "^\\w{3,}$"; // Words with at least 3 characters

	/**
	 * Default constructor. 
	 * It creates the group profile from the history file defined in the configuration file.  
	 */
	public CachableCoOccurrenceGraph(String queryLogLocation, String cacheLocation){
		super();
		File cache = new File(cacheLocation);
		Boolean inCache = cache.exists();
		if (inCache){
			getCoOccurrenceGraphFromCache(cache);
		} else {
			File queryLog = new File(queryLogLocation);
			if (queryLog.exists()){
				loadQueryLog(queryLog);
				cacheCoOccurrenceGraph(cache);
			}
		}
	}

	/**
	 * TODO
	 * @param cache
	 */
	private void getCoOccurrenceGraphFromCache(File cache) {
		try {
			BufferedReader bufferReader = new BufferedReader(new FileReader(cache));
			String jsonString = ""; 
			String currentLine;
			while ((currentLine = bufferReader.readLine()) != null) {
				jsonString += currentLine;
			}
			bufferReader.close();
			JSONArray jsonGraph = new JSONArray(jsonString);
			instanciateFromJson(jsonGraph);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO
	 * @param queryLog
	 */
	private void loadQueryLog(File queryLog) {
		try {
			FileReader fileReader = new FileReader(queryLog);
			BufferedReader bufferReader = new BufferedReader(fileReader);

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
	 * TODO
	 * @param f
	 */
	private void cacheCoOccurrenceGraph(File f) {
		try {
			if (!f.exists()){
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(this.toJsonString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Uses a cache to save computation time and resources. 
	 * @return A list of maximal cliques. 
	 */
	public List<Clique> getMaximalCliques(String cacheCliquesLocation){
		List<Clique> listMaximalCliques = new ArrayList<Clique>();
		File cache = new File(cacheCliquesLocation);
		Boolean inCache = cache.exists();
		if (inCache){
			// If the cliques are cached, we just have to load the content of the cache
			listMaximalCliques = getCliquesFromCache(cache);
		} else {
			// If the cliques are not cached, we have to do the computation and cache the result
			listMaximalCliques = super.getMaximalCliques();
			cacheCliques(cache, listMaximalCliques);
		}
		return listMaximalCliques;
	}

	/**
	 * TODO
	 * @param f
	 * @param listMaximalCliques
	 */
	private void cacheCliques(File f, List<Clique> listMaximalCliques) {
		String jsonCliques = "";
		for (Clique clique : listMaximalCliques){
			jsonCliques += clique.toJsonString() + JsonUtil.CS;
		}
		if (jsonCliques.endsWith(JsonUtil.CS)){
			jsonCliques = jsonCliques.substring(0, jsonCliques.length() - JsonUtil.CS.length());
		}
		jsonCliques = JsonUtil.sBrackets(jsonCliques);
		try {
			if (!f.exists()){
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(jsonCliques);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO
	 * @param f
	 * @return
	 */
	private List<Clique> getCliquesFromCache(File f) {
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
				Clique clique = new Clique(cliques.getJSONArray(i));
				listCliques.add(clique);
			}
			bufferReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listCliques;
	}

}
