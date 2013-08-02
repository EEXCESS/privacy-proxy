package eu.eexcess.insa.proxy.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
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
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.junit.Test;
import org.springframework.retry.ExhaustedRetryException;

import eu.eexcess.insa.oauth.OAuthSigningProcessor;
import eu.eexcess.insa.proxy.APIService;

public class ApplyPrivacySettingsTest extends CamelTestSupport {
	//http://tools.ietf.org/html/rfc5849
	
    @Produce(uri = "string-template:templates/profile.tm")
    protected ProducerTemplate template;
	private Processor prepMessageProcessor = new Processor() {
		public void process(Exchange exchange) throws Exception {
			Message in = exchange.getIn();
			// the input message will contain a raw elasticsearch 
			//response about a user's profile, stored into the exchange property : user_context-profile
			final InputStream testUserProfile= ClassLoader.getSystemResourceAsStream("data/mendeleyQueryTest_InputBody.json");
			exchange.setProperty("user_context-profile", testUserProfile);

		}
	};

    @Test
    public void test() throws Exception {
    	
    	
    	Exchange resp = template.send(prepMessageProcessor);
		String filtered= resp.getProperty("user_context-profile",String.class);
		
		
		
	    
		assertEquals(expectedString, result);
	    
    }
     
    @Override
    protected RouteBuilder createRouteBuilder() {
        return new APIService();
    }
}