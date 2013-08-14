package eu.eexcess.insa.proxy.actions;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;


public class EnrichedRecommendationQueryAggregator implements Processor {

	public void process(Exchange exchange) throws Exception {
		HashMap<String, Integer> ponderatedTopics = (HashMap<String, Integer>)exchange.getProperty("ponderated_topics", HashMap.class);
		Message in = exchange.getIn();
		InputStream contentQuery = in.getBody(InputStream.class);
		
		JsonFactory factory = new JsonFactory();
		JsonParser parser = factory.createJsonParser(contentQuery);
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode queryNode = mapper.readValue(parser, JsonNode.class);
		
		StringWriter sWriter = new StringWriter();
		JsonGenerator generator = factory.createJsonGenerator(sWriter);
		
		generator.writeStartObject();
		generator.writeFieldName("content");
		
		if ( !queryNode.path("query").isMissingNode()){
			mapper.writeTree(generator, queryNode.path("query"));
		}
		generator.writeFieldName("ponderatedTopics");
		generator.writeStartArray();
		
		Iterator<Entry<String, Integer>> it = ponderatedTopics.entrySet().iterator();
		
		while ( it.hasNext()){
			Entry<String, Integer> pair = it.next();
			generator.writeStartObject();
			generator.writeStringField("term", pair.getKey());
			generator.writeNumberField("value", pair.getValue());
			generator.writeEndObject();
			it.remove();
		}
		
		generator.writeEndArray();
		
		generator.writeEndObject();
		generator.close();
		
		in.setBody(sWriter.toString());
	}

}
