	package eu.eexcess.insa.proxy;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpOperationFailedException;

import com.semsaas.jsonxml.JsonXMLReader;

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
import eu.eexcess.insa.proxy.actions.GetUserId;
import eu.eexcess.insa.proxy.actions.GetUserIdFromBody;
import eu.eexcess.insa.proxy.actions.GetUserProfiles;
import eu.eexcess.insa.proxy.actions.PrepareLastTenTracesQuery;
import eu.eexcess.insa.proxy.actions.PrepareRecommendationRequest;
import eu.eexcess.insa.proxy.actions.PrepareRecommendationTermsPonderation;
import eu.eexcess.insa.proxy.actions.PrepareRequest;
import eu.eexcess.insa.proxy.actions.PrepareResponse;
import eu.eexcess.insa.proxy.actions.PrepareSearch;
import eu.eexcess.insa.proxy.actions.PrepareUserSearch;
import eu.eexcess.insa.proxy.actions.PrepareUserLogin;
import eu.eexcess.insa.proxy.actions.PrepareRespLogin;
import eu.eexcess.insa.proxy.actions.ProfileVerifier;
import eu.eexcess.insa.proxy.actions.PrepareUserProfile;
import eu.eexcess.insa.proxy.actions.UpdateEEXCESSProfile;
import eu.eexcess.insa.proxy.connectors.CloseJsonObject;
import eu.eexcess.insa.proxy.connectors.EconBizQueryMapper;
import eu.eexcess.insa.proxy.connectors.EconBizResultFormater;
import eu.eexcess.insa.proxy.connectors.MendeleyDocumentQueryMapper;
import eu.eexcess.insa.proxy.connectors.MendeleyUpdateProfileInfo;
import eu.eexcess.insa.proxy.connectors.MendeleyQueriesAggregator;
import eu.eexcess.insa.proxy.connectors.MendeleyQueryMapper;
import eu.eexcess.insa.proxy.connectors.RecomendationResultAggregator;


public class APIService extends RouteBuilder  {

	final PrepareRequest prepReq = new PrepareRequest();
	final PrepareResponse prepRes = new PrepareResponse();
	final PrepareSearch prepSearch = new PrepareSearch();
	final PrepareUserSearch prepUserSearch = new PrepareUserSearch();
	final PrepareUserLogin prepUserLogin = new PrepareUserLogin();
	final PrepareRespLogin prepRespUser = new PrepareRespLogin();
	final PrepareUserProfile prepUserProfile = new PrepareUserProfile();
	final PrepareRecommendationRequest prepRecommendRequ = new PrepareRecommendationRequest();
	final PrepareRecommendationTermsPonderation prepPonderation = new PrepareRecommendationTermsPonderation();
	final EconBizQueryMapper prepEconBizQuery = new EconBizQueryMapper ();
	final PrepareLastTenTracesQuery prepLastTen = new PrepareLastTenTracesQuery();
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
	
		public void configure() throws Exception {
			
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
				.process(prepUserSearch)
				.to("direct:elastic.userSearch")
			;
				
			
			/*
			 *  Route to get recommendations 
			 */
			from("jetty:http://localhost:12564/api/v0/recommend")	
				.removeHeaders("CamelHttp*")
				.removeHeader("Host")	
				.to("direct:recommend")
				.setHeader("Content-Type").constant("text/html")
				.setHeader("recommendation_query", property("recommendation_query"));
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
			

			/* Route to check if given username and email currently exit
			 * 
			 */
			from("jetty:http://localhost:11564/user/verify")
				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("users")
				.process(prepUserSearch)
				.to("direct:elastic.userSearch")
				.process(prepRes)
			;
			
			/*Route to log a user in
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
			
			/*route to get recommendation content
			 * 
			 */
			from("direct:recommend")
				.process(prepLastTen)  //this sets the user_id exchange property
				//.log("${in.body}")
				.setHeader("ElasticType").constant("trace")
				.setHeader("ElasticIndex").constant("privacy")
				.to("direct:elastic.userSearch")
				//.log("${in.body}")
				.setProperty("user_context-traces",simple("${in.body}", String.class))
				.to("direct:get.user.data")
				.setProperty("user_context-profile",simple("${in.body}", String.class))
				
				.process(prepPonderation)
				//.log("${in.body}")
				.setHeader("origin").simple("exchangeId")
				.multicast().aggregationStrategy(new RecomendationResultAggregator())
					.parallelProcessing().timeout(2000L)
					.to("direct:recommend.econbiz","direct:recommend.mendeley")
					
				.end()
				.unmarshal().string("UTF-8")
			    .unmarshal(new JsonXMLDataFormat())
			    //.wireTap("file:///tmp/econbiz/?fileName=example.xml")
			    .to("xslt:eu/eexcess/insa/xslt/results2html.xsl")
			    // .wireTap("file:///tmp/econbiz/?fileName=example.html")
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
			; 
			
			/* Route to get recommendation content from Mendeley
			 * 
			 */
			from("direct:recommend.mendeley")
			    .process(prepMendeleyQuery)
			    .recipientList().header("QueryEndpoint")
			    .process(prepDocumentSearch)
			    .recipientList(header("QueryEndPoint").tokenize(",")).aggregationStrategy(new MendeleyQueriesAggregator())
			    .process(closeJson)
			    
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

				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("users")
				.setHeader("traceId").property("user_id")
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
