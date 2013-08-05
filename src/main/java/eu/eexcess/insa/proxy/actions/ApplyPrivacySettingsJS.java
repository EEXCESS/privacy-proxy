package eu.eexcess.insa.proxy.actions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import javax.script.*;
public class ApplyPrivacySettingsJS implements Processor{
	ScriptEngine engine;
	public ApplyPrivacySettingsJS() throws ScriptException {
		InputStream privacyJS= ClassLoader.getSystemResourceAsStream("javascript/PrivacyRules.js");
		ScriptEngineManager factory = new ScriptEngineManager();
		engine = factory.getEngineByName("JavaScript");
		engine.eval(new InputStreamReader(privacyJS));
	}

	public void process(Exchange exchange) throws Exception {
		// foreach attribute for which we have privacy settings do
		// applyPrivacy(...) and replace value with output
		
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
	 * @throws ScriptException 
	 */
	private void writeNewProfile( JsonNode rootNode, JsonGenerator jg , HashMap<String, Integer> settings) throws JsonGenerationException, IOException, ScriptException{
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
							if(!userProfile.path("_source").path("email").isMissingNode()){
								
								String email = applyPrivacy("email",userProfile.path("_source").path("email").asText(),settings.get("email"));
								if ( !email.equals("nothing")){
									jg.writeStringField("email", email );
								}
								
							}
						}
						if ( settings.containsKey("gender")){
							if(!userProfile.path("_source").path("gender").isMissingNode()){
								String gender = applyPrivacy("gender",userProfile.path("_source").path("gender").asText(),settings.get("gender"));
								if ( !gender.equals("nothing")){
									jg.writeStringField("gender", gender );
								}
							}
						}
						
						if ( settings.containsKey("title")){
							if(!userProfile.path("_source").path("title").isMissingNode()){
								String title = applyPrivacy("title",userProfile.path("_source").path("title").asText(),settings.get("title"));
								if ( !title.equals("nothing")){
									jg.writeStringField("title", title );
								}
							}
						}
						
						if ( settings.containsKey("geoloc")){
							if( !userProfile.path("_source").path("geoloc").isMissingNode()){
								// TODO : see how we store the geolocation ( in the user profile, in the traces ... )
								//String geoloc = applySettings("geoloc", );
								
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
	 * This gets the displayed information for a given parameter according to a given disclosure level
	 * @param attribute : the name of the parameter we apply a privacy setting on
	 * @param rawValue : the value of the parameter as stored into the user's profile
	 * @param disclosureLevel : the level of information the user wants to share
	 * 							what informations are displayed for a given disclosure level is specific for every parameter
	 * @return : the given parameter showing the level of information defined by the given discosure level
	 * @throws ScriptException
	 */
	public String applyPrivacy(String attribute, String rawValue, int disclosureLevel) throws ScriptException {
		//TODO : check the user given parameters to prevent javascript injection
		
		String jsExpr = "privacy.apply('"+attribute+"','"+rawValue+"',"+disclosureLevel+")";
		Object result = engine.eval(jsExpr);
		return result.toString();
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
								
								if ( rootNode.path("hits").path("hits").get(0).path("_source").path("privacy").path(fieldName).isIntegralNumber() ){
									int value = rootNode.path("hits").path("hits").get(0).path("_source").path("privacy").path(fieldName).asInt();
									privacySettings.put(fieldName, value);
								}
								
								
							}
							
						}
					}
				}
			}
		}	
		return privacySettings;
	}
	

	
	public static void main(String[] args) throws ScriptException, JsonParseException, JsonMappingException, IOException {
		ApplyPrivacySettingsJS privacy = new ApplyPrivacySettingsJS();
		String attribute ="birthdate";
		String rawValue = "1992-06-03";
		Integer disclosureLevel = -1;
		//InputStream is = exchange.getProperty("user_context-traces", InputStream.class);
		String is = privacy.applyPrivacy(attribute, rawValue, disclosureLevel);
		
		System.out.println(is);
		if ( !is.equals("nothing")){
			JsonFactory factory = new JsonFactory();
			StringWriter sWriter = new StringWriter();
			JsonGenerator jg = factory.createJsonGenerator(sWriter);
			//JsonParser jp = factory.createJsonParser(is);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(is);
			System.out.println(jnode);
		}
		
	}
}
