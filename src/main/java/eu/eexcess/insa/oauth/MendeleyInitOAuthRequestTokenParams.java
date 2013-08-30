package eu.eexcess.insa.oauth;


import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class MendeleyInitOAuthRequestTokenParams implements Processor {

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
		exchange.setProperty("oauth_callback", in.getHeader("Origin",String.class)+"/oauth/index.html" );
		//exchange.setProperty("oauth_callback", "http://localhost.com/oauth/token" );
		//System.out.println(in.getHeader("Origin",String.class));
		in.setHeader(Exchange.HTTP_BASE_URI, "http://api.mendeley.com");
		in.setHeader(Exchange.HTTP_PATH,"/oauth/request_token/");
		in.setHeader(Exchange.HTTP_METHOD, "GET"); 
		
		
		
	}
	

}
