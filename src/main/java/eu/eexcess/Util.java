package eu.eexcess;

/**
 * This class contains a few useful methods. 
 * @author Thomas
 * TODO Javadoc
 */
public class Util {
	
	private static final String QM = "\""; // QM = Quotation Mark
	
	/**
	 * @param o An object
	 * @return A string representing the object framed by square brackets: [o]
	 */
	static public String sBrackets(Object o){
		return Util.brackets("[", o.toString(), "]");
	}
	
	/**
	 * @param o1 An object
	 * @param o2 Another object
	 * @return A string representing the 2 objects framed by square brackets and separated by a colon: [o1:o2] 
	 */
	static public String sBracketsColon(Object o1, Object o2){
		String str = Util.keyColonValue(o1, o2);
		return Util.brackets("[", str, "]");
	}
	
	/**
	 * @param o An object
	 * @return A string representing the object framed by curly brackets: {o}
	 */
	static public String cBrackets(Object o){
		return Util.brackets("{", o.toString(), "}");
	}
	
	/**
	 * 
	 * @param key An object representing a key
	 * @param value An object representing a value
	 * @return A string representing the 2 objects seperacted by a colon: o1:o2
	 */
	static public String keyColonValue(Object key, Object value){
		return key.toString() + ":" + value.toString();
	}
	
	/**
	 * 
	 * @param o An object
	 * @return A string of the object between quotation marks: "o"
	 */
	static public String quote(Object o){
		return Util.QM + o.toString() + Util.QM;
	}
	
	/**
	 * 
	 * @param br1
	 * @param str
	 * @param br2
	 * @return
	 */
	static private String brackets(String br1, String str, String br2){
		return br1 + str + br2;
	}

}
