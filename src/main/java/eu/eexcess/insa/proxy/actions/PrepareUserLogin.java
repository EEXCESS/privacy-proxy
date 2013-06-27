package eu.eexcess.insa.proxy.actions;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;


public class PrepareUserLogin implements Processor {

	public void process(Exchange exchange) throws Exception {

		Message in = exchange.getIn();
		String body = in.getBody(String.class);
		
		
		String query = "{\"query\":{\"bool\":{\"must\":["+body+"]}},\"from\":0,\"size\":50,\"sort\":[],\"facets\":{}}";
		in.setBody(query);

	}

}
