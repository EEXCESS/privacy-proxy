package eu.eexcess.insa.peas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.eexcess.Cst;
import eu.eexcess.JsonUtil;

/**
 * A co-occurrence graph is a weighted graph. 
 * Vertices are words, and edges represent the usage frequency between two words. 
 * Each line of the file looks like: {@code timestamp	breathing wheezing}. 
 * The fields correspond to the date the query was stored in the query log, and the list of keywords.  
 * @author Thomas Cerqueus
 * @version 1.0
 */
public class CoOccurrenceGraph extends SimpleWeightedGraph<String, DefaultWeightedEdge> {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor. 
	 */
	public CoOccurrenceGraph(){
		super(DefaultWeightedEdge.class);
	}
	
	/**
	 * Instantiates a co-occurrence graph from a JSON object.  
	 * @param jsonGraph Representation of a co-occurrence graph in JSON. 
	 */
	public CoOccurrenceGraph(JSONArray jsonGraph) {
		super(DefaultWeightedEdge.class);
		for (int i = 0 ; i < jsonGraph.length() ; i++){
			JSONObject entry = jsonGraph.getJSONObject(i);
			String term1 = entry.getString(Cst.TAG_TERM);
			JSONArray frequencyEntries = entry.getJSONArray(Cst.TAG_FREQUENCIES);
			for (int j = 0 ; j < frequencyEntries.length() ; j++){
				JSONObject frequencyEntry = frequencyEntries.getJSONObject(j);
				String term2 =  frequencyEntry.getString(Cst.TAG_TERM);
				Double frequency = frequencyEntry.getDouble(Cst.TAG_FREQUENCY);
				this.addEdge(term1, term2, frequency);
			}
		}
	}
	
	/**
	 * Increments by 1 the weight between {@code vertex1} and {@code vertex2}. 
	 * Vertices are created if they don't already exist in the graph. 
	 * It creates an edge if it didn't existed previously. 
	 * @param vertex1 Label of a vertex. 
	 * @param vertex2 Label of another vertex. 
	 */
	public void incrementWeight(String vertex1, String vertex2){
		
		double weight = 0;
		if (this.getEdge(vertex1, vertex2) != null){
			weight = this.getEdgeWeight(this.getEdge(vertex1, vertex2));
		}
		addEdge(vertex1, vertex2, weight + 1);
	}
	
	/**
	 * Creates an edge between two vertices with a given weight. 
	 * Vertices are created if they don't already exist in the graph. 
	 * @param vertex1 A vertex. 
	 * @param vertex2 Another vertex. 
	 * @param weight The weight between the two vertices. 
	 */
	public void addEdge(String vertex1, String vertex2, double weight){
		if (!this.containsVertex(vertex1)){
			this.addVertex(vertex1);
		}
		if (!this.containsVertex(vertex2)){
			this.addVertex(vertex2);
		}
		DefaultWeightedEdge edge = this.getEdge(vertex1, vertex2);
		if (!this.containsEdge(edge)){
			this.addEdge(vertex1, vertex2);
			edge = this.getEdge(vertex1, vertex2);
		} 
		this.setEdgeWeight(edge, weight); 
	}
	
	/**
	 * Converts a weighted graph into a JSON string. 
	 * The result look like: [
	 * 		{"term": "t1", "frequencies": [{"term": "t2", "frequency": 2}, ..., {"term": "tN", "frequency": 2}]}, 
	 * 		..., 
	 * 		{"term": "tX", "frequencies": [{"term": "tY", "frequency": 5}, ..., {"term": "tZ", "frequency": 3}]}
	 * ]
	 * To limit the size of the string, the frequency between tX and tY is considered if and only if tX < tY. 
	 * @return A JSON string representing the weighted graph. 
	 */
	public String toJsonString(){
		String res = "";
		for (String vertex : this.vertexSet()){
			String term = JsonUtil.keyColonValue(JsonUtil.quote(Cst.TAG_TERM), JsonUtil.quote(vertex));
			String arrayFrequencies = "";
			Set<DefaultWeightedEdge> associatedEdges = this.edgesOf(vertex);
			Boolean found = false;
			for (DefaultWeightedEdge associatedEdge : associatedEdges){
				String src = this.getEdgeSource(associatedEdge);
				String trg = this.getEdgeTarget(associatedEdge);
				if (trg.equals(vertex)){
					trg = src;
					src = vertex;
				}
				if (src.compareTo(trg) < 0){
					arrayFrequencies += JsonUtil.cBrackets(JsonUtil.keyColonValue(JsonUtil.quote(Cst.TAG_TERM), JsonUtil.quote(trg)) + JsonUtil.CS + 
							JsonUtil.keyColonValue(JsonUtil.quote(Cst.TAG_FREQUENCY), this.getEdgeWeight(associatedEdge))) + JsonUtil.CS;
					found = true;
				}
			}
			if (found){
				if (arrayFrequencies.endsWith(JsonUtil.CS)){
					// To remove the last comma introduced in the loop
					arrayFrequencies = arrayFrequencies.substring(0, arrayFrequencies.length() - JsonUtil.CS.length()); 
				}
				arrayFrequencies = JsonUtil.sBrackets(arrayFrequencies);
				String frequencies = JsonUtil.keyColonValue(JsonUtil.quote(Cst.TAG_FREQUENCIES), arrayFrequencies);
				res += JsonUtil.cBrackets(term + JsonUtil.CS + frequencies) + JsonUtil.CS;
			}
		}
		if (res.endsWith(JsonUtil.CS)){
			// To remove the last comma introduced in the loop
			res = res.substring(0, res.length() - JsonUtil.CS.length()); 
		}
		res = JsonUtil.sBrackets(res);
		return res;			
	}
	
	/**
	 * Computes all the maximal cliques contained in the weighted graph.
	 * Weights are not used in the algorithm, but they are kept in the cliques.  
	 * @return A list of maximal cliques. 
	 */
	protected List<Clique> getMaximalCliques(){
		List<Clique> listMaximalCliques = new ArrayList<Clique>();
		BronKerboschCliqueFinder<String, DefaultWeightedEdge> finder = new BronKerboschCliqueFinder<String, DefaultWeightedEdge>(this);
		Collection<Set<String>> maximalCliques = finder.getAllMaximalCliques();
		for (Set<String> cliqueVertices : maximalCliques) {
			Clique clique = new Clique();
            List<String> cliqueVerticesList = new ArrayList<String>(cliqueVertices);
            for (Integer i = 0 ; i < cliqueVerticesList.size() ; i++){
            	String vertex1 = cliqueVerticesList.get(i);
            	for (Integer j = i+1 ; j < cliqueVerticesList.size() ; j++){
            		String vertex2 = cliqueVerticesList.get(j);
            		DefaultWeightedEdge edge = null;
            		if (this.containsEdge(vertex1, vertex2)){
            			edge = this.getEdge(vertex1, vertex2);
            		} else if (this.containsEdge(vertex2, vertex1)){
            			edge = this.getEdge(vertex2, vertex1);
            		}
            		double weight = 0;
            		if (edge != null){
            			weight = this.getEdgeWeight(edge);
            		}
            		clique.addEdge(vertex1, vertex2, weight);
            	}
            }
            listMaximalCliques.add(clique);
        }
		return listMaximalCliques;
	}
	
}
