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
				/*from("jetty:http://localhost:8888/v0/eexcess/recommend")
					.removeHeaders("CamelHttp*")
					.process(prepReq)
//					.to("http4://www.google.com/search")
					.process(prepRes)
				;*/
				
				/*from("jetty:http://localhost:8888/api/v0/eexcess/trace")
					.removeHeaders("CamelHttp*")
					//.process(prepReq)
					.to("seda:elastic.index")
					//.process(prepRes)
				;*/
				
				from("jetty:http://localhost:8888/api/v0/eexcess/trace")
					.removeHeaders("CamelHttp*")
					
					.to("seda:elastic.trace.index")
				;
			}
		});
    	try {
			main.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
