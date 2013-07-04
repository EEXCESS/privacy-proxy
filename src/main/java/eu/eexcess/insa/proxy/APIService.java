	package eu.eexcess.insa.proxy;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import com.semsaas.jsonxml.JsonXMLReader;

import eu.eexcess.insa.camel.JsonXMLDataFormat;
import eu.eexcess.insa.proxy.actions.PrepareLastTenTracesQuery;
import eu.eexcess.insa.proxy.actions.PrepareRecommendationRequest;
import eu.eexcess.insa.proxy.actions.PrepareRecommendationTermsPonderation;
import eu.eexcess.insa.proxy.actions.PrepareRequest;
import eu.eexcess.insa.proxy.actions.PrepareResponse;
import eu.eexcess.insa.proxy.actions.PrepareSearch;
import eu.eexcess.insa.proxy.actions.PrepareUserSearch;
import eu.eexcess.insa.proxy.actions.PrepareUserLogin;
import eu.eexcess.insa.proxy.actions.PrepareRespLogin;
import eu.eexcess.insa.proxy.connectors.EconBizQueryMapper;

/**
 * Hello world!
 *
 */
public class APIService extends RouteBuilder  {

	final PrepareRequest prepReq = new PrepareRequest();
	final PrepareResponse prepRes = new PrepareResponse();
	final PrepareSearch prepSearch = new PrepareSearch();
	final PrepareUserSearch prepUserSearch = new PrepareUserSearch();
	final PrepareUserLogin prepUserLogin = new PrepareUserLogin();
	final PrepareRespLogin prepRespUser = new PrepareRespLogin();
	final PrepareRecommendationRequest prepRecommendRequ = new PrepareRecommendationRequest();
	final PrepareRecommendationTermsPonderation prepPonderation = new PrepareRecommendationTermsPonderation();
	final EconBizQueryMapper prepEconBizQuery = new EconBizQueryMapper ();
	final PrepareLastTenTracesQuery prepLastTen = new PrepareLastTenTracesQuery();
	
		public void configure() throws Exception {
			/*from("jetty:http://localhost:8888/v0/eexcess/recommend")
				.removeHeaders("CamelHttp*")
				.process(prepReq)
//				.to("http4://www.google.com/search")
				.process(prepRes)
			;*/
			
			from("jetty:http://localhost:12564/api/v0/privacy/trace")
				.setHeader("ElasticType").constant("trace")
				.setHeader("ElasticIndex").constant("privacy")
				.to("seda:elastic.trace.index")
			;	
				
			from("jetty:http://localhost:12564/api/v0/recommend")	
				.removeHeaders("CamelHttp*")
				.removeHeader("Host")	
				.to("direct:recommend.econbiz")
				.setHeader("Content-Type").constant("text/html")
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
			
			
			
			from("direct:recommend.econbiz")
				.process(prepLastTen)
				.setHeader("ElasticType").constant("trace")
				.setHeader("ElasticIndex").constant("privacy")
				.to("direct:elastic.userSearch")
				.process(prepPonderation)
				.process(prepEconBizQuery)
				.removeHeader("ElasticType")
				.removeHeader("ElasticIndex")
				
				.to("log:query?showHeaders=true")
				.choice()
					.when().simple("${in.header.CamelHttpQuery} == 'q='")
						.to("http4://api.econbiz.de/v1/search")
					.otherwise()
						.to("string-template:templates/empty-results.tm")
			    //.wireTap("direct:essai")
			    .unmarshal(new JsonXMLDataFormat())
			    .wireTap("file:///tmp/econbiz/?fileName=example.xml")
			    .to("xslt:eu/eexcess/insa/xslt/econbiz2html.xsl")
			    .wireTap("file:///tmp/econbiz/?fileName=example.html")
			;
			
			
			
			
			
			
			from("direct:essai")
			.unmarshal().string("UTF-8")
			.to("file:///tmp/econbiz/?fileName=debug.txt");
			
			
			from("direct:essaiRequete")
			.unmarshal().string("UTF-8")
			.to("file:///tmp/econbiz/?fileName=debugRequete.txt");
			
		}

	public static void main( String[] args ) {
    	final org.apache.camel.spring.Main main = new org.apache.camel.spring.Main();
    	main.addRouteBuilder(new APIService());
    	try {
			main.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
