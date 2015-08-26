package eu.eexcess.insa.peas;

import org.json.JSONArray;

/**
 * A clique is represented as a weighted graph. 
 * @author Thomas Cerqueus
 * @version 1.0
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
	 * Instantiates a clique from a JSON object.  
	 * @param jsonGraph Representation of a clique in JSON. 
	 */
	public Clique(JSONArray jsonGraph){
		super(jsonGraph);
	}
	
}
