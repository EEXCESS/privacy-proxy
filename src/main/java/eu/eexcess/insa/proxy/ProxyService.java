package eu.eexcess.insa.proxy;

import org.apache.camel.builder.RouteBuilder;

public class ProxyService extends RouteBuilder  {
	String apiBaseURI = "servlet://";
	
	public void configure() throws Exception {
		/*
		 *  This route stores a trace
		 * 
		 * INPUT:
		 *   A trace in JSON format
		 *   //TODO complete with an example
		 *   
		 * OUTPUT:
		 *  A response from Elasticsearch, JSON format :
		 *  
		 * {
		 * 	"ok": true
		 *	"_index": privacy
		 *	"_type": "trace"
		 *	"_id": "<id assigned to the sent trace by Elasticsearch>"
		 *	"_version": <number of times the trace have been updated>
		 *}
		 *
		 */
		from(apiBaseURI + "api/v0/privacy/trace").to("direct:trace.index");

		/* 
		 *  This route gets a given user's profile
		 * 
		 * INPUT:
		 *  {
		 * 		"_id": "<id>"
		 * 	}
		 * 
		 * OUTPUT:
		 * {
		 * 	"id": "<id>",
		 *	"values":{
		 *			<rest of the user profile>
		 * // TODO : complete with an user profile example
		 *		}
		 * }

		 *   
		 */
		from(apiBaseURI+ "api/v0/user/profile").to("direct:get.user.profile");


		/* 
		 *  This route gets traces corresponding to either a plugin, an user or both, depending of the informations sent
		 * 
		 * INPUT:
   		 * {
		 *	"pluginId": "<plugin's id>";
		 *	"userId": "<user's id>",
		 *	"environnement": "<environnement ( either "work" or "home">"
		 * }
		 * 	NOTE: If both fields are sent, the results will match both of them and be different than the combined results of 
		 * 		an api call with the user id and another call with the plugin id
		 * 
		 * OUTPUT:
		 *   an Elasticsearch list of traces, ordered from latest to oldest, maximum 50	
		 *   
		 *   {
		 *		 took: 4
		 * 		timed_out: false
		 * 		_shards: {
		 * 		total: 5
		 * 		successful: 5
		 * 		failed: 0
		 * 	}
		 * 	hits: {
		 * 		total: 50
		 * 		max_score: 1
		 * 		hits: [
		 * 				< the traces >
		 * 		]
		 * 	}
		 * }
		 *   
		 *
		 */
		from(apiBaseURI+"api/v0/user/traces").to("direct:retrieve.user.traces");
		
		
		/* 
		 *  This route updates an user profile 
		 * 
		 * INPUT:
		 *   the user profile, JSON format
		 *   //TODO : complete with an user profile example
		 *   
		 * OUTPUT:
		 *  a response from Elasticsearch, JSON format :
		 *  
		 * {
		 * 	"ok": true
		 *	"_index": "users"
		 *	"_type": "data"
		 *	"_id": "<user id>"
		 *	"_version": <number of times the user have been updated>
		 *}
		 *   
		 */
		from(apiBaseURI + "api/v0/user/data").to("direct:update.user.profile");


		/* 
		 *  This route updates an user profile directly without merging it with other profiles first
		 * 
		 * INPUT:
		 *   the user profile, JSON format
		 *   //TODOP complete with an user profile example
		 *   
		 * OUTPUT:
		 *   <user id>
		 *   
		 * 
		 */
		from(apiBaseURI + "api/v0/user/privacy_settings").to("direct:save.privacy.settings");

			

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
		 * OUTPUT: 
		 * 
		 * {
		 *	"hits": <number>
		 * }
		 * 
		 */
		from(apiBaseURI + "api/v0/user/exists").to("direct:verify.user");
				
			
		/*
		 * Route to get an user's id from his login informations ( username/ email and password )
		 * INPUT:
		 * 
		 *	{"term": {"data.<email | username>": "<email | username>"},{"term": {"data.password": "<password>"}}
		 *    
		 *    NOTE: You can user either the email or the username
		 * 
		 * OUTPUT:
		 * 
		 * {
		 *	 "loginValid": "< 1 if the credentials were correct, 0 if they weren't >",
		 *	 "id": "<user id>",
		 * 	 "username": <username>
		 * }
		 *   
		 */
		from(apiBaseURI+"api/v0/user/authenticate").to("direct:user.login");
			
			
			
		
		/* ================================================================
		 * 						MENDELEY CONNECT ROUTES
		 * ================================================================
		 */
		
		
		/*
		 * Route to initiate Mendeley OAuth authentication
		 *  
		 *  INPUT : nothing
		 *  
		 *  OUTPUT : 
		 *  	HEADERS : oauth_token, oauth_token_secret
		 *  		NOTE : these credentials are given by Mendeley and are needed to pursue the oauth process
		 *  				( redirecting the user to mendeley's authorization page )
		 */
		from(apiBaseURI+"api/v0/connect/mendeley/init").to("direct:oauth.mendeley.init");
				
			
		/*
		 * Route to finalize the Mendeley oauth authentication
		 * 
		 * INPUT :
		 * 	HEADERS :
		 * 		user_id (optional, is used to merge the mendeley profile with an existing eexcess profile)
		 * 		oauth_token
		 * 		oauth_token_secret
		 * 		ouath_verifier
		 * 
		 *  OUTPUT : 
		 *{
		 * 	"ok": true
		 *	"_index": "users"
		 *	"_type": "data"
		 *	"_id": "<user id>"
		 *	"_version": <number of times the user have been updated>
		 *}
		 *  	
		 */
		from(apiBaseURI+ "api/v0/connect/mendeley/validate").to("direct:oauth.mendeley.connect");
				
			
		//=======================================================
	}
}
