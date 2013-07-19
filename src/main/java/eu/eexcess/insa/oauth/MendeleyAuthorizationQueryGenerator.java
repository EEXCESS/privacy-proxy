package eu.eexcess.insa.oauth;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.http.HttpOperationFailedException;

import eu.eexcess.insa.commons.Strings;

public class MendeleyAuthorizationQueryGenerator implements Processor {

	public void process(Exchange exchange) throws HttpOperationFailedException {
		Message in = exchange.getIn();
		StringBuffer authorizationQuery = new StringBuffer();

		boolean isFirst = true;
		for(String param: OauthConstants.parameters) {
			String value = exchange.getProperty(param, String.class);
			if(value != null) {
				if(!isFirst) {
					authorizationQuery.append("&");
				} else {
					isFirst = false;
				}
				authorizationQuery.append(param);
				authorizationQuery.append("=");
				authorizationQuery.append(Strings.encodeURL(value));
			}
		}

		in.setHeader(Exchange.HTTP_QUERY,authorizationQuery.toString());


	}

}
