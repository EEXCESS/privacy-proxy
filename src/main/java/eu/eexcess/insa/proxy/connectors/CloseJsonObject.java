package eu.eexcess.insa.proxy.connectors;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
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
