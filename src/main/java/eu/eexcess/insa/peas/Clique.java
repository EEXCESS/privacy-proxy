package eu.eexcess.insa.peas;

import org.json.JSONArray;

/**
 * A clique is represented as a weighted graph. 
 * @author Thomas Cerqueus
 */
public class Clique extends CoOccurrenceGraph {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor. 
	 */
	public Clique(){
		super();
	}
	
	/**
	 * TODO
	 * @param jsonGraph
	 */
	public Clique(JSONArray jsonGraph){
		super(jsonGraph);
	}
	
}
