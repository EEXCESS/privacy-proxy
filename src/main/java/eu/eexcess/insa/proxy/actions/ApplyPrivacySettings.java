package eu.eexcess.insa.proxy.actions;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

/*
 *  This processor reads the user's profile from the user_profile exchange property 
 *  and gets rid of the unwanted informations following the privacy category of 
 *  the user's profile
 *  
 *  This processor also builds a query to get the user's last traces according to the privacy settings 
 */
public class ApplyPrivacySettings implements Processor {

	public void process(Exchange exchange) throws Exception {
		InputStream is = exchange.getProperty("user_context-traces", InputStream.class);
		
		JsonFactory factory = new JsonFactory();
		JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	    
	    HashMap<String,Integer> privacySettings = getPrivacySettings(rootNode);
	    
	    StringWriter sWriter = new StringWriter();
	    JsonGenerator jg = factory.createJsonGenerator(sWriter);
	    
	    writeNewProfile( rootNode, jg, privacySettings);
	    

	}
	
	
	
	/**
	 * This builds a new user profile according to the privacy settings
	 * @param rootNode : the raw profile
	 * @param jg : the generator used to build json
	 * @param settings : the privacy settings
	 * @throws IOException 
	 * @throws JsonGenerationException 
	 */
	private void writeNewProfile( JsonNode rootNode, JsonGenerator jg , HashMap<String, Integer> settings) throws JsonGenerationException, IOException{
		jg.writeStartObject();
		if ( !rootNode.path("hits").isMissingNode()){
			if(!rootNode.path("hits").path("hits").isMissingNode()){
				if( rootNode.path("hits").path("hits").get(0) != null){
					JsonNode userProfile = rootNode.path("hits").path("hits").get(0);
					
					//first some metadata
					if( !userProfile.path("_index").isMissingNode()){
						jg.writeStringField("_index", userProfile.path("_index").asText());
					}
					if( !userProfile.path("_type").isMissingNode()){
						jg.writeStringField("_type", userProfile.path("_type").asText());
					}
					if( !userProfile.path("_id").isMissingNode()){
						jg.writeStringField("_id", userProfile.path("_id").asText());
					}
					
					//the user profile
					jg.writeFieldName("_source");
					jg.writeStartObject();
					if( !userProfile.path("_source").isMissingNode()){
						
						
						if ( settings.containsKey("email")){
							if( settings.get("email") == 1 && !userProfile.path("_source").path("email").isMissingNode()){
								jg.writeStringField("email", userProfile.path("_source").path("email").asText() );
							}
						}
						if ( settings.containsKey("gender")){
							if( settings.get("gender") == 1 && !userProfile.path("_source").path("gender").isMissingNode()){
								jg.writeStringField("gender", userProfile.path("_source").path("gender").asText() );
							}
						}
						
						if ( settings.containsKey("title")){
							if( settings.get("title") == 1 && !userProfile.path("_source").path("title").isMissingNode()){
								jg.writeStringField("title", userProfile.path("_source").path("title").asText() );
							}
						}
						
						if ( settings.containsKey("email")){
							if( settings.get("email") == 1 && !userProfile.path("_source").path("email").isMissingNode()){
								jg.writeStringField("email", userProfile.path("_source").path("email").asText() );
							}
						}
						
					}
					
					
					
					jg.writeEndObject();
					
				}
			}
		}
				
		
		
		jg.writeEndObject();
		jg.close();
	}
	
	/**
	 * This give the user's age using his birthdate, according to the privacy setting
	 * @param birthDate : user's birthdate
	 * @param setting : defines the way the age will be returned
	 * @return : the user's age
	 */
	private String giveAge ( String birthDate, int setting ){
		GregorianCalendar gc = new GregorianCalendar(); //needs to be properly initialized
		String[] s = birthDate.split("-");
		int year = Integer.parseInt(s[0]);
		int month = Integer.parseInt(s[1]);
		int day = Integer.parseInt(s[2]);
		
		gc.set( year, month, day);
		Date usersBDate = gc.getTime();
		int age = gc.get(Calendar.YEAR);
	    
		gc.setTime(new Date());
		int currentYear = gc.get(Calendar.YEAR);
	    
		int usersAge = currentYear-age;
	    
	    String displayedAge ="";
	    
	    
		switch ( setting ){
			case 0 : { // the user doesn't want to give his age
				break;
			}
			case 1 : { // the user agreed to show his age range
				int ageRange = (usersAge - usersAge%10 );
				displayedAge+= ageRange;
			    displayedAge+= "'s";
			    break;
			}
			case 2 : { // the user agreed to give his age
				displayedAge+= usersAge;
				break;
			}
			case 3 : { // the users agreed to give his birthdate
				displayedAge = birthDate;
				break;
			}
		}
		
		return displayedAge;
		
		
	}
	
	
	/**
	 * This gets the privacy settings from the user profile 
	 * @param rootNode the raw elasticsearch response as a JsonNode, contains the user's profile
	 * @return a hashmap mapping each privacy setting with its numerical value
	 */
	private HashMap<String, Integer> getPrivacySettings ( JsonNode rootNode){
		HashMap<String, Integer> privacySettings = new HashMap<String, Integer>();
		if ( !rootNode.path("hits").isMissingNode()){
			if(!rootNode.path("hits").path("hits").isMissingNode()){
				if( rootNode.path("hits").path("hits").get(0) != null){
					if(!rootNode.path("hits").path("hits").get(0).path("_source").isMissingNode()){
						if(!rootNode.path("hits").path("hits").get(0).path("_source").path("privacy").isMissingNode()){
							Iterator<String> it = rootNode.path("hits").path("hits").get(0).path("_source").path("privacy").getFieldNames();
							while(it.hasNext()){
								String fieldName = it.next();
								// warning : some privacy settings fields are empty, need to check what the asInt function does then
								int value = rootNode.path("hits").path("hits").get(0).path("_source").path("privacy").path(fieldName).asInt();
								privacySettings.put(fieldName, value);
								
							}
							
						}
					}
				}
			}
		}	
		return privacySettings;
	}
	

}
