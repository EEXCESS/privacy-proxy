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

public class PrepareSearch implements Processor {

	public void process(Exchange exchange) throws Exception {

		Message in = exchange.getIn();
		in.removeHeader(Exchange.HTTP_BASE_URI);
		in.removeHeader(Exchange.HTTP_PATH);
		in.removeHeader(Exchange.HTTP_URI);
		in.removeHeader("CamelHttpUrl");
		in.removeHeader("CamelServletContextPath");
		
		in.removeHeader("CamelHttpServletRequest");
		
		
		
		InputStream userData = in.getBody(InputStream.class);
		
		in.setHeader("ElasticType", "trace");
		in.setHeader("ElasticIndex", "privacy");
		in.setHeader(Exchange.HTTP_METHOD, "POST");
		
		
		
		JsonFactory factory = new JsonFactory();
		
		JsonParser jp = factory.createJsonParser(userData);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode dataNode = mapper.readValue(jp, JsonNode.class);
		
		StringWriter sWriter = new StringWriter();
		JsonGenerator jg = factory.createJsonGenerator(sWriter);
		
		jg.writeStartObject();
		jg.writeFieldName("query");
		jg.writeStartObject();
		jg.writeFieldName("bool");
		jg.writeStartObject();
		jg.writeFieldName("must");
		jg.writeStartArray();
		if ( !dataNode.path("pluginId").isMissingNode()){
			jg.writeStartObject();
			jg.writeFieldName("term");
			jg.writeStartObject();
			jg.writeStringField("plugin.uuid", dataNode.path("pluginId").asText());
			jg.writeEndObject();
			jg.writeEndObject();
		}
		if ( !dataNode.path("userId").isMissingNode()){
			jg.writeStartObject();
			jg.writeFieldName("term");
			jg.writeStartObject();
			jg.writeStringField("user.user_id", dataNode.path("userId").asText());
			jg.writeEndObject();
			jg.writeEndObject();
		}
		
		jg.writeStartObject();
		jg.writeFieldName("term");
		jg.writeStartObject();
		jg.writeStringField("user.environnement", dataNode.path("environnement").asText());
		jg.writeEndObject();
		jg.writeEndObject();
		
		
		
		jg.writeEndArray();
		jg.writeEndObject();
		jg.writeEndObject();
		jg.writeEndObject();
		
		jg.close();
		
		String traceQuery = sWriter.toString();
		
		
		
		in.setBody(traceQuery);
		

	}

}
