package eu.eexcess.insa.proxy.actions;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eexcess.insa.lucene.Tokenizer;
import eu.eexcess.insa.proxy.Utils;

public class PrepareRecommendationTermsPonderation implements Processor {
	Logger logger = LoggerFactory.getLogger(PrepareRecommendationTermsPonderation.class);
	// simpledateformat no thread safe : we make a new instnace very time we need it
	//static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); 
	
	long T = 3600; // time range ( seconds ) to be considered in the calculation
	//( older obsels in the JsonNode parameter won't be taken into consideration )
	double k = 0.01;
	
	double b = (-(k*T)+ Math.sqrt(Math.pow((k*T),2)+4*k*T))/2;
	double B = -1/(k*T+b);

	
	
	
	
	/**
	 * Takes a user context to produce
	 * a list of terms with coefficients
	 * 
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		
		String traces = exchange.getProperty("user_context-traces",String.class);
		String userProfile = exchange.getProperty("user_context-profile",String.class);
		
		//System.out.println("traces : \n"+traces);
		//System.out.println("profil : \n"+userProfile);
		
		
		InputStream isTraces = exchange.getProperty("user_context-traces",InputStream.class);
		//String isTraces = exchange.getProperty("user_context-traces",String.class);
		
		InputStream isUserProfile = exchange.getProperty("user_context-profile",InputStream.class);
		//String isUserProfile = exchange.getProperty("user_context-profile",String.class);
		
		
		
		
		HashMap<String, Integer> ponderatedTerms = extractQueryFromTraces(isTraces, exchange);
		extractQueryFromProfile( ponderatedTerms, isUserProfile, exchange); 
		
		
	    StringWriter stringWriter = new StringWriter();
	    Utils.writeWeightedQuery(stringWriter, ponderatedTerms);
	    logger.info(ponderatedTerms.toString());
	    String q = stringWriter.toString();
	    
	    in.setBody(q);
	    logger.info("recommendation query : "+q);
	    exchange.setProperty("recommendation_query", q);
	    in.setHeader("recommendation_query",q);	    //in.setHeader("origin",exchange.getExchangeId());
	}
		
		
		
	
	/*
	 *  The user's profile is assumed to have been already filtered following the privacy settings
	 */
	private HashMap<String, Integer> extractQueryFromProfile ( HashMap<String, Integer> tracesQuery, InputStream is, Exchange exchange ) throws JsonParseException, IOException{
	
		JsonFactory factory = new JsonFactory();
		JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	    
	    
	    // the topics are for now the only information from the user's profile we take in consideration
	    List<String> topics = extractTopicsFromProfile( rootNode );
	    HashMap<String, Integer> ponderatedTopics = ponderateTopics( topics ) ;
	    // the topics are saved into an exchange property to be used later by the specific query mappers

	    exchange.setProperty("ponderated_topics", ponderatedTopics);
	    
		// the query terms aren't modified for now 
		return tracesQuery;
	}
	
	
	private HashMap<String, Integer> ponderateTopics( List<String> topics ){
		
		HashMap<String, Integer> ponderatedTopics = new HashMap<String, Integer>();
		Iterator<String> it = topics.iterator();
		//for now all topics are given a fixed ponderation value
		final int PONDERATION_VALUE = 1;
		while ( it.hasNext()){
			ponderatedTopics.put(it.next(), PONDERATION_VALUE);
		}
		return ponderatedTopics;
		
	}
	
	private List<String> extractTopicsFromProfile ( JsonNode rootNode ){
		ArrayList<String> topics = new ArrayList<String>();
	
		if(!rootNode.path("_source").isMissingNode()){
			if(!rootNode.path("_source").path("topics").isMissingNode()){
				
				JsonNode topicsNode = rootNode.path("_source").path("topics");
				Iterator<JsonNode> it = topicsNode.getElements();
				while ( it.hasNext()){
					topics.add(it.next().path("label").asText());
				}
			}
		}
		
		return topics;
	}
	
		
	private HashMap<String, Integer> extractQueryFromTraces( InputStream is, Exchange exchange ) throws ParseException, JsonParseException, JsonMappingException, IOException{
		//coefficients are calculated for each term from the obsel's title 
		// ************************************************************
		JsonFactory factory = new JsonFactory();
	    JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	   
	    //System.out.println(rootNode);
	    HashMap<String, Integer> ponderatedTerms = new HashMap<String,Integer>();
	    JsonNode hitsNode = rootNode.path("hits").path("hits");
	    Iterator<JsonNode> itJson = hitsNode.getElements();
	    String titleBuffer ="";
	    String term = "";
	    int coefficient = 0;
	    while(itJson.hasNext()){ //goes over all the obsels in order to extract their title's terms and 
	    	// to give them a coefficient
	    	JsonNode obsel = itJson.next();
	    	// the date value needs to be dependant on the trace sent from the front end !
	    	
	    	Date d = new Date ();
	    	String lastTrace_date = exchange.getProperty("lastTrace_date",String.class);
	    	if ( !lastTrace_date.equals("")){
	    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	    		d = dateFormat.parse(lastTrace_date);
	    	}
	    	
	    	
	    	
	    	coefficient = calcOneObselWeight(obsel, d); 
	    	
	    	titleBuffer = obsel.path("_source").path("document").path("title").asText();
	    	
	    	
	    	List<String> terms = tokenize(titleBuffer);
	    	
	    	Iterator<String> itTerms = terms.iterator();
	    	if ( coefficient > 0 ){
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
	    	
	    	
	    }
	   
	    return ponderatedTerms;

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
				
				e.printStackTrace();
				//coefficients.add(0.0);
				coefficients.add(0);
			}

		}
		
		return coefficients;
	}

	/** Attributes a coefficient from 0 to 10 to a given obsel
	 * 
	 * @param hitJson : the JsonNode refering to the obsel taken into consideration
	 * @param cDate = the current date
	 * 
	 * @return : given obsel's relative coefficient
	 * 
	 */
	private int calcOneObselWeight(JsonNode hitJson, Date cDate)
			throws ParseException {
		int highestCoefficient = 10;
		double truncatedCoefficient = 0;
		if ( !hitJson.path("_source").path("events").path("end").isMissingNode()){
			if (hitJson.path("_source").path("events").path("end").asText().equals("active") ){
				return highestCoefficient;
			}
		}
		
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
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		try{
			begin = dateFormat.parse(beginDate);
			if (!endDate.equals("") && endDate != null) {
				end = dateFormat.parse(endDate);
			} else {
				end = cDate;
			}
		}
		
		catch(Exception e){
			System.out.println("begin date : ");
			System.out.println(beginDate);
			System.out.println("endDate : ");
			System.out.println(endDate);
			e.printStackTrace();
			return 0;
		}
		
		if ( begin.equals(cDate) || end.getTime()> cDate.getTime()){ // the obsel is considered active so we give it the highest coefficient
			return highestCoefficient;
		}

		double beginTrace = (cDate.getTime() - begin.getTime()) / 1000;
		double endTrace = (cDate.getTime() - end.getTime()) / 1000;
		if (beginTrace >= T) {
			beginTrace = T;
		}
		if (endTrace >= T) {
			endTrace = T;
			// better return 0 ?
		}

		double coefficient = (Math.log((k * beginTrace + b)
				/ (k * endTrace + b))
				* (1 / k) + B * (beginTrace - endTrace))
				/ (Math.log((k * T + b) / b) * (1 / k) + B * T);
		//double truncatedCoefficient = coefficient - coefficient % 0.001;
		
		truncatedCoefficient = Math.round(coefficient*10);
			
		
		
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
