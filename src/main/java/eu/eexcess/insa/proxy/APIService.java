	package eu.eexcess.insa.proxy;

import org.apache.camel.builder.RouteBuilder;

import eu.eexcess.insa.proxy.actions.PrepareRequest;
import eu.eexcess.insa.proxy.actions.PrepareResponse;
import eu.eexcess.insa.proxy.actions.PrepareSearch;
import eu.eexcess.insa.proxy.actions.PrepareUserSearch;
import eu.eexcess.insa.proxy.actions.PrepareUserLogin;
import eu.eexcess.insa.proxy.actions.PrepareRespLogin;

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
    	final PrepareUserSearch prepUserSearch = new PrepareUserSearch();
    	final PrepareUserLogin prepUserLogin = new PrepareUserLogin();
    	final PrepareRespLogin prepRespUser = new PrepareRespLogin();
    
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
					.setHeader("ElasticType").constant("trace")
					.setHeader("ElasticIndex").constant("privacy")
					.process(prepSearch)
					.to("direct:elastic.search")
				;
				
				from("jetty:http://localhost:12564/api/v0/users/data")
					.setHeader("ElasticType").constant("data")
					.setHeader("ElasticIndex").constant("users")
					.to("seda:elastic.trace.index")
				;

				from("jetty:http://localhost:11564/user/verify")
					.setHeader("ElasticType").constant("data")
					.setHeader("ElasticIndex").constant("users")
					.process(prepUserSearch)
					.to("direct:elastic.userSearch")
					.process(prepRes)
				;
				
				from("jetty:http://localhost:11564/user/login")
					.setHeader("ElasticType").constant("data")
					.setHeader("ElasticIndex").constant("users")
					.process(prepUserLogin)
					.to("direct:elastic.userSearch")
					.process(prepRespUser)
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
