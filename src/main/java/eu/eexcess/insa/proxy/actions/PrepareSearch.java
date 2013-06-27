package eu.eexcess.insa.proxy.actions;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class PrepareSearch implements Processor {

	public void process(Exchange exchange) throws Exception {

		Message in = exchange.getIn();
		String body = in.getBody(String.class);
		
		body = "{\"query\":{\"bool\":{\"should\":["+body+"]}}}";
		in.setBody(body);
		
		//in.setHeader("Content-Type","text/html");
		//in.setBody("<ul><li>toto</li><li>titi</li></ul>");
	}

}
