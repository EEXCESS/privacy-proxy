package eu.eexcess.insa.peas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import eu.eexcess.Config;
import eu.eexcess.Cst;

/**
 * This class is used to read caches. It allows reading the co-occurrence graph cache and the maximal cliques graph. 
 * The locations of cache files are given in the configuration file. 
 * @author Thomas Cerqueus
 * @version 2.0
 */
public class CacheReader {

	private static volatile CacheReader instance = null;
	
	protected String cacheCoOccurrenceGraphLocation = Cst.CATALINA_BASE + Config.getValue(Config.CACHE_DIRECTORY) + Config.getValue(Config.CO_OCCURRENCE_GRAPH_FILE);
	protected String cacheMaximalCliquesLocation = Cst.CATALINA_BASE + Config.getValue(Config.CACHE_DIRECTORY) + Config.getValue(Config.CLIQUES_FILE);
	/**
	 * Default constructor. 
	 */
	private CacheReader(){}
	
	/**
	 * Method used in the implementation of the Singleton pattern. 
	 * @return An instance of {@code CacheReader}.
	 */
	public static CacheReader getInstance(){
		if (instance == null){
			instance = new CacheReader();
		}
		return instance;
	}
	
	/**
	 * Reads the cache and returns the co-occurrence graph. 
	 * The graph is empty if the cache does not exist yet. 
	 * @return A co-occurrence graph. 
	 */
	public CoOccurrenceGraph getCoOccurrenceGraph() {
		CoOccurrenceGraph graph = new CoOccurrenceGraph();
		try {
			File file = new File(cacheCoOccurrenceGraphLocation);
			if (file.exists()){
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferReader = new BufferedReader(fileReader);
				String jsonString = ""; 
				String currentLine;
				while ((currentLine = bufferReader.readLine()) != null) {
					jsonString += currentLine;
				}
				bufferReader.close();
				JSONArray jsonGraph = new JSONArray(jsonString);
				graph  = new CoOccurrenceGraph(jsonGraph);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return graph;
	}

	/**
	 * Reads the cache and returns a list of maximal cliques. 
	 * The list is empty if the cache does not exist yet. 
	 * @return A list of cliques. 
	 */
	public List<Clique> getMaximalCliques() {
		List<Clique> listCliques = new ArrayList<Clique>();
		File file = new File(cacheMaximalCliquesLocation);
		if (file.exists()){
			try {
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferReader = new BufferedReader(fileReader);
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
		}
		return listCliques;
	}

}
