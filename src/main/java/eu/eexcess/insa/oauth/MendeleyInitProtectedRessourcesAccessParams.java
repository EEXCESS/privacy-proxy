package eu.eexcess.insa.oauth;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class MendeleyInitProtectedRessourcesAccessParams implements Processor {

	final private String consumerKey = "e13d6dfff14e3174b84d9bca29cd6082051d51fd6";
	final private String consumerSecret = "6faa629a8339f4bf61d9f41be70c536f";
	
	public void process(Exchange exchange) throws Exception {
		
		
		Message in = exchange.getIn();
		in.removeHeader("oauth_consumer_key");
		in.removeHeader("oauth_signature_method");
		in.removeHeader("oauth_timestamp");
		in.removeHeader("oauth_nonce");
		in.removeHeader("oauth_signature");
		in.removeHeader("oauth_version");
		in.removeHeader("oauth_token");
		in.removeHeader("oauth_verifier");
		
		exchange.setProperty("oauth_consumer_key", consumerKey);
		exchange.setProperty("oauth_consumer_secret", consumerSecret);
		exchange.setProperty("oauth_signature_method", "HMAC-SHA1");
		exchange.setProperty("oauth_timestamp", String.valueOf(new Date().getTime()));
		
		SecureRandom random = new SecureRandom();
	    byte bytes[] = new byte[10];
	    random.nextBytes(bytes);
	    ByteBuffer bb = ByteBuffer.wrap(bytes);
	    String nonce = String.valueOf(Math.abs(bb.getLong()));
		exchange.setProperty("oauth_nonce", nonce);
		exchange.setProperty("oauth_version", "1.0");
		
		
		
		
		String response = in.getBody(String.class);
		Message out = exchange.getOut();
		List<NameValuePair> valPair = URLEncodedUtils.parse(response,Charset.forName("UTF-8"));
		for(NameValuePair vp: valPair) {
			if( vp.getName().equals("oauth_token")){
				exchange.setProperty("oauth_token", vp.getValue());	
			}
			else if( vp.getName().equals("oauth_token_secret")){
				exchange.setProperty("oauth_token_secret", vp.getValue());	
			}
		
		}

		exchange.setProperty("oauth_verifier",null);
		exchange.setProperty("oauth_signature",null);
		/*in.removeHeader(Exchange.HTTP_BASE_URI);
		in.removeHeader(Exchange.HTTP_PATH);
		in.removeHeader(Exchange.HTTP_METHOD);*/

		out.setHeader(Exchange.HTTP_BASE_URI, "http://api.mendeley.com");
		out.setHeader(Exchange.HTTP_PATH,"/oapi/profiles/info/me/");
		out.setHeader(Exchange.HTTP_METHOD, "GET"); 
		
	}
	

}

