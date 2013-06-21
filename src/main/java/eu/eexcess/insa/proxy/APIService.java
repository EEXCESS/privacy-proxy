	package eu.eexcess.insa.proxy;

import org.apache.camel.builder.RouteBuilder;

import eu.eexcess.insa.proxy.actions.PrepareRequest;
import eu.eexcess.insa.proxy.actions.PrepareResponse;
import eu.eexcess.insa.proxy.actions.PrepareSearch;

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
    	final PrepareSearch prepSearch = new PrepareSearch();
    
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
				
				from("jetty:http://localhost:12564/api/v0/privacy/trace")
					.setHeader("ElasticType").constant("trace")
					.setHeader("ElasticIndex").constant("privacy")
					.to("seda:elastic.trace.index")
				;
				
				from("jetty:http://localhost:12564/api/v0/recommend/document")
					.setHeader("ElasticType").constant("document")
					.setHeader("ElasticIndex").constant("recommend")
					.to("seda:elastic.trace.index")
				;
				
				from("jetty:http://localhost:11564/user/traces")
					.process(prepSearch)
					.to("direct:elastic.search")
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
