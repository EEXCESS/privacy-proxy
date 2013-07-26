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

public class MendeleyUpdateProfileInfo implements Processor { 

	


 
	public void process(Exchange exchange) throws Exception {
		
		String oauth_token ="" ;
		String oauth_token_secret= "";
		if(exchange.getProperty("oauth_token")!=null){
			oauth_token=exchange.getProperty("oauth_token",String.class);
		}
		if(exchange.getProperty("oauth_token_secret")!=null){
			oauth_token_secret=exchange.getProperty("oauth_token_secret",String.class);
		}
		if ( exchange.getProperty("user_id") == null || exchange.getProperty("user_id").equals("undefined")){
			String user_id = UUID.randomUUID().toString();
			exchange.setProperty("user_id", user_id.replace("-", ""));
		}
		Message in = exchange.getIn();
		
		JsonFactory factory = new JsonFactory();
		InputStream elasticResponse = in.getBody(InputStream.class);
		JsonParser responseParser = factory.createJsonParser(elasticResponse);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode responseNode = mapper.readValue(responseParser, JsonNode.class);
		
		String profileId ="";
		
		if(responseNode.path("hits").path("total").asText().equals("1")){ // the profile already exists
			// in our index, we need to retrieve the id and then to update it
			System.out.println("the profile already exists");
			String user_id = responseNode.path("hits").path("hits").get(0).path("_id").asText().replace(exchange.getProperty ( "mendeley_id" , String.class ), "");
			exchange.setProperty("user_id", user_id);
			profileId = responseNode.path("hits").path("hits").get(0).path("_id").asText();
			
		}
		else{ // the mendeley profile doesn't exist in our index yet
			String user_id = UUID.randomUUID().toString();
			exchange.setProperty("user_id", user_id.replace("-", ""));
			profileId = user_id + exchange.getProperty ( "mendeley_id" , String.class );
			
		}
		
		
	/*	
		if( ! oauth_token.equals("") ){
			System.out.println(oauth_token);
		}
		if ( ! oauth_token_secret.equals("")){
			System.out.println(oauth_token_secret);
		}
		*/
		
	    InputStream is = exchange.getProperty("mendeleyProfile",InputStream.class);
		
		
	    JsonParser jp = factory.createJsonParser(is);
	    
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	    
	    StringWriter stringWriter = new StringWriter();
	    JsonGenerator jg = factory.createJsonGenerator(stringWriter);
   
	    jg.writeStartObject();
	    jg.writeStringField("source","mendeley");
	    
	    if( ! oauth_token.equals("") && ! oauth_token_secret.equals("") ){
		    jg.writeFieldName("login_datas");
		    jg.writeStartObject();
		    jg.writeStringField("oauth_token",oauth_token);
		    jg.writeStringField("oauth_token_secret",oauth_token_secret);
		    jg.writeEndObject();
	    }
	    jg.writeFieldName("profile_data");
	    mapper.writeTree(jg, rootNode);
	    
	    
	    
	    jg.writeEndObject();
	    jg.close();
	    
	    in.setBody(stringWriter.toString());
	    in.setHeader("traceId", profileId);
	    
	   
	   
	}

}
