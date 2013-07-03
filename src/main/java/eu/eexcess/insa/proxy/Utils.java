package eu.eexcess.insa.proxy;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

public class Utils {
	public static JsonFactory jsonFactory = new JsonFactory();
	
	public static void writeWeightedQuery(StringWriter request, HashMap<String,Double> query) throws IOException {
		JsonGenerator jg = jsonFactory.createJsonGenerator(request);
		jg = jsonFactory.createJsonGenerator(request);
		jg.writeStartObject();
			for(Entry<String, Double> entry: query.entrySet()) {
				jg.writeArrayFieldStart("query");
					jg.writeStringField("term", entry.getKey());
					jg.writeNumberField("score", entry.getValue());
				jg.writeEndArray();
			}
		jg.writeEndObject();
		jg.close();
	}
}
