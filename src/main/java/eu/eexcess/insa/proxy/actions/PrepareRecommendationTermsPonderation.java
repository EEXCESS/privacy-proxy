package eu.eexcess.insa.proxy.actions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonNode;

public class PrepareRecommendationTermsPonderation implements Processor {

	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		
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
		
		long T = 3600; // time range ( seconds ) to be considered in the calculation
		//( older obsels in the JsonNode parameter won't be taken into consideration )
		double k = 0.01;
		
		
		double b = (-(k*T)+ Math.sqrt(Math.pow((k*T),2)+4*k*T))/2;
		double B = -1/(k*T+b);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		
	
		
		
		
		Date begin = null;
		Date end = null;
		JsonNode hitsJson = traceSearchResults.path("hits").path("hits");
		Iterator<JsonNode> it = hitsJson.getElements();
		while(it.hasNext()) {
			JsonNode hitJson = it.next();
			String endDate ="";
			String beginDate = hitJson.path("_source").path("temporal").path("begin").asText();
			if ( ! hitJson.path("_source").path("temporal").path("end").isMissingNode()){
				endDate = hitJson.path("_source").path("temporal").path("end").asText();
			}
			
			try {
			    begin = dateFormat.parse(beginDate);
				if (!endDate.equals("") ){
					end = dateFormat.parse(endDate);
				}
				else{
					end = cDate;
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				coefficients.add(0.0);
			}
			
			if ( begin != null && end != null ){
				
				double beginTrace = (cDate.getTime()-begin.getTime()) / 1000 ;
				double endTrace = (cDate.getTime()-end.getTime())/1000;
				if ( beginTrace >= T ){
					beginTrace = T;		
				}
				if (endTrace >= T) {
					endTrace = T;
				}
				
				double coefficient = (Math.log ( ( k * beginTrace + b ) / ( k * endTrace + b  ) ) * ( 1 / k ) + B * ( beginTrace - endTrace ) )/ (Math.log ( ( k * T + b ) / b  ) * ( 1 / k ) + B * T ) ;
				double truncatedCoefficient = coefficient - coefficient%0.001;
					
				coefficients.add(truncatedCoefficient);
	
			}
		}
		
		return coefficients;
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
