	package eu.eexcess.insa.proxy;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

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
import eu.eexcess.insa.proxy.connectors.MendeleyQueryMapper;
import eu.eexcess.insa.recommend.ProxyRecommendRoutes;
import eu.eexcess.insa.recommend.ProxyRecommendService;

public class ProxyService extends RouteBuilder  {
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
	
	
	String apiBaseURI = // "jetty:http://localhost:12564/";
						"servlet://";
		public void configure() throws Exception {
			final ApplyPrivacySettingsJS applyPrivacySettings = new ApplyPrivacySettingsJS();
			/* Route used to register traces
			 * 
			 */
			from(apiBaseURI + "api/v0/privacy/trace")
				.to("direct:trace.index")
			;

			
			/* Route used to retrieve user's profile data
			 * 
			 */
			from(apiBaseURI+ "api/v0/users/profile")
				.to("direct:get.user.profile")
			;

			
			
			/* Route to retrieve the user's traces
			 * 
			 */
			from(apiBaseURI+"user/traces")
				.to("direct:retrieve.user.traces")
			;
			
			
			
			/* Route to update and retrieve user information
			 * 
			 * 
			 */
			from(apiBaseURI + "api/v0/users/data")
				.to("direct:update.user.profile")
			;
				
			
			/*
			 *  Route to directly save the privacy settings into the user index ( without merging the data with other profiles first )
			 */
			from(apiBaseURI + "api/v0/users/privacy_settings")
				.to("direct:save.privacy.settings")
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
			from(apiBaseURI + "user/verify")
				.to("direct:verify.user")
			;
				
			
			/*
			 * Route to log a user in
			 * 
			 */

			from(apiBaseURI+"user/login")
				.to("direct:user.login")
			;
				
			
			
			
			/* ================================================================
			 * 						MENDELEY OAUTH ROUTES
			 * ================================================================
			 */
			
			
			
			
			/* Route to initialize Mendeley OAuth authentifiaction
			 *  ( get request token and other informations
			 */
			from(apiBaseURI+"oauth/mendeley/init")	
				.to("direct:oauth.mendeley.init")
			;
				
			
			/* Route to continue Mendeley Oauth authentification
			 *  (get access token and other informations)
			 */
			from(apiBaseURI+ "oauth/mendeley/connect")	
				.to("direct:oauth.mendeley.connect")
			;
				
			
			//=======================================================
			
			
		}

	public static void main( String[] args ) {
    	final org.apache.camel.spring.Main main = new org.apache.camel.spring.Main();
    	main.addRouteBuilder(new ProxyService());
    	main.addRouteBuilder(new ProxyRecommendService());
     	main.addRouteBuilder(new ProxyRecommendRoutes());

    	//insert other routebuilders here
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
