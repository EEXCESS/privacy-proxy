package eu.eexcess.insa.profile;

import java.util.regex.Matcher;

import static eu.eexcess.insa.profile.Mapper.*;

import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonNode;

public class MendeleyProfileMapper implements Processor {
	static Pattern locationPattern = Pattern.compile("([^,]*)[ ]*,[ ]*([^,]*)");
	

	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		//JsonNode existingProfile = in.getBody(JsonNode.class);
		JsonNode mendeleyProfile = exchange.getProperty("profile.mendeley",JsonNode.class);
		//System.out.println(mendeleyProfile);
		
		if ( mendeleyProfile != null){
			// Map main.location -> addressCity + addressCountry
			{
				String mendeleyUserLocation = mendeleyProfile.path("main").path("location").getTextValue();
				Matcher userLocationMatcher = locationPattern.matcher(mendeleyUserLocation);
				if(userLocationMatcher.find()) {
					String userCity = userLocationMatcher.group(1);
					String userCountry = userLocationMatcher.group(2);
					fillHeader(in, "ProfileAddressCity", userCity);
					fillHeader(in, "ProfileAddressCountry", userCountry);
				}
			}
			
			// Map main.name -> profileLastName + profileFirstName + profileUsername
			{
				String mendeleyUserName = mendeleyProfile.path("main").path("name").getTextValue();
				String[]splittedName = mendeleyUserName.split(" ");
				if(splittedName[0] != null ){
					fillHeader(in, "ProfileFirstName", splittedName[0]);
				}
				if(splittedName[1] != null ){
					fillHeader(in, "ProfileLastName", splittedName[1]);
				}
				fillHeader(in, "ProfileUsername", mendeleyUserName);
			}
			
			// Map contact.zipcode -> profileAddressPostalCode
			{
				String mendeleyZipcode = mendeleyProfile.path("contact").path("zipcode").getTextValue();
				fillHeader(in, "ProfileAddressPostalCode", mendeleyZipcode);
									
			}
			
			// Map contact.address -> profileAddressStreet
			{
				String mendeleyAddress = mendeleyProfile.path("contact").path("address").getTextValue();
				fillHeader(in, "ProfileAddressStreet", mendeleyAddress);
									
			}
			
			// Map contact.email -> ProfileEmail
			{
				if ( !mendeleyProfile.path("contact").path("email").isMissingNode()){
					String mendeleyEmail = mendeleyProfile.path("contact").path("email").asText();
					fillHeader(in, "ProfileEmail", mendeleyEmail);
				}
			}
			
			// map main.research_interests -> profileTopics
			{
				
				String[] topicsEexcess = (String[]) in.getHeader("profileTopics");
				if(topicsEexcess == null) {
					topicsEexcess = new String[]{};
				}
				
				String interests = mendeleyProfile.path("main").path("research_interests").getTextValue();
				interests = interests.replace("\n"," ");
				String charLimit= ",.:;";
				char[] limit= charLimit.toCharArray();
				String tabInterests[] = interests.split("[;,.:]");
				int nbTopics =0;
				int topicsEexcessLength;
				if ( topicsEexcess != null ){
				
					topicsEexcessLength = topicsEexcess.length;
				}
				else{
					topicsEexcessLength = 0;
				}
					String[] topics = new String[topicsEexcessLength + tabInterests.length];
					
					for(int i=0;i<topicsEexcessLength;i++){
						topics[i] = topicsEexcess[i];
					}
					nbTopics = topicsEexcessLength;
				
				for(int i=0;i<tabInterests.length;i++){

					//System.out.println(i+":"+topics[i+nbTopics-1]);


					topics[i+nbTopics] = "{\"label\":\""+tabInterests[i]+"\",\"env\":\"work\",\"source\":\"mendeley\"}";
				}
				fillHeader(in, "profileTopics" , topics);

			}
			
			
		}
		
	}

	
}
