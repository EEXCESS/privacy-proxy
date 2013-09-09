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

	
	
	
	public void process(Exchange exchange) throws Exception {
		String user_id = exchange.getProperty("user_id", String.class);
		String plugin_id = exchange.getProperty("plugin_uuid", String.class);
		String environnement = exchange.getProperty("environnement", String.class);
		HashMap< String, Integer> privacySettings = exchange.getProperty("privacy_settings", HashMap.class);
		int privacyTrace = 2; // privacy level is set to the minimum by default
		if ( privacySettings.containsKey("traces")){
			privacyTrace = privacySettings.get("traces");

			
		}
		if ( privacyTrace != 0){
			// the number of traces we want to consider 
			int nbTraces = 10;
			boolean onlyUseTracesFromSameEnvironnement = true ;
			
			// the message body contains the traces that has been sent from the front end ( the current trace)
			JsonFactory factory = new JsonFactory();
			StringWriter sWriter = new StringWriter();
			JsonGenerator jg = factory.createJsonGenerator(sWriter);
			
			//InputStream inputTrace = exchange.getIn().getBody(InputStream.class);
			//JsonParser jp = factory.createJsonParser(inputTrace);
			//ObjectMapper mapper = new ObjectMapper();
			//JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
			
			JsonNode rootNode = exchange.getIn().getBody(JsonNode.class);
			
			// we use this trace to retrieve the relevant datas : 
				//- date
				// environnement ( work / home )
				// ( the user_id and plugin_id already have been retrieved)
			
			String upperDate="";
			if ( rootNode != null){
				/*
				 * if ( !rootNode.path("user").isMissingNode()){
					if ( !rootNode.path("user").path("environnement").isMissingNode()){
						
						environnement = rootNode.path("user").path("environnement").asText();
						
					}
				}
				*/
				if ( !rootNode.path("temporal").isMissingNode()){
					if ( !rootNode.path("temporal").path("begin").isMissingNode()){
						upperDate = rootNode.path("temporal").path("begin").asText();
					}
				}
			}
			
			//System.out.println("user_id = "+user_id);

			jg.writeStartObject();
				jg.writeFieldName("sort");
				jg.writeStartArray();
					jg.writeStartObject();
						jg.writeFieldName("temporal.begin");
						jg.writeStartObject();
							jg.writeStringField("order", "desc");
						jg.writeEndObject();
					jg.writeEndObject();
				jg.writeEndArray();
				jg.writeFieldName("query");
				jg.writeStartObject();
					jg.writeFieldName("bool");
					jg.writeStartObject();
							jg.writeFieldName("must");
							jg.writeStartArray();
								
								// 
								if ( onlyUseTracesFromSameEnvironnement){ // is always true
									jg.writeStartObject();
										jg.writeFieldName("term");
										jg.writeStartObject();
											jg.writeStringField("trace.user.environnement", environnement );
										jg.writeEndObject();
									jg.writeEndObject();
								}
								if ( privacyTrace == 1 || user_id== null || user_id.equals("")){ // only the traces from this plugin are used
									jg.writeStartObject();
										jg.writeFieldName("term");
										jg.writeStartObject();
											jg.writeStringField("trace.plugin.uuid", plugin_id );
										jg.writeEndObject();
									jg.writeEndObject();
								}
								if ( !user_id.equals("")&& user_id!=null){
									jg.writeStartObject();
										jg.writeFieldName("term");
										jg.writeStartObject();
											jg.writeStringField("trace.user.user_id", user_id );
										jg.writeEndObject();
									jg.writeEndObject();
								}
									
									jg.writeStartObject();
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
			
			jg.close();
			
			String query = sWriter.toString();
			//String query ="{\"query\": {\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"term\": {\"user.user_id\": \""+user_id+"\"}},{\"term\": {\"plugin.uuid\": \""+plugin_id+"\"}}]}}]}},\"from\": 0,\"size\": 10,\"sort\": [{\"temporal.begin\": \"desc\"}]}";
			
			Message in = exchange.getIn();
			
			
			
			in.setBody(query);
			exchange.setProperty("needMoreTraces", "yes");
		}
		else{ // privacy.traces = 0 : only the current trace is used
			// we need to bypass the query and to keep the original trace ( since no other trace is needed )
			JsonFactory factory = new JsonFactory();
			StringWriter sWriter = new StringWriter();
			JsonGenerator jg = factory.createJsonGenerator(sWriter);
			JsonNode rootNode = exchange.getIn().getBody(JsonNode.class);
			ObjectMapper mapper = new ObjectMapper();
			jg.writeStartObject();
				jg.writeFieldName("hits");
				jg.writeStartObject();
					jg.writeNumberField("total", 1);
					jg.writeFieldName("hits");
					jg.writeStartArray();
						jg.writeStartObject();
						jg.writeFieldName("_source");
							mapper.writeTree(jg, rootNode);
						jg.writeEndObject();
					jg.writeEndArray();
				jg.writeEndObject();

				
			jg.writeEndObject();
			jg.close();
			
			String query = sWriter.toString();

			Message in = exchange.getIn();
			in.setBody(query);
			
			exchange.setProperty("needMoreTraces", "no");
		}

	}

}
