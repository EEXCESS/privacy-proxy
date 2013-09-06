package eu.eexcess.insa.proxy;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class JSONList2JSON implements Processor {
	JsonFactory factory = new JsonFactory();
	ObjectMapper mapper = new ObjectMapper();

	String rootNodeName =  "Documents";
	public String getRootNodeName() {
		return rootNodeName;
	}
	public void setRootNodeName(String rootNodeName) {
		this.rootNodeName = rootNodeName;
	}


	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<JsonNode> mergedArray = exchange.getIn().getBody(List.class);
		StringWriter sWriter = new StringWriter(); 
		JsonGenerator jg;
		try {
			jg = factory.createJsonGenerator(sWriter);
			jg.writeStartObject();
			jg.writeArrayFieldStart(rootNodeName);
			if ( mergedArray != null ){
				for(JsonNode n: mergedArray) { //bug hier
					if(n != null) {
						mapper.writeTree(jg, n);
					}
				}
			}
			jg.writeEndArray();
			jg.writeEndObject();
			jg.close();
			exchange.getIn().setBody(sWriter.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
