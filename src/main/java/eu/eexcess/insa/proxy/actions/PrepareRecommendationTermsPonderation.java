package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;
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
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrepareRecommendationTermsPonderation implements Processor {
	Logger logger = LoggerFactory.getLogger(PrepareRecommendationTermsPonderation.class);
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	long T = 3600; // time range ( seconds ) to be considered in the calculation
	//( older obsels in the JsonNode parameter won't be taken into consideration )
	double k = 0.01;
	
	double b = (-(k*T)+ Math.sqrt(Math.pow((k*T),2)+4*k*T))/2;
	double B = -1/(k*T+b);

	
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		InputStream is = in.getBody(InputStream.class);

		
		//coefficients are calculated for each term in the obsel's title 
		// *************************************************
		JsonFactory factory = new JsonFactory();
	    JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	   
	   // List<Double> coefficients = obselWeight(rootNode);
	    
	    HashMap<String, Double> ponderatedTerms = new HashMap<String,Double>();
	    JsonNode hitsNode = rootNode.path("hits").path("hits");
	    Iterator<JsonNode> itJson = hitsNode.getElements();
	   // Iterator<Double> itCoef = coefficients.iterator();
	    String titleBuffer ="";
	    String term = "";
	    double coefficient = 0.0;
	    while(itJson.hasNext()){
	    	
	    	coefficient = calcOneObselWeight(itJson.next(), new Date());
	    	titleBuffer = hitsNode.path("_source").path("document").path("title").asText();
	    	List<String> terms = tokenize(titleBuffer);
	    	Iterator<String> itTerms = terms.iterator();
	    	while ( itTerms.hasNext() ){
	    		term = itTerms.next();
	    		if ( ponderatedTerms.containsKey(term)){
	    			double newCoef = ponderatedTerms.get(term)+ coefficient;
	    			if(newCoef>1){
	    				newCoef = 1;
	    			}
	    			ponderatedTerms.remove(term);
	    			ponderatedTerms.put( term, newCoef);
	    		}
	    		else{
	    			ponderatedTerms.put( term, coefficient);
	    		}
	    	}
	    	
	    	
	    }
	    
	    // The coefficients are parsed into a Json structure 
	    //***************************************************
	    //in.setBody
		
		
		
		
	}

	
	
	
	public List<Double> obselWeight(JsonNode traceSearchResults){
		
		return( obselWeight ( traceSearchResults, new Date()));
	}
	
	
	
	/* Calculates each obsel a coefficient based on different parameters :
	 * 	* the duration the page represented by the obsel has been viewed
	 * 	* the time passed since said page have been opened
	 *  
	 * @param traceSearchResults : JsonNode froom the elasticsearch query
	 * @param cDate : the current Date
	 * @return : list containing the calculated coefficients, ordered the same way the obsels where in the JsonNode parameter
	 * 
	 */
	public List<Double> obselWeight(JsonNode traceSearchResults, Date cDate) {
		
		List<Double> coefficients = new ArrayList<Double>();		

		JsonNode hitsJson = traceSearchResults.path("hits").path("hits");
		Iterator<JsonNode> it = hitsJson.getElements();
		while(it.hasNext()) {
			try {
				JsonNode hitJson = it.next();
				Double obs = calcOneObselWeight(hitJson, cDate);

				coefficients.add(obs);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				coefficients.add(0.0);
			}

		}
		
		return coefficients;
	}

	
	private Double calcOneObselWeight(JsonNode hitJson, Date cDate)
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
		double truncatedCoefficient = coefficient - coefficient % 0.001;

		return truncatedCoefficient;
	}




	/*
	 *  @ return : the trace's title as a list of tokens
	 */
	public List<String> tokenize(String titleContent) {

		List<String> result = new ArrayList<String>(); 
		String tokenDeLimiters = " |[]/\'\"@\\&~,;:!?<>#_-.1234567890";
		StringTokenizer tokenizer = new StringTokenizer ( titleContent,tokenDeLimiters,  false );
		while ( tokenizer.hasMoreElements()){
			
			result.add(tokenizer.nextToken());
		}
		
		return result;
	}
}
