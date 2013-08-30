package eu.eexcess.insa.proxy.actions;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class GetUserId implements Processor {

	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String response = in.getBody( String.class );
		
		String subString = response.substring(response.indexOf("_id")+6);
		String id = subString.substring(0,subString.indexOf("\""));
		in.setBody(id);
		//System.out.println(id);
		exchange.setProperty("user_id", id);
		
	}

}
