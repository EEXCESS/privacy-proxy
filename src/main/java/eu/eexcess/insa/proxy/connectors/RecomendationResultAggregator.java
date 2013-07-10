package eu.eexcess.insa.proxy.connectors;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;


public class RecomendationResultAggregator implements AggregationStrategy {

	final int nbSources = 2; // number of messages we have to wait for until we have retrieved
							 //all the informations we asked for and can start ordering them
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if( oldExchange == null ){
			ArrayList<String> results = new ArrayList<String>();
			results.add(newExchange.getIn().getBody(String.class));
			newExchange.getIn().setBody(results);
			
		}
		else{
			ArrayList<String> results = (ArrayList<String>)(oldExchange.getIn().getBody(ArrayList.class)); 
			results.add(newExchange.getIn().getBody(String.class));
			if ( results.size() < nbSources){
				newExchange.getIn().setBody(results);
			}
			else{ // all messages have been retrieved
				JsonFactory factory = new JsonFactory();
				ObjectMapper mapper = new ObjectMapper();
				ArrayList<JsonNode> resultNodes = new ArrayList<JsonNode>();
				Iterator<String> it = results.iterator();
				while (it.hasNext()){
					JsonParser jp;
					try {
						jp = factory.createJsonParser(it.next());
						resultNodes.add(mapper.readValue(jp, JsonNode.class));
					} catch (JsonParseException e) {		
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				   
				}
				StringWriter sWriter = new StringWriter(); 
				JsonGenerator jg;
				try {
					jg = factory.createJsonGenerator(sWriter);
					jg.writeStartObject();
					jg.writeArrayFieldStart("Documents");
					Vector<Iterator<JsonNode>> documentIterators = new Vector<Iterator<JsonNode>>();
					Iterator<JsonNode> itNodes = resultNodes.iterator();
					while(itNodes.hasNext()){
						documentIterators.add(itNodes.next().path("documents").getElements());
					}
					boolean writeMore = true;
					boolean[] ct = new boolean[documentIterators.size()];
					for ( int i = 0 ; i < ct.length; i++ ){
						ct[i]= true;
					}
					while (writeMore){
						for ( int i = 0 ; i < documentIterators.size(); i++ ){
							if ( documentIterators.get(i).hasNext() ){
								//jg.writeTree(documentIterators.get(i).next());
								copyObject(jg, documentIterators.get(i).next());
							}
							else{
								ct[i]=false;
							}
						}
						writeMore = false;
						for ( int i = 0 ; i < ct.length ; i++ ){
							writeMore = writeMore || ct[i];
						}
						
					}

					jg.writeEndArray();
					jg.writeEndObject();
					jg.close();
					newExchange.getIn().setBody(sWriter.toString());
				
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
				
			}
		}
			
		return newExchange;
	}
	
	private void copyObject ( JsonGenerator jg , JsonNode object) throws JsonGenerationException, IOException{
		jg.writeStartObject();
		if ( !object.path("title").isMissingNode()){
			jg.writeFieldName("title");
			jg.writeString(object.path("title").asText());
		}
		else{
			jg.writeFieldName("title");
			jg.writeString("");
		}
		if ( !object.path("url").isMissingNode()){
			jg.writeFieldName("url");
			jg.writeString(object.path("url").asText());
		}
		else{
			jg.writeFieldName("url");
			jg.writeString("");
		
		}
		if ( !object.path("type").isMissingNode()){
			jg.writeFieldName("type");
			jg.writeString(object.path("type").asText());
		}
		else{
			jg.writeFieldName("type");
			jg.writeString("");
		}
		if ( !object.path("abstract").isMissingNode()){
			jg.writeFieldName("abstract");
			jg.writeString(object.path("abstract").asText());
		}
		else{
			jg.writeFieldName("abstract");
			jg.writeString("");
		}
		if ( !object.path("score").isMissingNode()){
			jg.writeFieldName("score");
			jg.writeString(object.path("score").asText());
		}
		else{
			//jg.writeFieldName("score");
			//jg.writeString("");
		}
		if ( !object.path("origin").isMissingNode()){
			jg.writeFieldName("origin");
			jg.writeString(object.path("origin").asText());
		}
		else{
			jg.writeFieldName("origin");
			jg.writeString("");
		}
		jg.writeEndObject();
	}

}
