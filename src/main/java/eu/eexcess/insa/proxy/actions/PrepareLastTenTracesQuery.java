package eu.eexcess.insa.proxy.actions;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class PrepareLastTenTracesQuery implements Processor{

	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		in.setHeader(Exchange.HTTP_METHOD,"POST");
		
		String query = "{\"query\": {\"bool\": {\"must\": [{\"match_all\": {}}]}},\"from\": 0,\"size\": 10,\"sort\": [{\"temporal.begin\": \"desc\"}]}";
		
		
		in.setBody(query);
		
		
		
	}

}
