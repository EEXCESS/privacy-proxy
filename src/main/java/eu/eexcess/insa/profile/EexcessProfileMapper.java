package eu.eexcess.insa.profile;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonNode;

public class EexcessProfileMapper implements Processor {


	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		JsonNode userGivenProfile = exchange.getProperty("profile.eexcess",JsonNode.class);
		in.setBody(userGivenProfile);
	}

	
}
