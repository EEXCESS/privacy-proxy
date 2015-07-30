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
 * 
 * @author Thomas Cerqueus
 * @version 1.0
 */
public class CachingTask extends TimerTask {
	
	protected String queryLogLocation = Config.getValue(Config.DATA_DIRECTORY) + Config.getValue(Config.QUERY_LOG);
	protected String cacheCoOccurrenceGraphLocation = Config.getValue(Config.CACHE_DIRECTORY) + Config.getValue(Config.CO_OCCURRENCE_GRAPH_FILE);
	protected String cacheMaximalCliquesLocation = Config.getValue(Config.CACHE_DIRECTORY) + Config.getValue(Config.CLIQUES_FILE);
	
	public CachingTask(){}
	
	@Override
	public void run(){
		File queryLog = new File(queryLogLocation);
		Boolean cogDeleted = false;
		Boolean mcsDeleted = false;
		// TODO Use renaming instead of delete + create
		if (queryLog.exists()){
			// Computes co-occurrence graph
			CoOccurrenceGraph graph = getCoOccurrenceGraphFromQueryLog(queryLog);
			// Computes the maximal cliques from the co-occurrence graph
			List<Clique> cliques = graph.getMaximalCliques();
			// Deletes the existing co-occurrence graph cache
			File cacheCoOccurrenceGraph = new File(cacheCoOccurrenceGraphLocation);
			cogDeleted = cacheCoOccurrenceGraph.delete();
			// Stores the new co-occurrence graph cache
			cacheCoOccurrenceGraph(graph, cacheCoOccurrenceGraph);
			// Deletes the existing maximal cliques cache
			File cacheMaximalCliques = new File(cacheMaximalCliquesLocation);
			mcsDeleted = cacheMaximalCliques.delete();
			// Stores the new maximal cliques cache
			cacheMaximalCliques(cliques, cacheMaximalCliques);
		}
		System.out.println("Caches updated [cog: " + cogDeleted +", mcs: " + mcsDeleted + "]"); // XXX Remove this line
	}
	
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
	
	private void cacheCoOccurrenceGraph(CoOccurrenceGraph graph, File f) {
		try {
			if (!f.exists()){
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(graph.toJsonString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void cacheMaximalCliques(List<Clique> listMaximalCliques, File f){
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

}
