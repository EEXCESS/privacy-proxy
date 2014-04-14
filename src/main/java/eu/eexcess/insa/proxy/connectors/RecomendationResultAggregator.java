package eu.eexcess.insa.proxy.connectors;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.TimeoutAwareAggregationStrategy;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/*
 * This processor cumulates results from different sources
 * and interleaves them
 * 
 * Example:
 *  With the two following result streams
 *    M1, M2, M3, ....
 *    E1, E2, E3, ....
 *  
 *  We return
 *     M1, E1, M2, E2, M3, E3, ....
 * 
 * 
 * We use a SOURCE_SEEN header to remember the number of sources already interleaved	
 * The sources may have different number of results
 * 
 */
public class RecomendationResultAggregator implements TimeoutAwareAggregationStrategy {
	JsonFactory factory = new JsonFactory();
	ObjectMapper mapper = new ObjectMapper();
	
	public Exchange aggregate(Exchange cumulExchange, Exchange singleExchange) {
		Exchange resultExchange = null;		
		try {
			// Load and parse results from new source
			String newResultsJSON = singleExchange.getIn().getBody(String.class);
			if(newResultsJSON == null) {
				cumulExchange = resultExchange;
			} else {
				JsonParser jp = factory.createJsonParser(newResultsJSON);
				JsonNode newResultNode = mapper.readValue(jp, JsonNode.class);
				JsonNode newResultsDoc = newResultNode.path("documents");
				
				// Merge (interleave) results with eventually existing results from other sources
				Integer sourceCount = null;
				LinkedList<JsonNode> mergedArray = null;
				if( cumulExchange == null ) {
					// No previous results to interleave with
					sourceCount = 0;
					resultExchange = singleExchange;
					mergedArray = new LinkedList<JsonNode>();
					Iterator<JsonNode> it = newResultsDoc.getElements();
					while(it.hasNext()) {
						mergedArray.add(it.next());
					}
				} else {
					Message cumulIn = cumulExchange.getIn();
					sourceCount = cumulIn.getHeader("SOURCE_COUNT",Integer.class);
					Integer expectedSize = null;
					{
						@SuppressWarnings("unchecked")
						LinkedList<JsonNode> cumulatedResults = cumulExchange.getIn().getBody(LinkedList.class);
						
						// Calculate new size 
						Integer prevMaxSize = cumulatedResults.size() / sourceCount;
						Integer newMaxSize  = newResultsDoc.size() > prevMaxSize ? newResultsDoc.size() : prevMaxSize;
						
						// Ensure that the exsting wumulated results have tail nulls
						for(int i=cumulatedResults.size();i<newMaxSize*sourceCount; i++) {
							cumulatedResults.add(i,null);
						}
						assert(cumulatedResults.size() == newMaxSize * sourceCount);
						expectedSize = newMaxSize*(sourceCount+1);
						mergedArray = cumulatedResults;
					}
					
					int insertIndex = sourceCount;
					Iterator<JsonNode> it = newResultsDoc.getElements();
					while(it.hasNext()) {
						mergedArray.add(insertIndex, it.next());
						insertIndex += (sourceCount + 1);
					}
					
					// Ensure new array is complete
					for(int i=mergedArray.size();i<expectedSize; i++) {
						mergedArray.add(i,null);
					}
					resultExchange = cumulExchange;
					
				}
		
				Message resultMessage = resultExchange.getIn();
				resultMessage.setBody(mergedArray);
				resultMessage.setHeader("SOURCE_COUNT",sourceCount + 1);
				resultExchange.setIn(resultMessage);
			}

		} catch (JsonParseException e) {
			e.printStackTrace();
			return cumulExchange;
			
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return cumulExchange;
		} catch (IOException e) {
			e.printStackTrace();
			return cumulExchange;
		}
		return resultExchange;
	}
	
	public void timeout(Exchange oldExchange, int index, int total, long timeout) {
	}

}
