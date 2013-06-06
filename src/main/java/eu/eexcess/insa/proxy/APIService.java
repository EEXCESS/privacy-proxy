	package eu.eexcess.insa.proxy;

import org.apache.camel.builder.RouteBuilder;

import eu.eexcess.insa.proxy.actions.PrepareRequest;
import eu.eexcess.insa.proxy.actions.PrepareResponse;

/**
 * Hello world!
 *
 */
public class APIService 
{
    public static void main( String[] args )
    {
    	final PrepareRequest prepReq = new PrepareRequest();
    	final PrepareResponse prepRes = new PrepareResponse();
    	
    	final org.apache.camel.spring.Main main = new org.apache.camel.spring.Main();
    	main.addRouteBuilder(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				from("jetty:http://localhost:8888/v0/eexcess/recommend")
					.process(prepReq)
					.to("http://www.google.com/");
//					.process(prepRes);
			}
		});
    	try {
			main.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
