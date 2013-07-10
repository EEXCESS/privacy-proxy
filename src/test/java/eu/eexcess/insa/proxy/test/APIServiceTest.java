package eu.eexcess.insa.proxy.test;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import eu.eexcess.insa.proxy.APIService;

public class APIServiceTest extends CamelTestSupport {
	 
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;
 
    @Produce(uri = "direct:start")
    protected ProducerTemplate template;
 
    @Test
    public void testSendMatchingMessage() throws Exception {
        String expectedBody = "<matched/>";
 
        resultEndpoint.expectedBodiesReceived(expectedBody);
 
        template.sendBodyAndHeader(expectedBody, "foo", "bar");
 
        resultEndpoint.assertIsSatisfied();
    }
 
    @Test
    public void testSendNotMatchingMessage() throws Exception {
        resultEndpoint.expectedMessageCount(0);
 
        template.sendBodyAndHeader("<notMatched/>", "foo", "notMatchedHeaderValue");
 
        resultEndpoint.assertIsSatisfied();
    }
 
    @Override
    protected RouteBuilder createRouteBuilder() {
        return new APIService();
    }
}