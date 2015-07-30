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

/**
 * 
 * @author Thomas Cerqueus
 * @version 2.0
 */
public class CacheReader {

	private static volatile CacheReader instance = null;
	
	protected String cacheCoOccurrenceGraphLocation = Config.getValue(Config.CACHE_DIRECTORY) + Config.getValue(Config.CO_OCCURRENCE_GRAPH_FILE);
	protected String cacheMaximalCliquesLocation = Config.getValue(Config.CACHE_DIRECTORY) + Config.getValue(Config.CLIQUES_FILE);
	
	private CacheReader(){}
	
	public static CacheReader getInstance(){
		if (instance == null){
			instance = new CacheReader();
		}
		return instance;
	}
	
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
				graph.instanciateFromJson(jsonGraph);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return graph;
	}

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
