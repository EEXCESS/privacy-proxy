package eu.eexcess.insa.proxy.actions;

import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class PrepareRecommendationTracesRequest implements Processor {

	
	//TODO
	//prendre en compte les privacy settings ( en exchange property ) pour savoir le nombre de traces à récupérer
	//  0 --> only current page
	// 1 -> traces on this computer only
	//2 --> all computers
	// regarder la trace envoyée du front end
	// récupérer environnement -- > le stocker en propriété
	// faire une meilleure requete elasticsearch ( avec jsongenerator etc )
	// pas oublier de faire un nouveau processor pour filtrer les topics
	public void process(Exchange exchange) throws Exception {
		String user_id = exchange.getProperty("user_id", String.class);
		String plugin_id = exchange.getProperty("plugin_id", String.class);
		HashMap< String, Integer> privacySettings = exchange.getProperty("privacy_settings", HashMap.class);
		if ( privacySettings.containsKey("traces")){
			
		}
		
		
		String query ="{\"query\": {\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"term\": {\"user.user_id\": \""+user_id+"\"}},{\"term\": {\"plugin.uuid\": \""+plugin_id+"\"}}]}}]}},\"from\": 0,\"size\": 10,\"sort\": [{\"temporal.begin\": \"desc\"}]}";
		
		Message in = exchange.getIn();
		
		
		in.setBody(query);

	}

}
