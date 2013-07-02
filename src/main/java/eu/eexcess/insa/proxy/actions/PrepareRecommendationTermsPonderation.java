package eu.eexcess.insa.proxy.actions;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonNode;

public class PrepareRecommendationTermsPonderation implements Processor {

	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public List<Double> obselWeight(JsonNode traceSearchResults) {
		return null;
	}

	public List<String> tokenize(String titleContent) {
		// TODO Auto-generated method stub
		return null;
	}
}
