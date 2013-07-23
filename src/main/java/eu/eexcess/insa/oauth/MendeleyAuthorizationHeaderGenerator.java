package eu.eexcess.insa.oauth;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.http.HttpOperationFailedException;

public class MendeleyAuthorizationHeaderGenerator implements Processor {

	public void process(Exchange exchange) throws HttpOperationFailedException {
		Message in = exchange.getIn();
		StringBuffer authorizationHeader = new StringBuffer();
		
		authorizationHeader.append("oauth_consumer_key=\"");
		authorizationHeader.append(exchange.getProperty("oauth_consumer_key", String.class));
		authorizationHeader.append("\",");
		
		authorizationHeader.append("oauth_signature_method=\"");
		authorizationHeader.append(exchange.getProperty("oauth_signature_method", String.class));
		authorizationHeader.append("\",");
		
		authorizationHeader.append("oauth_signature=\"");
		authorizationHeader.append(exchange.getProperty("oauth_signature", String.class));
		authorizationHeader.append("\",");
		
		authorizationHeader.append("oauth_timestamp=\"");
		authorizationHeader.append(exchange.getProperty("oauth_timestamp", String.class));
		authorizationHeader.append("\",");
		
		authorizationHeader.append("oauth_nonce=\"");
		authorizationHeader.append(exchange.getProperty("oauth_nonce", String.class));
		authorizationHeader.append("\",");
		
		authorizationHeader.append("oauth_version=\"");
		authorizationHeader.append(exchange.getProperty("oauth_version", String.class));
		authorizationHeader.append("\"");
		
		in.setHeader("Authorization",authorizationHeader.toString());


	}

}
