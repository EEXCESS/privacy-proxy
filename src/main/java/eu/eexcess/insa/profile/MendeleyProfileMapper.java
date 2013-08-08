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
			
			// Map main.name -> profileLastName + profileFirstName
			{
				String mendeleyUserName = mendeleyProfile.path("main").path("name").getTextValue();
				String[]splittedName = mendeleyUserName.split(" ");
				if(splittedName[0] != null ){
					fillHeader(in, "ProfileFirstName", splittedName[0]);
				}
				if(splittedName[1] != null ){
					fillHeader(in, "ProfileLastName", splittedName[1]);
				}		
			}
			
			// Map contact.zipcode -> profileAddressPostalCode
			{
				String mendeleyZipcode = mendeleyProfile.path("contact").path("zipcode").getTextValue();
				fillHeader(in, "ProfileAddressPostalCode", mendeleyZipcode);
									
			}
			
			// Map contact.zipcode -> profileAddressStreet
			{
				String mendeleyAddress = mendeleyProfile.path("contact").path("address").getTextValue();
				fillHeader(in, "ProfileAddressStreet", mendeleyAddress);
									
			}
			
			// map main.research_interests -> profileTopics
			{
				String[] topicsEexcess = (String[]) in.getHeader("profileTopics");
				if(topicsEexcess == null) {
					topicsEexcess = new String[]{};
				}
				
				String interests = mendeleyProfile.path("main").path("research_interests").getTextValue();
				String charLimit= ",.:;";
				char[] limit= charLimit.toCharArray();
				String tabInterests[] = interests.split("[;,.:]");
				
				String[] topics = new String[topicsEexcess.length + tabInterests.length];
				
				for(int i=0;i<topicsEexcess.length;i++){
					topics[i] = topicsEexcess[i];
				}
				int nbTopics = topicsEexcess.length;
				
				for(int i=0;i<tabInterests.length;i++){
					topics[i+nbTopics] = "{\"label\":\""+tabInterests[i]+"\",\"env\":\"work\",\"source\":\"mendeley\"}";
				}
				fillHeader(in, "profileTopics" , topics);

			}
			
			
		}
		
	}

	
}
