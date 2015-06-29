package eu.eexcess.insa.peas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import eu.eexcess.Cst;
import eu.eexcess.Util;

import org.jgrapht.alg.BronKerboschCliqueFinder;

/**
 * TODO
 * @author Thomas Cerqueus
 *
 */
public class Graph extends SimpleWeightedGraph<String, DefaultWeightedEdge> {

	private static final long serialVersionUID = 1L;

	public Graph(){
		super(DefaultWeightedEdge.class);

	}
	
	/**
	 * TODO
	 * @param vertex1
	 * @param vertex2
	 */
	public void incrementWeight(String vertex1, String vertex2){
		if (!this.containsVertex(vertex1)){
			this.addVertex(vertex1);
		}
		if (!this.containsVertex(vertex2)){
			this.addVertex(vertex2);
		}
		DefaultWeightedEdge edge = this.getEdge(vertex1, vertex2);
		if (!this.containsEdge(edge)){
			this.addEdge(vertex1, vertex2);
		} else {
			this.setEdgeWeight(edge, this.getEdgeWeight(edge) + 1);
		}
	}
	
	/**
	 * TODO
	 * @return
	 */
	public String toJsonString(){
		String res = "";
		String comma = ", ";
		for (String vertex : this.vertexSet()){
			String term = Util.keyColonValue(Util.quote(Cst.TAG_TERM), Util.quote(vertex));
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
					arrayFrequencies += Util.cBrackets(Util.keyColonValue(Util.quote(Cst.TAG_TERM), Util.quote(trg)) + comma + 
							Util.keyColonValue(Util.quote(Cst.TAG_FREQUENCY), this.getEdgeWeight(associatedEdge))) + comma;
					found = true;
				}
			}
			if (found){
				if (arrayFrequencies.endsWith(comma)){
					// To remove the last comma introduced in the loop
					arrayFrequencies = arrayFrequencies.substring(0, arrayFrequencies.length() - comma.length()); 
				}
				arrayFrequencies = Util.sBrackets(arrayFrequencies);
				String frequencies = Util.keyColonValue(Util.quote(Cst.TAG_FREQUENCIES), arrayFrequencies);
				res += Util.cBrackets(term + comma + frequencies) + comma;
			}
		}
		if (res.endsWith(comma)){
			// To remove the last comma introduced in the loop
			res = res.substring(0, res.length() - comma.length()); 
		}
		res = Util.sBrackets(res);
		return res;			
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Clique> extractMaximalCliques(){
		List<Clique> listMaximalCliques = new ArrayList<Clique>();
		BronKerboschCliqueFinder<String, DefaultWeightedEdge> finder = new BronKerboschCliqueFinder<String, DefaultWeightedEdge>(this);
		Collection<Set<String>> maximalCliques = finder.getAllMaximalCliques();
		for (Set<String> clique : maximalCliques) {
            for (String e : clique){
            	System.out.println("- " + e);
            }
            System.out.println("====");
        }
		return listMaximalCliques;
	}
	
	/*
	private ArrayList<ArrayList<Pair<HashSet<String>, Integer>>> executeBronKerboschAlgo(Multiset<String> wordCounter, HashMap<String, Multiset<String>> cooccurrence) {

        ArrayList<ArrayList<Pair<HashSet<String>, Integer>>> cliques = new ArrayList<>();

        UndirectedGraph<String, DefaultEdge> g = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        for (String vertex : cooccurrence.keySet()) {
            g.addVertex(vertex);
        }
        for (Map.Entry<String, Multiset<String>> entry : cooccurrence.entrySet()) {
            for (String cooccurenteVertex : entry.getValue().elementSet()) {
                g.addEdge(entry.getKey(), cooccurenteVertex);
            }
        }
        BronKerboschCliqueFinder<String, DefaultEdge> finder = new BronKerboschCliqueFinder<>(g);
        for (Set<String> clique : finder.getAllMaximalCliques()) {
            BiMap<String, Integer> wordId = HashBiMap.create(clique.size());
            int k = 0;
            for (String keyword : clique) {
                wordId.put(keyword, k);
                k++;
            }

            int min = -1;
            if (wordId.size() > 1) {
                for (int i = 0; i < clique.size(); i++) {
                    Multiset<String> multiset = cooccurrence.get(wordId.inverse().get(i));
                    if (multiset == null) {
                        continue;
                    }
                    for (int j = i + 1; j < clique.size(); j++) {
                        int count = multiset.count(wordId.inverse().get(j));
                        if (min == -1) {
                            min = count;
                        } else if (count < min) {
                            min = count;
                        }
                    }
                }
            } else {
                min = wordCounter.count(wordId.inverse().get(0));
            }
            if (min == -1) {
                min = 0;
            }
            while (cliques.size() < clique.size() - 1) {
                cliques.add(null);
            }
            if (cliques.size() == clique.size() - 1) {
                cliques.add(new ArrayList<>());
            } else if (cliques.get(clique.size() - 1) == null) {
                cliques.set(clique.size() - 1, new ArrayList<>());
            }
            Pair<HashSet<String>, Integer> pair = new Pair<>(new HashSet<>(clique), min);
            cliques.get(clique.size() - 1).add(pair);
            index.put(pair.getKey(), pair);
        }

        return cliques;
    }
    */
	
}
