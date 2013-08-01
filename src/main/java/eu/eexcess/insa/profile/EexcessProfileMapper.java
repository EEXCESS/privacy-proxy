package eu.eexcess.insa.profile;

import static eu.eexcess.insa.profile.Mapper.*;

import java.util.regex.Matcher;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonNode;

import eu.eexcess.insa.commons.Strings;

public class EexcessProfileMapper implements Processor {


	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		JsonNode userGivenProfile = exchange.getProperty("profile.eexcess",JsonNode.class);
		//in.setBody(userGivenProfile);
		if(userGivenProfile != null && !userGivenProfile.isMissingNode() ){
			// Map address: street, postalcode, city, country
			if(!userGivenProfile.path("address").isMissingNode()){
				if(!userGivenProfile.path("address").path("street").isMissingNode()){
					String userStreet = userGivenProfile.path("address").path("street").getTextValue();
					fillHeader(in, "ProfileAddressStreet", userStreet);
				}
				
				if(!userGivenProfile.path("address").path("postalcode").isMissingNode()){
					String userPostalcode = userGivenProfile.path("address").path("postalcode").getTextValue();
					fillHeader(in, "ProfileAddressPostalCode", userPostalcode);
				}
				
				if(!userGivenProfile.path("address").path("city").isMissingNode()){
					String userCity = userGivenProfile.path("address").path("city").getTextValue();
					fillHeader(in, "ProfileAddressCity", userCity);
				}
				
				if(!userGivenProfile.path("address").path("country").isMissingNode()){
					String userCountry = userGivenProfile.path("address").path("country").getTextValue();
					fillHeader(in, "ProfileAddressCountry", userCountry);
				}
			}
			
			// Map: username
			if(!userGivenProfile.path("username").isMissingNode()){
				String userUsername = userGivenProfile.path("username").getTextValue();
				fillHeader(in, "ProfileUsername", userUsername);
			}
			
			// Map: email
			if(!userGivenProfile.path("email").isMissingNode()){
				String userEmail = userGivenProfile.path("email").getTextValue();
				fillHeader(in, "ProfileEmail", userEmail);
			}
			
			// Map: password
			if(!userGivenProfile.path("password").isMissingNode()){
				String userPassword = userGivenProfile.path("password").getTextValue();
				fillHeader(in, "ProfilePassword", userPassword);
			}
			
			// Map: title
			if(!userGivenProfile.path("title").isMissingNode()){
				String userTitle = userGivenProfile.path("title").getTextValue();
				fillHeader(in, "ProfileTitle", userTitle);
			}
			
			// Map: lastname
			if(!userGivenProfile.path("lastname").isMissingNode()){
				String userLastname = userGivenProfile.path("lastname").getTextValue();
				fillHeader(in, "ProfileLastName", userLastname);
			}
			
			// Map: firstname
			if(!userGivenProfile.path("firstname").isMissingNode()){
				String userFirstname = userGivenProfile.path("firstname").getTextValue();
				fillHeader(in, "ProfileFirstName", userFirstname);
			}
			
			// Map: gender
			if(!userGivenProfile.path("gender").isMissingNode()){
				String userGender = userGivenProfile.path("gender").getTextValue();
				fillHeader(in, "ProfileGender", userGender);
			}
			
			// Map: birthdate
			if(!userGivenProfile.path("birthdate").isMissingNode()){
				String userBirthdate = userGivenProfile.path("birthdate").getTextValue();
				fillHeader(in, "ProfileBirthDate", userBirthdate);
			}
			
			// Map privacy
			if(!userGivenProfile.path("privacy").isMissingNode()){
				if(!userGivenProfile.path("privacy").path("email").isMissingNode()){
					String userPrivacyEmail = userGivenProfile.path("privacy").path("email").getTextValue();
					fillHeader(in, "ProfilePrivacyEmail", userPrivacyEmail);
				}
				
				if(!userGivenProfile.path("privacy").path("gender").isMissingNode()){
					String userPrivacyGender = userGivenProfile.path("privacy").path("gender").getTextValue();
					fillHeader(in, "ProfilePrivacyGender", userPrivacyGender);
				}
				
				if(!userGivenProfile.path("privacy").path("title").isMissingNode()){
					String userPrivacyTitle = userGivenProfile.path("privacy").path("title").getTextValue();
					fillHeader(in, "ProfilePrivacyTitle", userPrivacyTitle);
				}
				
				if(!userGivenProfile.path("privacy").path("Traces").isMissingNode()){
					String userPrivacyTraces = userGivenProfile.path("privacy").path("traces").getTextValue();
					fillHeader(in, "ProfilePrivacyTraces", userPrivacyTraces);
				}
				
				if(!userGivenProfile.path("privacy").path("geoloc").isMissingNode()){
					String userPrivacyGeoloc = userGivenProfile.path("privacy").path("geoloc").getTextValue();
					fillHeader(in, "ProfilePrivacyGeoloc", userPrivacyGeoloc);
				}
				
				if(!userGivenProfile.path("privacy").path("age").isMissingNode()){
					String userPrivacyAge = userGivenProfile.path("privacy").path("age").getTextValue();
					fillHeader(in, "ProfilePrivacyAge", userPrivacyAge);
				}
				
				if(!userGivenProfile.path("privacy").path("address").isMissingNode()){
					String userPrivacyAddress = userGivenProfile.path("privacy").path("address").getTextValue();
					fillHeader(in, "ProfilePrivacyAddress", userPrivacyAddress);
				}
			}
			
			// Map topics
			if(!userGivenProfile.path("topics").isMissingNode()){
				
				int nbTopics = userGivenProfile.path("topics").size();
				String[] topics = new String[ nbTopics ];
				for ( int i = 0 ; i < nbTopics ; i++ ){
					topics[i] = "{\"label\":\""+userGivenProfile.path("topics").get( i ).path("label").getTextValue() + "\",\"env\":\""+userGivenProfile.path("topics").get( i ).path("env").getTextValue()+"\",\"source\":\"eexcess\"}";
				}
				fillHeader(in, "profileTopics" , topics);
			}
		}	
	}
}
