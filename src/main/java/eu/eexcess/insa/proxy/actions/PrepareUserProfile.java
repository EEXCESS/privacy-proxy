package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;
import java.io.StringWriter;


import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class PrepareUserProfile implements Processor {
	static JsonFactory factory = new JsonFactory();
	public void process(Exchange exchange) throws Exception {
		
		Message in = exchange.getIn();
		InputStream is = in.getBody(InputStream.class);
		
		
		
	    JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	    JsonNode entryNode = rootNode.path("hits").path("hits").path(0);

	    StringWriter sWriter = new StringWriter();
	    JsonGenerator jg = factory.createJsonGenerator(sWriter);
	    jg.writeStartObject();
	    jg.writeStringField("id", entryNode.path("_id").asText());
	    jg.writeFieldName("values");
	    mapper.writeTree(jg, entryNode.path("_source"));
	    jg.writeEndObject();
	    sWriter.append('}');
	    String body = sWriter.toString();
	    in.setBody(body);
	}

}