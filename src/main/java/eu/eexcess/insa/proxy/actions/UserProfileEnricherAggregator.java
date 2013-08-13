package eu.eexcess.insa.proxy.actions;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class UserProfileEnricherAggregator implements AggregationStrategy {

	/*
	 * This aggregator enriches the original exchange by setting properties with informations from the ressource exchange
	 * 
	 * These informations are the user's profile & the corresponding privacy settings
	 * The original is unchanged
	 * (non-Javadoc)
	 * @see org.apache.camel.processor.aggregate.AggregationStrategy#aggregate(org.apache.camel.Exchange, org.apache.camel.Exchange)
	 */
	
	public Exchange aggregate(Exchange originalExchange, Exchange ressourceExchange) {
		
		InputStream is = ressourceExchange.getIn().getBody(InputStream.class);
		JsonFactory factory = new JsonFactory();
		JsonNode rootNode = null;
	    try {
	    	JsonParser jp = factory.createJsonParser(is);
		    ObjectMapper mapper = new ObjectMapper();
			rootNode = mapper.readValue(jp, JsonNode.class);
		} catch (JsonParseException e) {
			
			e.printStackTrace();
		} catch (JsonMappingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	    HashMap<String,Integer> privacySettings = getPrivacySettings(rootNode);
	    originalExchange.setProperty("privacy_settings", privacySettings);
	    originalExchange.setProperty("user_context-profile",rootNode);
		
		return originalExchange;
	}
	
	
	/**
	 * This gets the privacy settings from the user profile 
	 * @param rootNode the raw elasticsearch response as a JsonNode, contains the user's profile
	 * @return a hashmap mapping each privacy setting with its numerical value
	 */
	private HashMap<String, Integer> getPrivacySettings ( JsonNode rootNode){
		if ( rootNode == null ){
			return null;
		}
		HashMap<String, Integer> privacySettings = new HashMap<String, Integer>();
		if ( !rootNode.path("hits").isMissingNode()){
			if(!rootNode.path("hits").path("hits").isMissingNode()){
				if( rootNode.path("hits").path("hits").get(0) != null){
					if(!rootNode.path("hits").path("hits").get(0).path("_source").isMissingNode()){
						if(!rootNode.path("hits").path("hits").get(0).path("_source").path("privacy").isMissingNode()){
							Iterator<String> it = rootNode.path("hits").path("hits").get(0).path("_source").path("privacy").getFieldNames();
							while(it.hasNext()){
								String fieldName = it.next();
							
								//if ( rootNode.path("hits").path("hits").get(0).path("_source").path("privacy").path(fieldName).isIntegralNumber() ){
									int value = rootNode.path("hits").path("hits").get(0).path("_source").path("privacy").path(fieldName).asInt();
									
									privacySettings.put(fieldName, value);
									
								//}
								
								
							}
							
						}
					}
				}
			}
		}	
		return privacySettings;
	}

}
