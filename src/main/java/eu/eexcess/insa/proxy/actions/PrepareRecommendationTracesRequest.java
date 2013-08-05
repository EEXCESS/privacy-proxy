package eu.eexcess.insa.proxy.actions;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class PrepareRecommendationTracesRequest implements Processor {

	public void process(Exchange exchange) throws Exception {
		String user_id = exchange.getProperty("user_id", String.class);
		String plugin_id = exchange.getProperty("plugin_id", String.class);
		String query ="{\"query\": {\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"term\": {\"user.user_id\": \""+user_id+"\"}},{\"term\": {\"plugin.uuid\": \""+plugin_id+"\"}}]}}]},\"from\": 0,\"size\": 10,\"sort\": [{\"temporal.begin\": \"desc\"}]}";
		
		Message in = exchange.getIn();
		
		System.out.println("query (last ten traces\n"+query);
		in.setBody(query);

	}

}
