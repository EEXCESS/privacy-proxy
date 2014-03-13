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

public class ProxyRoutes extends RouteBuilder  {
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
	
			
			
			from("direct:trace.index")
				//.streamCaching()
				//.log("Indexing trace ...")
				.setHeader("ElasticType").constant("trace")
				.setHeader("ElasticIndex").constant("privacy")
				//.to("log:indexing trace :?showAll=true&showStreams=true ")
				.to("seda:elastic.trace.index")
				//.to("log:indexing trace :?showAll=true&showStreams=true ")
			;	
			
			
			from("direct:get.user.profile")
				.process(getUserIdFromBdy)
				.to("direct:get.user.data")
				.process(prepUserProfile)
			;
				
			
			from("direct:get.user.data")
				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("users")
				.to("string-template:templates/elastic/user.account.search.tm")
				//.to("log:DEBUG : getUserData?showAll=true")
				.to("direct:elastic.userSearch")
			;

			from("direct:retrieve.user.traces")
				//.streamCaching()
				//.convertBodyTo(String.class)
				//.to("log:send traces : input?showAll=true")
				.setHeader("ElasticType").constant("trace")
				.setHeader("ElasticIndex").constant("privacy")
				.process(prepSearch)
				.removeHeader("Host")
				.to("direct:elastic.search")
				// for testing purposes
				.convertBodyTo(String.class)
				//.to("log:send traces?showHeaders=true")
				//.wireTap("file:test_data")

			;
			
		
			from("direct:update.user.profile")
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
			from("direct:save.privacy.settings")
				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("users")
				.to("seda:elastic.trace.index")
				
				.process(getUserId)	
			;
			
			from("direct:verify.user")
				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("users")
				.process(jsonBody2Properties)
				.to("string-template:templates/elastic/user.account.search.tm")
				.to("direct:elastic.userSearch")
				.process(elasticExtractHitCount)
				.to("string-template:templates/results/hits.tm")
			;
			
			

			from("direct:user.login")
				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("users")
				.process(prepUserLogin)
				.to("direct:elastic.userSearch")
				.removeHeader(Exchange.HTTP_URI)
				.process(prepRespUser)
			;
			
			
			
			
			/* ================================================================
			 * 						MENDELEY OAUTH ROUTES
			 * ================================================================
			 */

			from("direct:oauth.mendeley.init")	
				.process(mendeleyInitOAuthParams)
				.process(signingProcessor)
				.process(oauthQueryGenerator)
				.removeHeaders("CamelHttpUri")
				.removeHeaders("CamelHttpPath")
				.setHeader("Host", simple("api.mendeley.com"))
				.to("http4://api.mendeley.com/oauth/request_token/")
				.process(new MendeleyProcessResponse())			
			;
			

			from("direct:oauth.mendeley.connect")		 
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
			
			
			//=======================================================
			
			
			
			
			
			
			
			
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
				//.to("log:ex1.1?showAll=true") 	
				.to("direct:elastic.userSearch")
				
				//.unmarshal().string("UTF-8")
				//.wireTap("file:///tmp/merge/?fileName=example.json")
				
				.process(profileSplitter)
				.process(eexcessProfileMapper)
				.process(mendeleyProfileMapper)
				.to("string-template:templates/profile.tm")

				.setHeader("ElasticType").constant("data")
				.setHeader("ElasticIndex").constant("users")
				.setHeader("traceId").property("user_id")
				//.to("log:just_befire_indexing?showAll=true")
				.to("seda:elastic.trace.index")
			;
		}
}
