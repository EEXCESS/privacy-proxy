package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eexcess.insa.lucene.Tokenizer;
import eu.eexcess.insa.proxy.Utils;

public class PrepareRecommendationTermsPonderation implements Processor {
	Logger logger = LoggerFactory.getLogger(PrepareRecommendationTermsPonderation.class);
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	long T = 3600; // time range ( seconds ) to be considered in the calculation
	//( older obsels in the JsonNode parameter won't be taken into consideration )
	double k = 0.01;
	
	double b = (-(k*T)+ Math.sqrt(Math.pow((k*T),2)+4*k*T))/2;
	double B = -1/(k*T+b);

	
	
	
	
	/**
	 * Takes a list of obsels and processes it into 
	 * a list of terms with coefficients
	 * 
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		InputStream is = in.getBody(InputStream.class);

		
		//coefficients are calculated for each term from the obsel's title 
		// ************************************************************
		JsonFactory factory = new JsonFactory();
	    JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	   
	  
	    
	    HashMap<String, Integer> ponderatedTerms = new HashMap<String,Integer>();
	    JsonNode hitsNode = rootNode.path("hits").path("hits");
	    Iterator<JsonNode> itJson = hitsNode.getElements();
	    String titleBuffer ="";
	    String term = "";
	    int coefficient = 0;
	    while(itJson.hasNext()){ //goes over all the obsels in order to extract their title's terms and 
	    	// to give them a coefficient
	    	JsonNode obsel = itJson.next();
	    	coefficient = calcOneObselWeight(obsel, new Date()); 
	    	titleBuffer = obsel.path("_source").path("document").path("title").asText();
	    	
	    	
	    	List<String> terms = tokenize(titleBuffer);
	    	
	    	Iterator<String> itTerms = terms.iterator();
	    	while ( itTerms.hasNext() ){
	    		term = itTerms.next();
	    		if ( ponderatedTerms.containsKey(term)){
	    			int newCoef = ponderatedTerms.get(term)+ coefficient;
	    			
	    			ponderatedTerms.remove(term);
	    			ponderatedTerms.put( term, newCoef);
	    		}
	    		else{
	    			ponderatedTerms.put( term, coefficient);
	    		}
	    	}
	    	
	    	
	    }
	    
	    
	    StringWriter stringWriter = new StringWriter();
	    Utils.writeWeightedQuery(stringWriter, ponderatedTerms);
	    logger.info(ponderatedTerms.toString());
	    in.setBody(stringWriter.toString());

		
		
		
		
	}

	
	
	
	public List<Integer> obselWeight(JsonNode traceSearchResults){
		
		return( obselWeight ( traceSearchResults, new Date()));
	}
	
	
	
	/*//////////////// CURRENTLY UNUSED ///// ( to be removed ?)
	 * Calculates each obsel a coefficient based on different parameters :
	 * 	* the duration the page represented by the obsel has been viewed
	 * 	* the time passed since said page have been opened
	 *  
	 * @param traceSearchResults : JsonNode froom the elasticsearch query
	 * @param cDate : the current Date
	 * @return : list containing the calculated coefficients, ordered the same way the obsels where in the JsonNode parameter
	 * 
	 */
	public List<Integer> obselWeight(JsonNode traceSearchResults, Date cDate) {
		
		//List<Double> coefficients = new ArrayList<Double>();
		List<Integer> coefficients = new ArrayList<Integer>();

		JsonNode hitsJson = traceSearchResults.path("hits").path("hits");
		Iterator<JsonNode> it = hitsJson.getElements();
		while(it.hasNext()) {
			try {
				JsonNode hitJson = it.next();
				//Double obs = calcOneObselWeight(hitJson, cDate);
				int  obs = calcOneObselWeight(hitJson, cDate);

				coefficients.add(obs);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//coefficients.add(0.0);
				coefficients.add(0);
			}

		}
		
		return coefficients;
	}

	/** Attributes a coefficient from 0 to 1 to a given obsel
	 * 
	 * @param hitJson : the JsonNode refering to the obsel taken into consideration
	 * @param cDate = the current date
	 * 
	 * @return : given obsel's relative coefficient
	 * 
	 */
	private int calcOneObselWeight(JsonNode hitJson, Date cDate)
			throws ParseException {
		Date begin = null;
		Date end = null;

		String endDate = "";
		String beginDate = hitJson.path("_source").path("temporal")
				.path("begin").asText();
		if (!hitJson.path("_source").path("temporal").path("end")
				.isMissingNode()) {
			endDate = hitJson.path("_source").path("temporal").path("end")
					.asText();
		}

		begin = dateFormat.parse(beginDate);
		if (!endDate.equals("")) {
			end = dateFormat.parse(endDate);
		} else {
			end = cDate;
		}

		double beginTrace = (cDate.getTime() - begin.getTime()) / 1000;
		double endTrace = (cDate.getTime() - end.getTime()) / 1000;
		if (beginTrace >= T) {
			beginTrace = T;
		}
		if (endTrace >= T) {
			endTrace = T;
		}

		double coefficient = (Math.log((k * beginTrace + b)
				/ (k * endTrace + b))
				* (1 / k) + B * (beginTrace - endTrace))
				/ (Math.log((k * T + b) / b) * (1 / k) + B * T);
		//double truncatedCoefficient = coefficient - coefficient % 0.001;
		double truncatedCoefficient = Math.round(coefficient*300);
		return (int)(truncatedCoefficient);
	}




	/** @param titleContent : the obsel's title to exctract tokens from
	 * 
	 *  @ return : the trace's title as a list of tokens
	 */
	public List<String> tokenize(String titleContent) {

		String utf8String = StringEscapeUtils.unescapeHtml4(titleContent);
		List<String> tokens = Tokenizer.tokenize(utf8String);
		
		return tokens;
	}
}
