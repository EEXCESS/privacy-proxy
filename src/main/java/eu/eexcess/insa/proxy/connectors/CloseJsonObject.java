package eu.eexcess.insa.proxy.connectors;

import org.apache.camel.Exchange;

import org.apache.camel.Processor;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.util.TokenBuffer;

public class CloseJsonObject implements Processor { 

	
	public void process(Exchange exchange) throws Exception {
		
		TokenBuffer buffer = exchange.getIn().getBody(TokenBuffer.class);
		buffer.writeEndArray();
		buffer.writeEndObject();
		buffer.close();
		
		ObjectMapper mapper = new ObjectMapper();
		String res = mapper.writeValueAsString(mapper.readTree(buffer.asParser()));
		exchange.getIn().setBody(res);
	}

}
