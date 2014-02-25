package eu.eexcess.insa.recommend;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpOperationFailedException;

import eu.eexcess.insa.camel.JsonXMLDataFormat;
import eu.eexcess.insa.proxy.JSONList2JSON;
import eu.eexcess.insa.proxy.actions.ApplyPrivacySettingsJS;
import eu.eexcess.insa.proxy.actions.EnrichedRecommendationQueryAggregator;
import eu.eexcess.insa.proxy.actions.ExtractUserEnvironment;
import eu.eexcess.insa.proxy.actions.PrepareRecommendationTermsPonderation;
import eu.eexcess.insa.proxy.actions.PrepareRecommendationTracesRequest;
import eu.eexcess.insa.proxy.actions.UserProfileEnricherAggregator;
import eu.eexcess.insa.proxy.connectors.CloseJsonObject;
import eu.eexcess.insa.proxy.connectors.EconBizQueryMapper;
import eu.eexcess.insa.proxy.connectors.EconBizResultFormater;
import eu.eexcess.insa.proxy.connectors.MendeleyDocumentQueryMapper;
import eu.eexcess.insa.proxy.connectors.MendeleyQueriesAggregator;
import eu.eexcess.insa.proxy.connectors.MendeleyQueryMapper;
import eu.eexcess.insa.proxy.connectors.RecomendationResultAggregator;

public class ProxyRecommendRoutes extends RouteBuilder {
	final PrepareRecommendationTermsPonderation prepPonderation = new PrepareRecommendationTermsPonderation();
	final EnrichedRecommendationQueryAggregator recommendationQueryAggregator = new EnrichedRecommendationQueryAggregator();
	final ExtractUserEnvironment extractUserEnv = new ExtractUserEnvironment();
	final UserProfileEnricherAggregator userContextAggregator = new UserProfileEnricherAggregator();
	final PrepareRecommendationTracesRequest prepTraces = new PrepareRecommendationTracesRequest();
	final EconBizQueryMapper prepEconBizQuery = new EconBizQueryMapper ();
	final MendeleyQueryMapper prepMendeleyQuery = new MendeleyQueryMapper();
	final MendeleyDocumentQueryMapper prepDocumentSearch = new MendeleyDocumentQueryMapper();
	final EconBizResultFormater econBizResultFormater = new EconBizResultFormater();
	final CloseJsonObject closeJson = new CloseJsonObject();
	
	@Override
	public void configure() throws Exception {
		final ApplyPrivacySettingsJS applyPrivacySettings = new ApplyPrivacySettingsJS();
		
		
		/*=========================================================================
		 *  Recommendation routes
		 *=========================================================================*/

		from("direct:query.enrich")
		//.to("log:just_trace sent?showAll=true")
			.to("direct:context.safe.load")
			.process(prepPonderation)
			.process(recommendationQueryAggregator)
		;
		
		/*
		 *  Route to get recommendations 
		 */

		from("direct:recommendation.route")
			.removeHeaders("CamelHttp*")
			.removeHeader("Host")	
			//.to("log:recommendation route start?showAll=true")
			.to("direct:context.safe.load")
			.to("direct:recommend")
			.setHeader("Content-Type").constant("text/html")
			//.setHeader("recommendation_query", property("recommendation_query"));
			//.setHeader("recommendation_query",simple("${property[recommendation_query]}"))
			.setHeader("recommendation_query",property("recommendation_query"))
		;
		

		/*
		 *  Loads a user context and ensures it is privacy safe by applying the user privacy settings
		 *   User context is composed of:
		 *     - a user profile (demographic information)
		 *     - a list of user traces relative to a current time
		 *     
		 *  INPUT HEADERS:
		 *  
		 *  INPUT BODY: A JSON of a trace
		 *  
		 *  INPUT PROPERTIES:
		 *    
		 *  OUTPUT HEADERS:
		 * 
		 *  OUTPUT HEADERS:
		 * 
		 *  
		 */
		from("direct:context.safe.load")
			.process(extractUserEnv)  //this sets the user_id and plugin_uuid exchange properties
			// we need to get the user context
			 .enrich("direct:get.user.data", userContextAggregator)
			
			// we get the last traces
			.to("direct:get.recommendation.traces")
			 // filter the user profile following the privacy settings
		    .process(applyPrivacySettings)
		;
		
		from("direct:recommend")
			// user's context is used to prepare a query
			.process(prepPonderation)
			.setHeader("origin").simple("exchangeId")
			.multicast().aggregationStrategy(new RecomendationResultAggregator())
				.parallelProcessing()
				.timeout(10000)
				.to("direct:recommend.econbiz","direct:recommend.mendeley")
				
			.end()
			.process(new JSONList2JSON())
			//.to("log:aggregation complete")
			.unmarshal().string("UTF-8")
		    .unmarshal(new JsonXMLDataFormat())
		    //.wireTap("file:///tmp/econbiz/?fileName=example.xml")
		    .to("xslt:eu/eexcess/insa/xslt/results2html.xsl")
		    
		    
		    //.log("${property.recommendation_query}")
		    //.setHeader("recommendation_query",simple("${property.recommendation_query}"))
		    // .wireTap("file:///tmp/econbiz/?fileName=example.html")
		;
		
		/* This route gets the traces needed to prepare a recommendation
		 * 
		 */
		from("direct:get.recommendation.traces")
			.setHeader("ElasticType").constant("trace")
			.setHeader("ElasticIndex").constant("privacy")
			.process(prepTraces)
			.choice()
				.when().simple("${property.needMoreTraces} == 'yes'")
					.to("direct:elastic.userSearch")
				.otherwise()
					.to("log:no need for a traces query")
				
			.end()
			
			// filter the traces following the privacy settings
			//.log("${in.body}")
			//.convertBodyTo(String.class)
			//.to("log:recommendations.traces?showAll=true") 
			.setProperty("user_context-traces",simple("${in.body}", String.class))
		;
		
		    
		 /* route to get recommendation content from EconBiz
		  *    
		  */
		from("direct:recommend.econbiz")
			.process(prepEconBizQuery)
		    .choice()
				//.when().simple("${in.header.CamelHttpQuery} != 'q='")
		    	.when().simple("${property.emptyQuery} == 'no'")
					.to("http4://api.econbiz.de/v1/search")
				.otherwise()
					.to("log:mendeley recommendation : no query")
					.to("string-template:templates/empty-results.tm")
			.end()
			.process(econBizResultFormater)
		; 
		
		/* Route to get recommendation content from Mendeley
		 * 
		 */
		from("direct:recommend.mendeley")
			.onException(HttpOperationFailedException.class)
				
				//.handled(true) .log("exception handled")   /// a changer  (detruit tout l'exchange ?)
				.to("log:Mendeley recommendation error")
				.to("string-template:templates/empty-results.tm")
				.continued(false)
				//.to("string-template:templates/empty-results.tm")
				//.to("log:Mendeley.recommendation.httpexception?showAll=true")
			.end()
		    
	    	.process(prepMendeleyQuery)
	    	.choice()
	    		.when(simple("${property.no_terms} == true"))
	    			.to("string-template:templates/empty-results.tm")
	    		.otherwise()
	    			.throttle(30).timePeriodMillis(1000)
	    				.recipientList().header("QueryEndpoint")
	    				.process(prepDocumentSearch)
	    				.recipientList(header("QueryEndPoint").tokenize(",")).aggregationStrategy(new MendeleyQueriesAggregator())
	    			.end()
	    			.process(closeJson)
	    	.end()

		;


	}

}
