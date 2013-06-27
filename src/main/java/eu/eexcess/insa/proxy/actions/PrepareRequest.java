package eu.eexcess.insa.proxy.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class PrepareRequest implements Processor {

	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		
		//in.getBody(String.class);
		if (in.hasHeaders()){
			System.out.println("le mesage a des headers");
			Object header = in.getHeader("traceId");
			System.out.println(header);
		}

	}

}
