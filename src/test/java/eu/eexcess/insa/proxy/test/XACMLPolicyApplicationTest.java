package eu.eexcess.insa.proxy.test;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import eu.eexcess.insa.proxy.ProxyRoutes;
import eu.eexcess.insa.proxy.policy.XACMLEnforcmentProcessor;

public class XACMLPolicyApplicationTest extends CamelTestSupport {
	//http://tools.ietf.org/html/rfc5849
	
	static XACMLEnforcmentProcessor xacmlPEP = new XACMLEnforcmentProcessor();
	
    @Produce(uri = "direct:test.policy.apply")
    protected ProducerTemplate template;
    
	private Processor prepMessageProcessor = new Processor() {
		@Override
		public void process(Exchange arg0) throws Exception {
		}
	};

    @Test
    public void test() throws Exception {
    	Exchange resp = template.send(prepMessageProcessor);
    	Message in = resp.getIn();
    	
    	String expectedString = in.getHeader("expected",String.class).replaceAll("\\s+","");    	
		String resultRaw = in.getBody(String.class);
		System.out.println(resultRaw);
		String result = resultRaw.replaceAll("\\s+","");
		System.out.println(result);
	    
		assertEquals(expectedString, result);
    }
     
    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
        	@Override
        	public void configure() throws Exception {
        		from("direct:test.policy.apply")
        			.to("string-template:templates/policy/test.policyA.json.st")
        			.setHeader("XACMLPolicy",body())
        			
        			.to("string-template:templates/expected/expected.profile001.policyA.json.st")
        			.setHeader("expected",body())

        			.to("string-template:templates/profiles/test.profile001.json.st")
        			
        			.process(xacmlPEP);
        		;
        		
        	}
        };
    }
}