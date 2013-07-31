package eu.eexcess.insa.proxy.actions;

import java.io.StringWriter;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

public class GetUserProfiles implements Processor {

	public void process(Exchange exchange) throws Exception {
		String user_id = exchange.getProperty("user_id",String.class);
		Message in = exchange.getIn(); 
		StringWriter sw = new StringWriter();
		JsonFactory factory = new JsonFactory();
		JsonGenerator jg = factory.createJsonGenerator(sw);
		
		jg.writeStartObject();
		
			jg.writeFieldName("query");
			jg.writeStartObject();
		
				jg.writeFieldName("bool");
				jg.writeStartObject();
		
					jg.writeFieldName("must");
					jg.writeStartArray();
		
						jg.writeStartObject();
		
							jg.writeFieldName("prefix");
							jg.writeStartObject();
		
								jg.writeStringField("_id",user_id);
		
							jg.writeEndObject();
		
						jg.writeEndObject();
		
					jg.writeEndArray();
		
				jg.writeEndObject();
		
			jg.writeEndObject();
		
		jg.writeEndObject();
		jg.close();
		String q = sw.toString();
		System.out.println(q);
		in.setBody(q);
		//in.setHeader(Exchange.HTTP_PATH, value)
		
	}


}
