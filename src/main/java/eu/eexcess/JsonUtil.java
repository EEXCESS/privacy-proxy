package eu.eexcess;

/**
 * This class contains methods to create JSON content.  
 * @author Thomas Cerqueus
 */
public class JsonUtil {
	
	public static final String QM = "\""; // QM = Quotation Mark
	public static final String CS = ", "; // CM = Comma with Space
	
	/**
	 * Converts {@code o} to a String and surrounds it with "[" and "]". 
	 * @param o An object
	 * @return A string representing the object framed by square brackets: [o]
	 */
	static public String sBrackets(Object o){
		return JsonUtil.brackets("[", o.toString(), "]");
	}
	
	/**
	 * Converts {@code o} to a String and surrounds it with "{" and "}". 
	 * @param o An object
	 * @return A string representing the object framed by curly brackets: {o}
	 */
	static public String cBrackets(Object o){
		return JsonUtil.brackets("{", o.toString(), "}");
	}
	
	/**
	 * Creates a string of the form {@code key: value}.  
	 * @param key An object representing a key
	 * @param value An object representing a value
	 * @return A string representing the 2 objects separated by a colon: o1:o2
	 */
	static public String keyColonValue(Object key, Object value){
		return key.toString() + ":" + value.toString();
	}
	
	/**
	 * Converts {@code o} to a String and surrounds it with quotes.   
	 * @param o An object
	 * @return A string of the object between quotation marks: "o"
	 */
	static public String quote(Object o){
		return JsonUtil.QM + o.toString() + JsonUtil.QM;
	}
	
	/**
	 * Puts {@code str} between {@code br1} and {@code br2}. 
	 * @param br1 A symbol (or string). 
	 * @param str A string to be surrounded. 
	 * @param br2 Another symbol (or string).
	 * @return The string {@code str} surrounded by {@code br1} and {@code br2}. 
	 */
	static private String brackets(String br1, String str, String br2){
		return br1 + str + br2;
	}

}
