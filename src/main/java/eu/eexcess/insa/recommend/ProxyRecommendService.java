package eu.eexcess.insa.recommend;

import org.apache.camel.builder.RouteBuilder;

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
import eu.eexcess.insa.proxy.connectors.MendeleyQueryMapper;

public class ProxyRecommendService extends RouteBuilder {
	final PrepareRecommendationTermsPonderation prepPonderation = new PrepareRecommendationTermsPonderation();
	final EnrichedRecommendationQueryAggregator recommendationQueryAggregator = new EnrichedRecommendationQueryAggregator();
	final ExtractUserEnvironment extractUserEnv = new ExtractUserEnvironment();
	final UserProfileEnricherAggregator userContextAggregator = new UserProfileEnricherAggregator();
	final PrepareRecommendationTracesRequest prepTraces = new PrepareRecommendationTracesRequest();
	final MendeleyQueryMapper prepMendeleyQuery = new MendeleyQueryMapper();
	final MendeleyDocumentQueryMapper prepDocumentSearch = new MendeleyDocumentQueryMapper();
	final EconBizResultFormater econBizResultFormater = new EconBizResultFormater();


	/*
	public APIRecommendation() {
	
	}

	public APIRecommendation(CamelContext context) {
		super(context);
		
	}
	*/
	
	String apiBaseURI = // "jetty:http://localhost:12564";
			"servlet://";
	
	
	@Override
	public void configure() throws Exception {
		final ApplyPrivacySettingsJS applyPrivacySettings = new ApplyPrivacySettingsJS();

		/*
		 * Route to get an array of weighted search terms from an user context
		 * 
		 * INPUT :
		 * 	a trace, JSON format
		 * //TODO : example of a trace
		 * 
		 * OUTPUT :
		 * 
		 * {"content":
		 * 	[
		 * 		{"term":"<term>","score":<score>},
		 * 		{"term":"<term>","score":<score>}
		 *  ],
		 *  "ponderatedTopics":
		 *  [
		 *  	{"term":"<topic>","value":<value>},
		 *  	{"term":"<topic>","value":<value>}
		 *  ]
		 * }
		 * 	
		 * 
		 */
		from(apiBaseURI + "api/v0/recommend/rewrite").to("direct:query.enrich");
		
		
		/*
		 *  Route to get recommendations from an user context
		 *  
		 *  INPUT : 
		 *  	a trace, JSON format
		 *  //TODO : example trace
		 *  
		 *  OUTPUT :
		 *  	a html list (<ul><li> [...] </li></ul>
		 *  
		 */
		from(apiBaseURI + "api/v0/recommend/fetch").to("direct:recommendation.route");	

		
		
		/*
		 * Integrated recommendation service
		 */
		from(apiBaseURI + "api/v1/recommend").to("direct:recommendation.v1.route");		

	}

}
