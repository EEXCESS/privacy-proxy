package eu.eexcess.insa.proxy.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

import eu.eexcess.insa.proxy.APIService;

public class MendeleyQueryTest extends CamelTestSupport {
	 
    @Produce(uri = "log:test")
    protected ProducerTemplate template;
 
    @Test
    public void testSendMatchingMessage() throws Exception {
    	String expected = "";
		final InputStream testProfile= ClassLoader.getSystemResourceAsStream("data/userProfile.json");
        
		Exchange resp = template.send("direct:recommend.mendeley", new Processor() {
			public void process(Exchange exchange) throws Exception {
				Message in = exchange.getIn();
				in.setHeader("toto", "tata");
				in.setBody(testBody);
			}
		});
        
        String result = resp.getIn(String.class);
        assertEquals(expected, result);
    }
 
    @Override
    protected RouteBuilder createRouteBuilder() {
        return new APIService();
    }
}