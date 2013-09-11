package eu.eexcess.insa.proxy;

import org.apache.camel.builder.RouteBuilder;

public class ProxyService extends RouteBuilder  {
	String apiBaseURI = "servlet://";
	
	public void configure() throws Exception {
		/*
		 *  This route stores a trace into an elasticsearch database
		 * 
		 * INPUT:
		 *   A trace in JSON format
		 *   
		 * OUTPUT:
		 *  A response from Elasticsearch, JSN format :
		 *  
		 * {
		 * 	ok: true
		 *	_index: privacy
		 *	_type: trace
		 *	_id: 8XKl3FqqTcWiq1Cokr3W9Q
		 *	_version: 1
		 *}
		 *   
		 * 
		 */
		from(apiBaseURI + "api/v0/privacy/trace").to("direct:trace.index");

		/* 
		 *  <quoi>
		 * 
		 * INPUT:
		 *   <Format du message>
		 *   
		 * PARAMETERS:
		 *   <nom>: <descirption>
		 * 
		 * OUTPUT:
		 *   <Format du message>
		 *   
		 */
		from(apiBaseURI+ "api/v0/users/profile").to("direct:get.user.profile");


		/* 
		 *  <quoi>
		 * 
		 * INPUT:
		 *   <Format du message>
		 *   
		 * PARAMETERS:
		 *   <nom>: <descirption>
		 * 
		 * OUTPUT:
		 *   <Format du message>
		 *   
		 * TODO map user/traces -> api/v0/user/traces
		 */
		from(apiBaseURI+"api/v0/user/traces").to("direct:retrieve.user.traces");
		
		
		/* 
		 *  <quoi>
		 * 
		 * INPUT:
		 *   <Format du message>
		 *   
		 * PARAMETERS:
		 *   <nom>: <descirption>
		 *   
		 * OUTPUT:
		 *   <Format du message>
		 *   
		 */
		from(apiBaseURI + "api/v0/users/data").to("direct:update.user.profile");


		/* 
		 *  <quoi>
		 * 
		 * INPUT:
		 *   <Format du message>
		 *   
		 * PARAMETERS:
		 *   <nom>: <descirption>
		 *   
		 * OUTPUT:
		 *   <Format du message>
		 *   
		 * 
		 */
		from(apiBaseURI + "api/v0/users/privacy_settings").to("direct:save.privacy.settings");

			

		/*
		 * Route to check if given username or email currently exit
		 * INPUT: JSON with the following structure
		 *   {
		 *      "user_email": <USER_EMAIL>,
		 *      "user_id": <USER_ID>
		 *   }
		 *   
		 *    NOTE: Only one of the keys is necessary
		 * 
		 * OUTPUT: ElasticSearch search results with hits of the form:
		 *   {
		 * TODO Complete with example user data hit
		 *   }
		 *   
		 * TODO map user/verify -> api/v0/user/exists
		 * 
		 */
		from(apiBaseURI + "api/v0/user/exists").to("direct:verify.user");
				
			
		/*
		 * Route to check if given username or email currently exit
		 * INPUT: JSON with the following structure
		 *   {
		 *      "user_email": <USER_EMAIL>,
		 *      "user_id": <USER_ID>
		 *   }
		 *   
		 *    NOTE: Only one of the keys is necessary
		 * 
		 * OUTPUT: ElasticSearch search results with hits of the form:
		 *   {
		 * TODO Complete with whatever is returned on user authentication
		 *   }
		 *  
		 * TODO map user/verify -> api/v0/user/exists
		 */
		from(apiBaseURI+"api/v0/user/authenticate").to("direct:user.login");
			
			
			
		
		/* ================================================================
		 * 						MENDELEY CONNECT ROUTES
		 * ================================================================
		 */
		
		
		/*
		 * Route to initialize Mendeley OAuth authentifiaction
		 *  ( get request token and other informations
		 *  
		 *  TODO map oauth/mendeley/init -> api/v0/connect/mendeley/init
		 */
		from(apiBaseURI+"api/v0/connect/mendeley/init").to("direct:oauth.mendeley.init");
				
			
		/*
		 * Route to continue Mendeley Oauth authentification
		 *  (get access token and other informations)
		 *  
		 *  TODO map oauth/mendeley/connect -> api/v0/connect/mendeley/validate
		 */
		from(apiBaseURI+ "api/v0/connect/mendeley/validate").to("direct:oauth.mendeley.connect");
				
			
		//=======================================================
	}
}
