package eu.eexcess.insa.oauth;

import java.nio.charset.Charset;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import eu.eexcess.insa.commons.Strings;

public class MendeleyProcessResponse implements Processor {

	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();

		String queryComponent = in.getBody(String.class);
		List<NameValuePair> valPair = URLEncodedUtils.parse(queryComponent,Charset.forName("UTF-8"));

		for(NameValuePair vp: valPair) {
			String name = vp.getName();
			String val = vp.getValue();
			
			if (name.equals("oauth_token")){
				in.setHeader("oauth_token", val);
			};
			if (name.equals("oauth_token_secret")){
				in.setHeader("oauth_token_secret", val);
			};
		}
		

	}

}
