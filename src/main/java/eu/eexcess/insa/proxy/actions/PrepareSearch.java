package eu.eexcess.insa.proxy.actions;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class PrepareSearch implements Processor {

	public void process(Exchange exchange) throws Exception {

		Message in = exchange.getIn();
		in.removeHeader(Exchange.HTTP_BASE_URI);
		in.removeHeader(Exchange.HTTP_PATH);
		in.removeHeader(Exchange.HTTP_URI);
		in.removeHeader("CamelHttpUrl");
		in.removeHeader("CamelServletContextPath");
		//in.setHeader(Exchange.HTTP_URI, value)
		in.removeHeader("CamelHttpServletRequest");
		String body = in.getBody(String.class);
		in.setHeader("ElasticType", "trace");
		in.setHeader("ElasticIndex", "privacy");
		in.setHeader(Exchange.HTTP_METHOD, "POST");
		body = "{\"query\":{\"bool\":{\"must\":["+body+"]}}}"; //must or should ?
		in.setBody(body);
		

	}

}
