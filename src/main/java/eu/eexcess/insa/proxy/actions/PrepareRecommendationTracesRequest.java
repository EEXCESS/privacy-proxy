package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class PrepareRecommendationTracesRequest implements Processor {

	
	//TODO
	//prendre en compte les privacy settings ( en exchange property ) pour savoir le nombre de traces à récupérer
	//  0 --> only current page
	// 1 -> traces on this computer only
	//2 --> all computers
	// regarder la trace envoyée du front end
	// récupérer environnement -- > le stocker en propriété
	// faire une meilleure requete elasticsearch ( avec jsongenerator etc )
	// pas oublier de faire un nouveau processor pour filtrer les topics
	public void process(Exchange exchange) throws Exception {
		String user_id = exchange.getProperty("user_id", String.class);
		String plugin_id = exchange.getProperty("plugin_uuid", String.class);
		HashMap< String, Integer> privacySettings = exchange.getProperty("privacy_settings", HashMap.class);
		int privacyTrace = 2; // privacy level is set to the minimum by default
		if ( privacySettings.containsKey("traces")){
			privacyTrace = privacySettings.get("traces");
		}
		if ( privacyTrace != 0){
		
			JsonFactory factory = new JsonFactory();
			StringWriter sWriter = new StringWriter();
			JsonGenerator jg = factory.createJsonGenerator(sWriter);
			InputStream inputTrace = exchange.getIn().getBody(InputStream.class);
			JsonParser jp = factory.createJsonParser(inputTrace);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
			
			
			String upperDate;
			int nbTraces;
		
			String environnement;
			
			
			jg.writeStartObject();
				jg.writeFieldName("query");
				jg.writeStartObject();
					jg.writeFieldName("bool");
					jg.writeStartObject();
							jg.writeFieldName("must");
							jg.writeStartArray();
								jg.writeStartObject();
								// 
									jg.writeFieldName("term");
									jg.writeStartObject();
										jg.writeStringField("trace.user.environnement", );
									jg.writeEndObject();
									
									jg.writeFieldName("term");
									jg.writeStartObject();
										jg.writeStringField("trace.plugin.uuid", );
									jg.writeEndObject();
									
									jg.writeFieldName("term");
									jg.writeStartObject();
										jg.writeStringField("trace.user.user_id", );
									jg.writeEndObject();
									
									jg.writeFieldName("range");
									jg.writeStartObject();
										jg.writeFieldName("temporal.begin");
										jg.writeStartObject();
											jg.writeStringField("to", upperDate);
											jg.writeBooleanField("include_upper", true);
										jg.writeEndObject();
									jg.writeEndObject();
									
								jg.writeEndObject();
							jg.writeEndArray();
					
					jg.writeEndObject();
					
				jg.writeEndObject();
				
				jg.writeNumberField("size", nbTraces);
				
			jg.writeEndObject();
			
			
			String query ="{\"query\": {\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"term\": {\"user.user_id\": \""+user_id+"\"}},{\"term\": {\"plugin.uuid\": \""+plugin_id+"\"}}]}}]}},\"from\": 0,\"size\": 10,\"sort\": [{\"temporal.begin\": \"desc\"}]}";
			
			Message in = exchange.getIn();
			
			
			in.setBody(query);
		}

	}

}
