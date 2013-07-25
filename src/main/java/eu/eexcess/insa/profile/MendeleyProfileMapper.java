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
		JsonNode existingProfile = in.getBody(JsonNode.class);
		JsonNode medeleyProfile = exchange.getProperty("profile.mendeley",JsonNode.class);
		
		
		// Map main.location -> address.city + address.country
		{
			String mendeleyUserLocation = medeleyProfile.path("main").path("location").getTextValue();
			Matcher userLocationMatcher = locationPattern.matcher(mendeleyUserLocation);
			if(userLocationMatcher.find()) {
				String userCity = userLocationMatcher.group(1);
				String userCountry = userLocationMatcher.group(2);
				fillHeader(in, "ProfileAddressCity", userCity);
				fillHeader(in, "ProfileAddressCity", userCity);
			}
		}
		
		
	}

	
}
