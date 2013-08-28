	package eu.eexcess.insa.proxy;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpOperationFailedException;

import eu.eexcess.insa.camel.JsonXMLDataFormat;
import eu.eexcess.insa.oauth.MendeleyAuthorizationHeaderGenerator;
import eu.eexcess.insa.oauth.MendeleyAuthorizationQueryGenerator;
import eu.eexcess.insa.oauth.MendeleyInitOAuthAccessTokenParams;
import eu.eexcess.insa.oauth.MendeleyInitOAuthRequestTokenParams;
import eu.eexcess.insa.oauth.MendeleyInitProtectedRessourcesAccessParams;
import eu.eexcess.insa.oauth.MendeleyProcessResponse;
import eu.eexcess.insa.oauth.OAuthSigningProcessor;
import eu.eexcess.insa.profile.EexcessProfileMapper;
import eu.eexcess.insa.profile.MendeleyProfileMapper;
import eu.eexcess.insa.profile.ProfileSplitter;
import eu.eexcess.insa.proxy.actions.ApplyPrivacySettingsJS;
import eu.eexcess.insa.proxy.actions.EnrichedRecommendationQueryAggregator;
import eu.eexcess.insa.proxy.actions.GetUserId;
import eu.eexcess.insa.proxy.actions.GetUserIdFromBody;
import eu.eexcess.insa.proxy.actions.GetUserProfiles;
import eu.eexcess.insa.proxy.actions.ExtractUserEnvironment;
import eu.eexcess.insa.proxy.actions.PrepareRecommendationRequest;
import eu.eexcess.insa.proxy.actions.PrepareRecommendationTermsPonderation;
import eu.eexcess.insa.proxy.actions.PrepareRecommendationTracesRequest;
import eu.eexcess.insa.proxy.actions.PrepareRequest;
import eu.eexcess.insa.proxy.actions.ElasticExtractHitCount;
import eu.eexcess.insa.proxy.actions.PrepareSearch;
import eu.eexcess.insa.proxy.actions.JSONBody2Properties;
import eu.eexcess.insa.proxy.actions.PrepareUserLogin;
import eu.eexcess.insa.proxy.actions.PrepareRespLogin;
import eu.eexcess.insa.proxy.actions.ProfileVerifier;
import eu.eexcess.insa.proxy.actions.PrepareUserProfile;
import eu.eexcess.insa.proxy.actions.UpdateEEXCESSProfile;
import eu.eexcess.insa.proxy.actions.UserProfileEnricherAggregator;
import eu.eexcess.insa.proxy.connectors.CloseJsonObject;
import eu.eexcess.insa.proxy.connectors.EconBizQueryMapper;
import eu.eexcess.insa.proxy.connectors.EconBizResultFormater;
import eu.eexcess.insa.proxy.connectors.MendeleyDocumentQueryMapper;
import eu.eexcess.insa.proxy.connectors.MendeleyUpdateProfileInfo;
import eu.eexcess.insa.proxy.connectors.MendeleyQueriesAggregator;
import eu.eexcess.insa.proxy.connectors.MendeleyQueryMapper;
import eu.eexcess.insa.proxy.connectors.RecomendationResultAggregator;

public class APIService extends RouteBuilder  {
	final ElasticExtractHitCount elasticExtractHitCount = new ElasticExtractHitCount();
	
	
	final PrepareRequest prepReq = new PrepareRequest();
	final PrepareSearch prepSearch = new PrepareSearch();
	final JSONBody2Properties jsonBody2Properties = new JSONBody2Properties();
	final PrepareUserLogin prepUserLogin = new PrepareUserLogin();
	final PrepareRespLogin prepRespUser = new PrepareRespLogin();
	final PrepareUserProfile prepUserProfile = new PrepareUserProfile();
	final PrepareRecommendationRequest prepRecommendRequ = new PrepareRecommendationRequest();
	final PrepareRecommendationTermsPonderation prepPonderation = new PrepareRecommendationTermsPonderation();
	final EconBizQueryMapper prepEconBizQuery = new EconBizQueryMapper ();
	final ExtractUserEnvironment extractUserEnv = new ExtractUserEnvironment();
	final MendeleyQueryMapper prepMendeleyQuery = new MendeleyQueryMapper();
	final MendeleyDocumentQueryMapper prepDocumentSearch = new MendeleyDocumentQueryMapper();
	final CloseJsonObject closeJson = new CloseJsonObject();
	final EconBizResultFormater econBizResultFormater = new EconBizResultFormater();
	final MendeleyInitOAuthRequestTokenParams mendeleyInitOAuthParams = new MendeleyInitOAuthRequestTokenParams();
	final OAuthSigningProcessor signingProcessor = new OAuthSigningProcessor ( ) ;
	final MendeleyAuthorizationHeaderGenerator oauthHeaderGenerator = new MendeleyAuthorizationHeaderGenerator();
	final MendeleyAuthorizationQueryGenerator oauthQueryGenerator = new MendeleyAuthorizationQueryGenerator();
	final MendeleyInitOAuthAccessTokenParams mendeleyInitAccessParams = new MendeleyInitOAuthAccessTokenParams();
	final MendeleyInitProtectedRessourcesAccessParams mendeleyInitAccessRessourcesParams = new MendeleyInitProtectedRessourcesAccessParams();
	final GetUserId getUserId = new GetUserId();
	final MendeleyUpdateProfileInfo mendeleyUpdateProfileInfo = new MendeleyUpdateProfileInfo();  
	final ProfileVerifier verifyProfile = new ProfileVerifier(); 
	final UpdateEEXCESSProfile updateEexcessProfile = new UpdateEEXCESSProfile();
	final GetUserProfiles getProfiles = new GetUserProfiles();
	final JsonXMLDataFormat jsonDataFormat =new JsonXMLDataFormat(); 
	final EexcessProfileMapper eexcessProfileMapper = new EexcessProfileMapper();
	final MendeleyProfileMapper mendeleyProfileMapper = new MendeleyProfileMapper(); 
	final ProfileSplitter profileSplitter = new ProfileSplitter();
	final GetUserIdFromBody getUserIdFromBdy = new GetUserIdFromBody();
	final PrepareRecommendationTracesRequest prepTraces = new PrepareRecommendationTracesRequest();
	final UserProfileEnricherAggregator userContextAggregator = new UserProfileEnricherAggregator();
	final EnrichedRecommendationQueryAggregator recommendationQueryAggregator = new EnrichedRecommendationQueryAggregator();
	//final ApplyPrivacySettingsJS applyPrivacySettings = new ApplyPrivacySettingsJS();
	
		public void configure() throws Exception {
			final ApplyPrivacySettingsJS applyPrivacySettings = new ApplyPrivacySettingsJS();
			/* Route used to register traces
			 * 
			 */
			from("jetty:http://localhost:12564/api/v0/privacy/trace")
				.setHeader("ElasticType").constant("trace")
				.setHeader("ElasticIndex").constant("privacy")
				.to("seda:elastic.trace.index")
			;	
			
			/* Route used to retrieve user's profile data
			 * 
			 */
			from("jetty:http://localhost:12564/api/v0/users/profile")
				.process(getUserIdFromBdy)
				.to("direct:get.user.data")
				.process(prepUserProfile)
			;
				
			
			from("direct:get.user.data")
				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("users")
				.to("string-template:templates/elastic/user.account.search.tm")
				.to("direct:elastic.userSearch")
			;
				
			
			/*
			 *  Route to get recommendations 
			 */
			from("jetty:http://localhost:12564/api/v0/recommend")	
				.removeHeaders("CamelHttp*")
				.removeHeader("Host")	
				//.to("log:recommendation route start?showAll=true")
				.to("direct:context.safe.load")
				.to("direct:recommend")
				.setHeader("Content-Type").constant("text/html")
				//.setHeader("recommendation_query", property("recommendation_query"));
				//.setHeader("recommendation_query",simple("${property[recommendation_query]}"))
				.setHeader("recommendation_query",property("recommendation_query"))
				
			    .to("log:headerestilla?showHeaders=true")
			;
			
			
			
			/* Route to retrieve the user's traces
			 * 
			 */
			from("jetty:http://localhost:11564/user/traces")
				.setHeader("ElasticType").constant("trace")
				.setHeader("ElasticIndex").constant("privacy")
				.process(prepSearch)
				.removeHeader("Host")
				.to("direct:elastic.search")
				.to("log:send traces?showHeaders=true")

			;
			
			
			/* Route to update and retrieve user information
			 * 
			 * 
			 */
			from("jetty:http://localhost:12564/api/v0/users/data")
				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("profiles")
				.process(updateEexcessProfile)
				.to("seda:elastic.trace.index")
				
				//.to("direct:merge.profiles")
				//.setHeader("ElasticType").constant("data")
				//.setHeader("ElasticIndex").constant("users")
				//.to("seda:elastic.trace.index")
				//.log("user registered")
				//.log("${in.body}")
				//.log("${out.body}")
				.process(getUserId)	
				.to("direct:profiles.merge")
			;
			
			/*
			 *  Route to directly save the privacy settings into the user index ( without merging the data with other profiles first )
			 */
			from("jetty:http://localhost:12564/api/v0/users/privacy_settings")
				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("users")
				.to("seda:elastic.trace.index")
				
				.process(getUserId)	
			;
			

			/*
			 * Route to check if given username or email currently exit
			 * INPUT BODY: JSON with the following structure
			 *   {
			 *      "user_email": <USER_EMAIL>,
			 *      "user_id": <USER_ID>
			 *   }
			 *   
			 * NOTE: Only one of the keys is necessary
			 * 
			 * OUTPUT BODY:
			 *   ElasticSearch results
			 */
			from("jetty:http://localhost:11564/user/verify")
				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("users")
				.process(jsonBody2Properties)
				.to("string-template:templates/elastic/user.account.search.tm")
				.to("direct:elastic.userSearch")
				.process(elasticExtractHitCount)
				.to("string-template:templates/results/hits.tm")
			;
			
			/*
			 * Route to log a user in
			 * 
			 */
			from("jetty:http://localhost:11564/user/login")
				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("users")
				.process(prepUserLogin)
				.to("direct:elastic.userSearch")
				.removeHeader(Exchange.HTTP_URI)
				.process(prepRespUser)
			;
			
			/*=========================================================================
			 *  Recommendation routes
			 *=========================================================================*/
			
			
			from("jetty:http://localhost:11564/api/v0/query/enrich")
			//.to("log:just_trace sent?showAll=true")
				.to("direct:context.safe.load")
				.process(prepPonderation)
				.process(recommendationQueryAggregator)
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
					.timeout(5000)
					.to("direct:recommend.econbiz","direct:recommend.mendeley")
					.log("multicast : end")
					
				.end()
				.process(new JSONList2JSON())
				.to("log:aggregation complete")
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
				.to("log:recommendations.traces?showAll=true") 
				.setProperty("user_context-traces",simple("${in.body}", String.class))
			;
			
			    
			 /* route to get recommendation content from EconBiz
			  *    
			  */
			from("direct:recommend.econbiz")
				.process(prepEconBizQuery)
			    .choice()
					.when().simple("${in.header.CamelHttpQuery} != 'q='")
						.to("http4://api.econbiz.de/v1/search")
					.otherwise()
						.to("string-template:templates/empty-results.tm")
				.end()
				.process(econBizResultFormater)
				.log("EconBiz recommendations retrieved")
			; 
			
			/* Route to get recommendation content from Mendeley
			 * 
			 */
			from("direct:recommend.mendeley")
				.onException(HttpOperationFailedException.class)
					
					//.handled(true) .log("exception handled")   /// a changer  (detruit tout l'exchange ?)
					.log("erreur mandeley")
					.to("string-template:templates/empty-results.tm")
					
					.continued(true)
					//.to("string-template:templates/empty-results.tm")
					//.to("log:Mendeley.recommendation.httpexception?showAll=true")
				.end()
			    
		    	.process(prepMendeleyQuery)
		    	.choice()
		    		.when(simple("${property.no_terms} == true"))
		    			.to("log:no terms")
		    			.to("string-template:templates/empty-results.tm")
		    		.otherwise()
		    			.to("log:terms")
		    			.recipientList().header("QueryEndpoint")
					    .process(prepDocumentSearch)
					    .recipientList(header("QueryEndPoint").tokenize(",")).aggregationStrategy(new MendeleyQueriesAggregator())
					    .process(closeJson)
		    	.end()
				
			    .to("log:ex1.1?showAll=true")

			    
			;
			
			/* Route to initialize Mendeley OAuth authentifiaction
			 *  ( get request token and other informations
			 */
			from("jetty:http://localhost:11564/oauth/mendeley/init")	
				.process(mendeleyInitOAuthParams)
				.process(signingProcessor)
				.process(oauthQueryGenerator)
				.removeHeaders("CamelHttpUri")
				.removeHeaders("CamelHttpPath")
				.setHeader("Host", simple("api.mendeley.com"))
				.to("http4://api.mendeley.com/oauth/request_token/")
				.process(new MendeleyProcessResponse())			
			;
			
			/* Route to continue Mendeley Oauth authentification
			 *  (get access token and other informations)
			 */
			from("jetty:http://localhost:11564/oauth/mendeley/connect")
				 
				.process(mendeleyInitAccessParams)
				.process(signingProcessor)
				.process(oauthQueryGenerator)
				.removeHeaders("CamelHttpUri")
				.removeHeaders("CamelHttpPath")
				.setHeader("Host", simple("api.mendeley.com"))
				.to("http4://api.mendeley.com/oauth/access_token/")
				.to("direct:oauth.access")
			;
					
			/* Route to get user's Mendely profile informations
			 * 
			 */
			from("direct:oauth.access")
				.process(mendeleyInitAccessRessourcesParams)
				.process(signingProcessor)
				.process(oauthQueryGenerator)
				.setHeader("Host", simple("api.mendeley.com"))
				.to("http4://api.mendeley.com/")
				.to("direct:elastic.save")
			;
			
			/* Route to save user's Mendeley profile informations to ElasticSearch
			 * 
			 */
			from("direct:elastic.save")
				
				.process(verifyProfile)
				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("profiles")
				.to("direct:elastic.userSearch")
				
				.process(mendeleyUpdateProfileInfo)
				.to("seda:elastic.trace.index")
				.to("direct:profiles.merge")
				
			;
			
			/* Route to merge the user's different profiles
			 * 
			 */
			from("direct:profiles.merge")
				.process(getProfiles)
				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("profiles")
				.to("log:ex1.1?showAll=true") 	
				.to("direct:elastic.userSearch")
				
				.unmarshal().string("UTF-8")
				.wireTap("file:///tmp/merge/?fileName=example.json")
				
				.process(profileSplitter)
				.process(eexcessProfileMapper)
				.process(mendeleyProfileMapper)
				.to("string-template:templates/profile.tm")
				.to("log:coucou2?showAll=true")

				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("users")
				.setHeader("traceId").property("user_id")
				.to("log:just_befire_indexing?showAll=true")
				.to("seda:elastic.trace.index")
			;
		}

	public static void main( String[] args ) {
    	final org.apache.camel.spring.Main main = new org.apache.camel.spring.Main();
    	main.addRouteBuilder(new APIService());
    	
    	Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {			
			public void run() {
				try {
					main.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}));
    	try {
			main.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
