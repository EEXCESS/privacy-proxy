package eu.eexcess.insa.proxy.connectors;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class MendeleyGetProfileInfo implements Processor { 

	


 
	public void process(Exchange exchange) throws Exception {
		
		String oauth_token ="" ;
		String oauth_token_secret= "";
		if(exchange.getProperty("oauth_token")!=null){
			oauth_token=exchange.getProperty("oauth_token",String.class);
		}
		if(exchange.getProperty("oauth_token_secret")!=null){
			oauth_token_secret=exchange.getProperty("oauth_token_secret",String.class);
		}
		if ( exchange.getProperty("user_id") == null ){
			String user_id = UUID.randomUUID().toString();
			exchange.setProperty("user_id", user_id);
		}
		Message in = exchange.getIn();
		
		
		
		if( ! oauth_token.equals("") ){
			System.out.println(oauth_token);
		}
		if ( ! oauth_token_secret.equals("")){
			System.out.println(oauth_token_secret);
		}
		//System.out.println(in.getBody(String.class ));
	    InputStream is = in.getBody(InputStream.class);
		
		JsonFactory factory = new JsonFactory();
	    JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	    
	   StringWriter stringWriter = new StringWriter();
	   JsonGenerator jg = factory.createJsonGenerator(stringWriter);
	    //JsonNode nodeBuffer;
	    
	   

	   
	    jg.writeStartObject();
	    jg.writeFieldName("user_")
	    jg.writeArrayFieldStart("data");
	    jg
	    	
	    	
	    	
	    	
	    
	    jg.writeEndArray();
	    jg.writeEndObject();
	    jg.close();
	    in.setBody(stringWriter.toString());
	   
	   
	}

}
