package eu.eexcess.insa.proxy;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

public class Utils {
	public static JsonFactory jsonFactory = new JsonFactory();
	
	public static void writeWeightedQuery(StringWriter request, HashMap<String,Integer> query) throws IOException {
		JsonGenerator jg = jsonFactory.createJsonGenerator(request);
		jg = jsonFactory.createJsonGenerator(request);
		jg.writeStartObject();
		jg.writeArrayFieldStart("query");
			for(Entry<String, Integer> entry: query.entrySet()) {
				//jg.writeArrayFieldStart("query");
				//jg.writeFieldName("query");
					jg.writeStartObject();
						jg.writeStringField("term", entry.getKey());
						jg.writeNumberField("score", entry.getValue());
					jg.writeEndObject();
				//jg.writeEndArray();
			}
			jg.writeEndArray();
		jg.writeEndObject();
		jg.close();
	}
}
