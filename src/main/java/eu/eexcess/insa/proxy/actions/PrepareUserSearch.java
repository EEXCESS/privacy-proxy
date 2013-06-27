package eu.eexcess.insa.proxy.actions;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;


public class PrepareUserSearch implements Processor {

	public void process(Exchange exchange) throws Exception {

		Message in = exchange.getIn();
		String body = in.getBody(String.class);
		
		
		String query = "{\"query\":{\"bool\":{\"should\":["+body+"]}},\"from\":0,\"size\":50,\"sort\":[],\"facets\":{}}";
		in.setBody(query);
		
		
		/*InputStream input = in.getBody(InputStream.class);
		JsonParser jp = jsonFactory.createJsonParser(input);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readValue(jp, JsonNode.class);		

		if(node.get("username")!=null){
			input.close();
			in.setBody()
			
			
		}*/
		
		
		//in.setHeader("Content-Type","text/html");
		//in.setBody("<ul><li>toto</li><li>titi</li></ul>");
	}

}
