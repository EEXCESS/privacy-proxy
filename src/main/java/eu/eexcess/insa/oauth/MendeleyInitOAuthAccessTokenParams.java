package eu.eexcess.insa.oauth;


import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class MendeleyInitOAuthAccessTokenParams implements Processor {

	final private String consumerKey = "e13d6dfff14e3174b84d9bca29cd6082051d51fd6";
	final private String consumerSecret = "6faa629a8339f4bf61d9f41be70c536f";
	
	public void process(Exchange exchange) throws Exception {
		
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
		
		
		
		Message in = exchange.getIn();
		if( in.getHeader("user_id") != null){
			String user_id = in.getHeader("user_id",String.class);
			in.removeHeader("user_id");
			exchange.setProperty( "user_id", user_id ) ;
		}
		
		
		exchange.setProperty("oauth_token", in.getHeader("oauth_token",String.class));
		exchange.setProperty("oauth_token_secret", in.getHeader("oauth_token_secret",String.class));
		exchange.setProperty("oauth_verifier", in.getHeader("oauth_verifier",String.class));

		in.setHeader(Exchange.HTTP_BASE_URI, "http://api.mendeley.com");
		in.setHeader(Exchange.HTTP_PATH,"/oauth/access_token/");
		in.setHeader(Exchange.HTTP_METHOD, "GET"); 
		
		
		
	}
	

}
