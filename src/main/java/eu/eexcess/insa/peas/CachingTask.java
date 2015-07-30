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
import java.util.TimerTask;

import eu.eexcess.Config;
import eu.eexcess.Cst;
import eu.eexcess.JsonUtil;

/**
 * This class is used to put the co-occurrence graph and the maximal cliques in cache. 
 * They are computed from the query log. 
 * @author Thomas Cerqueus
 * @version 1.0
 */
public class CachingTask extends TimerTask {
	
	// Query log
	protected String queryLogLocation = Config.getValue(Config.DATA_DIRECTORY) + Config.getValue(Config.QUERY_LOG);
	// Co-occurrence graph cache
	protected String cacheCoOccurrenceGraphLocation = Config.getValue(Config.CACHE_DIRECTORY) + Config.getValue(Config.CO_OCCURRENCE_GRAPH_FILE);
	protected String tmpCacheCoOccurrenceGraphLocation = Config.getValue(Config.CACHE_DIRECTORY) + Cst.TMP_FILE_PREFIX + Config.getValue(Config.CO_OCCURRENCE_GRAPH_FILE);
	// Maximal cliques cache
	protected String cacheMaximalCliquesLocation = Config.getValue(Config.CACHE_DIRECTORY) + Config.getValue(Config.CLIQUES_FILE);
	protected String tmpCacheMaximalCliquesLocation = Config.getValue(Config.CACHE_DIRECTORY) + Cst.TMP_FILE_PREFIX + Config.getValue(Config.CLIQUES_FILE);
	
	/**
	 * Default constructor. 
	 */
	public CachingTask(){}
	
	/**
	 * Performs the caching of the co-occurrence graph and the maximal cliques. 
	 * This method does not have to be called explicitly, as a scheduler is supposed to do it. 
	 */
	@Override
	public void run(){
		File queryLog = new File(queryLogLocation);
		if (queryLog.exists()){
			// Computes co-occurrence graph
			CoOccurrenceGraph graph = getCoOccurrenceGraphFromQueryLog(queryLog);
			// Computes the maximal cliques from the co-occurrence graph
			List<Clique> cliques = graph.getMaximalCliques();
			// Stores the new co-occurrence graph cache
			File cacheCoOccurrenceGraph = new File(tmpCacheCoOccurrenceGraphLocation);
			cacheCoOccurrenceGraph(graph, cacheCoOccurrenceGraph);
			cacheCoOccurrenceGraph.renameTo(new File(cacheCoOccurrenceGraphLocation));
			// Stores the new maximal cliques cache
			File cacheMaximalCliques = new File(tmpCacheMaximalCliquesLocation);
			cacheMaximalCliques(cliques, cacheMaximalCliques);
			cacheMaximalCliques.renameTo(new File(cacheMaximalCliquesLocation));
		}
		System.out.println(this.getClass() + " " + this.hashCode());
	}
	
	/**
	 * Extracts the co-occurrence graph from the query log.  
	 * @param queryLog File containing the query log. 
	 * @return A co-occurrence graph. 
	 */
	private CoOccurrenceGraph getCoOccurrenceGraphFromQueryLog(File queryLog) {
		CoOccurrenceGraph graph = new CoOccurrenceGraph();
		try {
			FileReader fileReader = new FileReader(queryLog);
			BufferedReader bufferReader = new BufferedReader(fileReader);

			String currentLine;
			while ((currentLine = bufferReader.readLine()) != null) {
				String[] arrayLine = currentLine.split(Cst.COLUMN_SEPARATOR);
				String query = arrayLine[1];
				String[] keywords = query.split(Cst.KEYWORDS_SEPARATOR);
				List<String> filteredKeywords = new ArrayList<String>();
				// Filtering the keywords
				for (int i = 0 ; i < keywords.length ; i++){
					if (keywords[i].matches(Cst.MATCHING_CRITERION)){ 
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
								graph.incrementWeight(w1, w2);
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
		return graph;
	}
	
	/**
	 * Puts a co-occurrence graph in cache. 
	 * @param graph The co-occurrence graph to be cached. 
	 * @param file The file in which the graph must be cached. 
	 */
	private void cacheCoOccurrenceGraph(CoOccurrenceGraph graph, File file) {
		try {
			if (!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(graph.toJsonString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Puts a list of cliques in cache. 
	 * @param listMaximalCliques The list of cliques to be cached. 
	 * @param file The file in which the cliques must be cached. 
	 */
	private void cacheMaximalCliques(List<Clique> listMaximalCliques, File file){
		String jsonCliques = "";
		for (Clique clique : listMaximalCliques){
			jsonCliques += clique.toJsonString() + JsonUtil.CS;
		}
		if (jsonCliques.endsWith(JsonUtil.CS)){
			jsonCliques = jsonCliques.substring(0, jsonCliques.length() - JsonUtil.CS.length());
		}
		jsonCliques = JsonUtil.sBrackets(jsonCliques);
		try {
			if (!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(jsonCliques);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
