package eu.eexcess.insa.oauth.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.retry.ExhaustedRetryException;

import eu.eexcess.insa.oauth.OAuthSigningProcessor;
import eu.eexcess.insa.proxy.APIService;

public class OAuthSignTest extends CamelTestSupport {
	//http://tools.ietf.org/html/rfc5849
	
    @Produce(uri = "log:test")
    protected ProducerTemplate template;
    private Processor oauthSign = new OAuthSigningProcessor();
	private Processor prepMessageProcessor = new Processor() {
		public void process(Exchange exchange) throws Exception {
			Message in = exchange.getIn();
			
			in.setHeader(Exchange.HTTP_URI, "http://example.com/request");
			in.setHeader(Exchange.HTTP_METHOD, "POST");   // Méthode GET plutôt ?
			in.setHeader(Exchange.HTTP_QUERY, "b5=%3D%253D&a3=a&c%40=&a2=r%20b&c2&a3=2+q");
			in.setHeader(Exchange.HTTP_BASE_URI, "http://example.com");
			in.setHeader(Exchange.HTTP_PATH, "/request");
	
			exchange.setProperty("oauth_consumer_key", "9djdj82h48djs9d2");
			exchange.setProperty("oauth_signature_method", "HMAC-SHA1");
			exchange.setProperty("oauth_token", "kkk9d7dh3k39sjv7");
			exchange.setProperty("oauth_timestamp", "137131201");
			exchange.setProperty("oauth_nonce", "7d8f3e4a");
		}
	};
	private Processor prepMessageSigningProcessor = new Processor() {
		public void process(Exchange exchange) throws Exception {
			Message in = exchange.getIn();
			
			in.setHeader(Exchange.HTTP_URI, "http://photos.example.net/initiate");
			in.setHeader(Exchange.HTTP_METHOD, "POST");   // Méthode GET plutôt ?
			in.setHeader(Exchange.HTTP_QUERY, "");
			in.setHeader(Exchange.HTTP_BASE_URI, "http://photos.example.com");
			in.setHeader(Exchange.HTTP_PATH, "/initiate");
			exchange.setProperty("oauth_consumer_key", "dpf43f3p2l4k3l03");
			exchange.setProperty("oauth_consumer_secret", "kd94hf93k423kf44");
			exchange.setProperty("oauth_signature_method", "HMAC-SHA1");
			exchange.setProperty("oauth_timestamp", "137131200");
			exchange.setProperty("oauth_callback","http://printer.example.com/ready");
			exchange.setProperty("oauth_nonce", "wIjqoS");
			exchange.setProperty("oauth_version", "1.0");
		}
	};

    @Test
    public void testEncoding() throws Exception {
    	String expectedString = "POST&http%3A%2F%2Fexample.com%2Frequest&a2%3Dr%2520b%26a3%3D2%2520q%26a3%3Da%26b5%3D%253D%25253D%26c%2540%3D%26c2%3D%26oauth_consumer_key%3D9djdj82h48djs9d2%26oauth_nonce%3D7d8f3e4a%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D137131201%26oauth_token%3Dkkk9d7dh3k39sjv7";
    	System.out.println("is this real life ?");
    	Exchange resp = template.send("direct:oauth.encode", prepMessageProcessor);
		String result = resp.getIn().getBody(String.class);
	    assertEquals(expectedString, result);
    }
    
    @Test
    public void testSignature() throws Exception {
    	String expectedSign = "RyOXu4kZmg2gDfv+L4IB/H5VRu4=";
    	Exchange resp = template.send("direct:oauth.sign", prepMessageSigningProcessor);
        
        String resultSign = resp.getProperty("oauth_signature",String.class);
        assertEquals(expectedSign, resultSign);
    }
 
    @Override
    protected RouteBuilder createRouteBuilder() {
        return new APIService() {
        	public void configure() throws Exception {
        		super.configure();

        		from("direct:oauth.encode").log("youhou").bean(oauthSign,"calcBaseString");
        		from("direct:oauth.sign").process(oauthSign);
        	}
        };
    }
}