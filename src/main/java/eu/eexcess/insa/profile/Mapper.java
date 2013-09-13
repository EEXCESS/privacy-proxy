package eu.eexcess.insa.profile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;


import org.apache.camel.Message;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Mapper {
	public static void fillHeader(Message in, String headerName, String value) {
		if(
				in.getHeader(headerName,String.class) == null ||
				"".equals(in.getHeader(headerName,String.class))
		) {
			if(value != null && !"".equals(value)) {
				in.setHeader(headerName, value);
			}
		}
	
	}
	
	public static void fillHeader(Message in, String headerName, String[] values) throws JsonParseException, JsonMappingException, IOException {
		HashSet<String> valueSet = new HashSet<String>();
		String[] existingValues = in.getHeader( headerName , String[].class);
		if(existingValues != null) {
			valueSet.addAll(Arrays.asList(existingValues));
		}
		
		if(values != null) {
			valueSet.addAll(Arrays.asList(values));
			JsonFactory factory = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper();
			Iterator<String> it = valueSet.iterator();
			HashSet<JsonNode> topicsSet = new HashSet<JsonNode>();
			while(it.hasNext()){
				JsonParser jp = factory.createJsonParser(it.next());
				JsonNode topic = mapper.readValue(jp, JsonNode.class);
				boolean unique = true;
				Iterator<JsonNode> it2 = topicsSet.iterator();
				while(it2.hasNext()){
					JsonNode t = it2.next();
					if(t.path("label").asText().equals(topic.path("label").asText())){
						if (t.path("source").asText().equals("mendeley") ){
							it2.remove();
						}
						else{
							unique = false;
						}	
					}
				}
				if ( unique ){
					topicsSet.add(topic);
				}
				
			 }
			valueSet.clear();
			Iterator<JsonNode> it3 = topicsSet.iterator(); 
			while ( it3.hasNext()){
				//valueSet.add(mapper.readValue(it3.next().traverse(), String.class));
				valueSet.add(it3.next().toString());
			}
		}
		
		if(valueSet.size() > 0) {
			String[] newValues = new String[valueSet.size()];
			newValues = valueSet.toArray(newValues);
			in.setHeader(headerName, newValues );	
		}	
	}
}
