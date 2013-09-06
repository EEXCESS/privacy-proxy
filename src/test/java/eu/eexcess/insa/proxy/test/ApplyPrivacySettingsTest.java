package eu.eexcess.insa.proxy.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.script.ScriptException;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.retry.ExhaustedRetryException;

import eu.eexcess.insa.oauth.OAuthSigningProcessor;
import eu.eexcess.insa.proxy.APIService;
import eu.eexcess.insa.proxy.actions.ApplyPrivacySettingsJS;

public class ApplyPrivacySettingsTest extends CamelTestSupport {
	
	public String profile;
	
	//http://tools.ietf.org/html/rfc5849
	
	
	
	
    @Produce(uri = "direct:apply_privacy")
   /* protected ProducerTemplate template;
	private Processor prepMessageProcessor = new Processor() {
		public void process(Exchange exchange) throws Exception {
			
			exchange.setProperty("user_context-profile", testUserProfile);

		}
	};
	*/
	
	public ArrayList<HashMap<String,String>> addressTestDataGenerator() throws IOException{
		
		ArrayList<HashMap<String,String>> inputData = new ArrayList<HashMap<String,String>>();
		
		HashMap<String, String> address1 = new HashMap<String, String> ( );
		
		HashMap<String, String> address1Data = new HashMap<String,String>();
		address1.put("0",formateAddress(address1Data));
		
		address1Data.put("country", "France");
		address1.put("1",formateAddress(address1Data));
		
		address1Data.put("region", "Rhône-Alpes");
		address1.put("2",formateAddress(address1Data));
		
		address1Data.put("district", "Région de Lyon");
		address1.put("3",formateAddress(address1Data));
		
		address1Data.put("city", "Villeurbanne");
		address1Data.put("postalcode", "69100");
		address1.put("4",formateAddress(address1Data));
		
		address1Data.put("street", "42 rue de la Liberté");
		address1Data.put("lattitude", "41.785110");
		address1Data.put("longitude", "23.889504");
		address1.put("5",formateAddress(address1Data));
		
		address1.put("raw", formateAddress(address1Data));
		
		inputData.add(address1);
		
		return inputData;
		
		
		
	    
	}
	
	String formateAddress ( HashMap<String, String> address) throws IOException{
		
		JsonFactory factory = new JsonFactory();
		StringWriter sWriter = new StringWriter();
		JsonGenerator jg = factory.createJsonGenerator(sWriter);
		
		jg.writeStartObject();
		if ( !address.isEmpty()){
			
			if ( address.containsKey("country")){
				jg.writeStringField("country", address.get("country"));
			}
			if ( address.containsKey("region")){
				jg.writeStringField("region", address.get("region"));
			}
			if ( address.containsKey("district")){
				jg.writeStringField("district", address.get("district"));
			}
			if ( address.containsKey("city")){
				jg.writeStringField("city", address.get("city"));
			}
			if ( address.containsKey("postalcode")){
				jg.writeStringField("postalcode", address.get("postalcode"));
			}
			if ( address.containsKey("street")){
				jg.writeStringField("street", address.get("street"));
			}
			if ( address.containsKey("longitude")){
				jg.writeStringField("longitude", address.get("longitude"));
			}
			if ( address.containsKey("lattitude")){
				jg.writeStringField("lattitude", address.get("lattitude"));
			}
			
		}

		jg.writeEndObject();
		jg.close();
		return sWriter.toString();
	}
	
	
	public ArrayList<HashMap<String,String>> birthDateTestDataGenerator(){
		
		ArrayList<HashMap<String,String>> inputData = new ArrayList<HashMap<String,String>>();
			
		HashMap<String,String> date1 = new HashMap<String, String>();
			date1.put("raw", "1992-06-03");
			date1.put("3", "{\"date\":\"1992-06-03\"}");
			date1.put("2", "{\"age\":\"21 years\"}");
			date1.put("1", "{\"decade\":\"20\"}");
			date1.put("0", "nothing");
			inputData.add(date1);
			
		HashMap<String,String> date2 = new HashMap<String, String>();
			date2.put("raw", "1982-12-3");
			date2.put("3", "{\"date\":\"1982-12-3\"}");
			date2.put("2", "{\"age\":\"31 years\"}");
			date2.put("1", "{\"decade\":\"30\"}");
			date2.put("0", "nothing");
			inputData.add(date2);
		return inputData;	
	}
	
	
	public ArrayList<HashMap<String,String>> emailTestDataGenerator(){
		
		ArrayList<HashMap<String,String>> inputData = new ArrayList<HashMap<String,String>>();
			
		HashMap<String,String> email1 = new HashMap<String, String>();
			email1.put("raw", "email.test@test.fr");
			email1.put("1", "email.test@test.fr");
			email1.put("0", "nothing");
			inputData.add(email1);
			
		HashMap<String,String> email2 = new HashMap<String, String>();
			email2.put("raw", "email");
			email2.put("1", "email");
			email2.put("0", "nothing");
			inputData.add(email2);
			
		return inputData;	
	}
	
	public ArrayList<HashMap<String,String>> titleTestDataGenerator(){
		
		ArrayList<HashMap<String,String>> inputData = new ArrayList<HashMap<String,String>>();
			
		HashMap<String,String> email1 = new HashMap<String, String>();
			email1.put("raw", "Mister");
			email1.put("1", "Mister");
			email1.put("0", "nothing");
			inputData.add(email1);
			
		HashMap<String,String> title2 = new HashMap<String, String>();
			title2.put("raw", "Miss");
			title2.put("1", "Miss");
			title2.put("0", "nothing");
			inputData.add(title2);
			
		HashMap<String,String> title3 = new HashMap<String, String>();
			title3.put("raw", "Misses");
			title3.put("1", "Misses");
			title3.put("0", "nothing");
			inputData.add(title3);
			
		return inputData;	
	}
	
	
	public ArrayList<HashMap<String,String>> genderTestDataGenerator(){
			
		ArrayList<HashMap<String,String>> inputData = new ArrayList<HashMap<String,String>>();
			
		HashMap<String,String> gender1 = new HashMap<String, String>();
			gender1.put("raw", "Male");
			gender1.put("1", "Male");
			gender1.put("0", "nothing");
			inputData.add(gender1);
			
		HashMap<String,String> gender2 = new HashMap<String, String>();
			gender2.put("raw", "Female");
			gender2.put("1", "Female");
			gender2.put("0", "nothing");
			inputData.add(gender2);
			
		return inputData;	
		}

	
	public ArrayList<String[]> applyPrivacySettingsTestDataGenerator( ) throws IOException{
		HashMap<String,String> data1 = new HashMap<String,String>(); 
		data1.put("privacyEmail", "0");
		data1.put("privacyGender", "1");
		data1.put("privacyTitle", "1");
		data1.put("privacyTraces", "1");
		data1.put("privacyGeoloc", "2");
		data1.put("privacyAge", "2");
		data1.put("privacyAddress", "3");
		return applyPrivacySettingsTestDataGenerator(data1);
	}
	
	public ArrayList<String[]> applyPrivacySettingsTestDataGenerator(HashMap<String,String> privacySettings) throws IOException{
		ArrayList<String[]> generatedProfiles = new ArrayList<String[]>();
		
		HashMap<String, String>data1 = new HashMap<String, String>();
		data1.put("userName", "userName");
		data1.put("email", "email");
		data1.put("password", "password");
		data1.putAll(privacySettings);
		data1.put("title", "title");
		data1.put("lastname", "lastname");
		data1.put("firstname", "firstname");
		data1.put("gender", "gender");
		data1.put("birthdate", "1992-06-03");
		data1.put("addressLattitude", "addressLattitude");
		data1.put("addressLongitude", "addressLongitude");
		data1.put("addressStreet", "addressStreet");
		data1.put("addressCity", "addressCity");
		data1.put("addressPostaCode", "addressPostaCode");
		data1.put("addressDistrict", "addressDistrict");
		data1.put("addressRegion", "addressRegion");
		data1.put("addressCountry", "addressCountry");
		
		String raw1 = profileGenerator(data1, false);
		
		HashMap<String, String>expectedData1 = new HashMap<String, String>();
		expectedData1.put("title", "title");
		expectedData1.put("gender", "gender");
		expectedData1.put("expectedBirthdate", "{\"age\": \"21 years\"}");
		expectedData1.put("addressDistrict", "addressDistrict");
		expectedData1.put("addressRegion", "addressRegion");
		expectedData1.put("addressCountry", "addressCountry");
		
		
		String expected1 = profileGenerator(expectedData1, true);
		
		String[] profile1 = { raw1, expected1 };
		
		generatedProfiles.add(profile1);
		
		
		return generatedProfiles;
		
	}
	
	/*================= profil en entrée ( à générer pour les tests)
	 * 
	 * 
	 * {
    "took": 2,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "failed": 0
    },
    "hits": {
        "total": 1,
        "max_score": 1,
        "hits": [
            {
                "_index": "users",
                "_type": "data",
                "_id": "fXLiU0NTSTO9R-olo1ZgAA",
                "_score": 1,
                "_source": {
                    "username": "Mathis",
                    "email": "mathis.paul@insa-lyon.fr",
                    "password": "223299646cffd9dac77d03e16800147d",
                    "privacy": {
                        "email": "",
                        "gender": "",
                        "title": "",
                        "traces": "",
                        "geoloc": "",
                        "age": "",
                        "address": ""
                    },
                    "title": "Mister",
                    "lastname": "Paul",
                    "firstname": "Mathis",
                    "gender": "Male",
                    "birthdate": "1992-06-03",
                    "address": {
                        "street": "13 rue Frederic Faÿs",
                        "postalcode": "69100",
                        "city": "Villeurbanne",
                        "country": "France"
                    },
                    "topics": [
                        {
                            "label": "network",
                            "env": "work",
                            "source": "eexcess"
                        },
                        {
                            "label": "computer",
                            "env": "home",
                            "source": "eexcess"
                        },
                        {
                            "label": "hadoop",
                            "env": "work",
                            "source": "eexcess"
                        },
                        {
                            "label": "machine learning",
                            "env": "home",
                            "source": "eexcess"
                        },
                        {
                            "label": "computer science",
                            "env": "all",
                            "source": "eexcess"
                        },
                        {
                            "label": "tetris",
                            "env": "nothing",
                            "source": "eexcess"
                        }
                    ]
                }
            }
        ]
    }
}
	 * 
	 * 
	 */
	public void writePrivacyField( JsonGenerator jg , String field, HashMap<String, String> data, String dataField ) throws JsonGenerationException, IOException{
		if ( data.containsKey(dataField)){
			jg.writeNumberField(field, Integer.parseInt(data.get(dataField)));
		}
	}
	public void writeStringField ( JsonGenerator jg, String field, HashMap<String, String> data) throws JsonGenerationException, IOException{
		writeStringField(jg, field, data, field);
	}
	public void writeStringField ( JsonGenerator jg, String field, HashMap<String, String> data, String dataField) throws JsonGenerationException, IOException{
		if ( data.containsKey(dataField)){
			jg.writeStringField(field, data.get(dataField));
		}
	}
	
	public String profileGenerator( HashMap<String,String> data, boolean expected) throws IOException{
		JsonFactory factory = new JsonFactory();
		StringWriter sWriter = new StringWriter();
		JsonGenerator jg = factory.createJsonGenerator(sWriter);
		
		if ( !expected ){
		jg.writeStartObject();
			jg.writeNumberField("took",2);
			jg.writeBooleanField("timed_out", false);
			jg.writeFieldName("_shards");
			jg.writeStartObject();
				jg.writeNumberField("total", 5);
				jg.writeNumberField("failed",0);
			jg.writeEndObject();
			jg.writeFieldName("hits");
			jg.writeStartObject();
				jg.writeNumberField("total", 1);
				jg.writeNumberField("max_score", 1);
				jg.writeFieldName("hits");
				jg.writeStartArray();
		}
					jg.writeStartObject();
						jg.writeStringField("_index", "users");
						jg.writeStringField("_type", "data");
						jg.writeStringField("_id", "fXLiU0NTSTO9R-olo1ZgAA");
						if ( !expected){
							jg.writeNumberField("_score", 1);
						}
						jg.writeFieldName("_source");
						jg.writeStartObject();
							writeStringField(jg, "username", data);
							writeStringField(jg, "email", data);
							writeStringField(jg, "passwordd", data);
							if ( data.containsKey("privacyEmail")||
									data.containsKey("privacyGender")||
									data.containsKey("privacyTitle")||
									data.containsKey("privacyTraces")||
									data.containsKey("privacyGeoloc")||
									data.containsKey("privacyAge")||
									data.containsKey("privacyAddress")
									){
									jg.writeFieldName("privacy");
									jg.writeStartObject();
										/*
										writePrivacyField(jg, "email", data, "privacyEmail");
										writePrivacyField(jg, "gender", data, "privacyGender");
										writePrivacyField(jg, "title", data, "privacyTitle");
										writePrivacyField(jg, "traces", data, "privacyTraces");
										writePrivacyField(jg, "geoloc", data, "privacyGeoloc");
										writePrivacyField(jg, "age", data, "privacyAge");
										writePrivacyField(jg, "address", data, "privacyAddress");
										*/
										
										jg.writeStringField("email", data.get("privacyEmail"));
										jg.writeStringField("gender", data.get("privacyGender"));
										jg.writeStringField("title", data.get("privacyTitle"));
										jg.writeStringField("traces", data.get("privacyTraces"));
										jg.writeStringField("geoloc", data.get("privacyGeoloc"));
										jg.writeStringField("age", data.get("privacyAge"));
										jg.writeStringField("address", data.get("privacyAddress"));
										
									jg.writeEndObject();
									}
							
							writeStringField(jg, "lastname", data);
							writeStringField(jg, "firstname", data);
							writeStringField(jg, "gender", data);
							writeStringField(jg, "title", data);
							writeStringField(jg, "birthdate", data);
							if ( data.containsKey("expectedBirthdate")){
								jg.writeFieldName("birthdate");
								JsonParser jp = factory.createJsonParser(data.get("expectedBirthdate"));
								ObjectMapper mapper = new ObjectMapper();
							    JsonNode birthdateNode = mapper.readValue(jp, JsonNode.class);
							    mapper.writeTree(jg, birthdateNode);
							}
							jg.writeFieldName("address");
							jg.writeStartObject();
								writeStringField(jg, "country", data, "addressCountry");
								writeStringField(jg, "region", data, "addressRegion");
								writeStringField(jg, "district", data, "addressDistrict");
								writeStringField(jg, "city", data, "addressCity");
								writeStringField(jg, "street", data, "addressStreet");
								writeStringField(jg, "longitude", data, "addressLongitude");
								writeStringField(jg, "lattitude", data, "addressLattitude");
							jg.writeEndObject();
							
							jg.writeFieldName("topics");
							jg.writeStartArray();
								jg.writeStartObject();
									jg.writeStringField("label", "chocolate");
									jg.writeStringField("env", "home");
									jg.writeStringField("source", "eexcess");
								jg.writeEndObject();
							jg.writeEndArray();
							jg.writeEndObject();
							
					jg.writeEndObject();
	   if ( ! expected){
				jg.writeEndArray();
			jg.writeEndObject();
		
		jg.writeEndObject();
		}
		jg.close();
		return sWriter.toString();
	}

/*
    @Test
    public void test() throws Exception {
    	
    	
    	Exchange resp = template.send(prepMessageProcessor);
		String filtered= resp.getProperty("user_context-profile",String.class);
		
		
		
	    
		
    }
    */
    
    @Test
    public void test_applyPrivacy_birthdate() throws ScriptException{
    	ApplyPrivacySettingsJS privacy = new ApplyPrivacySettingsJS();
		ArrayList<HashMap<String,String>> inputDatas = birthDateTestDataGenerator();
		Iterator<HashMap<String,String>> it =inputDatas.iterator();
		
		while ( it.hasNext()){
			HashMap<String,String> h = it.next();
			String field="birthdate";
		
			assertEquals( h.get("0"),privacy.applyPrivacy(field, h.get("raw"), 0) );
			assertEquals(  h.get("1"),privacy.applyPrivacy(field, h.get("raw"), 1));
			assertEquals( h.get("2"), privacy.applyPrivacy(field, h.get("raw"), 2));
			assertEquals( h.get("3"),privacy.applyPrivacy(field, h.get("raw"), 3));
			
		}
    }
    
    @Test
    public void test_applyPrivacy_email() throws ScriptException{
    	ApplyPrivacySettingsJS privacy = new ApplyPrivacySettingsJS();
		ArrayList<HashMap<String,String>> inputDatas = emailTestDataGenerator();
		Iterator<HashMap<String,String>> it =inputDatas.iterator();
		String field ="email";
		while ( it.hasNext()){
			HashMap<String,String> h = it.next();
			assertEquals( h.get("0"),privacy.applyPrivacy(field, h.get("raw"), 0) );
			assertEquals(  h.get("1"),privacy.applyPrivacy(field, h.get("raw"), 1));
		}
    }
    
    @Test
    public void test_applyPrivacy_title() throws ScriptException{
    	ApplyPrivacySettingsJS privacy = new ApplyPrivacySettingsJS();
		ArrayList<HashMap<String,String>> inputDatas = titleTestDataGenerator();
		Iterator<HashMap<String,String>> it =inputDatas.iterator();
		String field ="title";
		while ( it.hasNext()){
			HashMap<String,String> h = it.next();
			assertEquals( h.get("0"),privacy.applyPrivacy(field, h.get("raw"), 0) );
			assertEquals(  h.get("1"),privacy.applyPrivacy(field, h.get("raw"), 1));
		}
    }
    
    @Test
    public void test_applyPrivacy_gender() throws ScriptException{
    	ApplyPrivacySettingsJS privacy = new ApplyPrivacySettingsJS();
		ArrayList<HashMap<String,String>> inputDatas = genderTestDataGenerator();
		Iterator<HashMap<String,String>> it =inputDatas.iterator();
		String field ="gender";
		while ( it.hasNext()){
			HashMap<String,String> h = it.next();
			assertEquals( h.get("0"),privacy.applyPrivacy(field, h.get("raw"), 0) );
			assertEquals(  h.get("1"),privacy.applyPrivacy(field, h.get("raw"), 1));
		}
    }
    
    @Test
    public void test_applyPrivacy_address() throws ScriptException, IOException{
    	ApplyPrivacySettingsJS privacy = new ApplyPrivacySettingsJS();
		ArrayList<HashMap<String,String>> inputDatas = addressTestDataGenerator();
		Iterator<HashMap<String,String>> it =inputDatas.iterator();
		
		while ( it.hasNext()){
			HashMap<String,String> h = it.next();
			String field="address";
		
			assertEquals( h.get("0"),privacy.applyPrivacy(field, h.get("raw"), 0) );
			assertEquals(  h.get("1"),privacy.applyPrivacy(field, h.get("raw"), 1));
			assertEquals( h.get("2"), privacy.applyPrivacy(field, h.get("raw"), 2));
			assertEquals( h.get("3"),privacy.applyPrivacy(field, h.get("raw"), 3));
			assertEquals( h.get("4"),privacy.applyPrivacy(field, h.get("raw"), 4));
			assertEquals( h.get("5"),privacy.applyPrivacy(field, h.get("raw"), 5));
			
		}
    }
    
    class ParametrizedProcessor implements Processor{
    	JsonNode profile;
    	HashMap<String,Integer> pSettings;
    	String env;
		public void process(Exchange exchange) throws Exception {
			exchange.setProperty("user_context-profile",this.profile);
			exchange.setProperty("privacy_settings", pSettings);
			exchange.setProperty("environnement", this.env);
		}
    	
		public ParametrizedProcessor ( String p, HashMap<String,String> pSettings,String env) throws JsonParseException, IOException{
			JsonFactory factory = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper();
			JsonParser jp = factory.createJsonParser(p);
			JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
			this.profile = rootNode;
			
			HashMap<String,Integer> settings = new HashMap<String,Integer>();
			if ( pSettings.containsKey("privacyAge")){
				settings.put("age", Integer.parseInt(pSettings.get("privacyAge")));
			}
			if ( pSettings.containsKey("privacyEmail")){
				settings.put("email", Integer.parseInt(pSettings.get("privacyEmail")));
			}
			if ( pSettings.containsKey("privacyTitle")){
				settings.put("title", Integer.parseInt(pSettings.get("privacyTitle")));
			}
			if ( pSettings.containsKey("privacyTraces")){
				settings.put("traces", Integer.parseInt(pSettings.get("privacyTraces")));
			}
			if ( pSettings.containsKey("privacyGeoloc")){
				settings.put("geoloc", Integer.parseInt(pSettings.get("privacyGeoloc")));
			}
			if ( pSettings.containsKey("privacyAddress")){
				settings.put("address", Integer.parseInt(pSettings.get("privacyAddress")));
			}
			if ( pSettings.containsKey("privacyGender")){
				settings.put("gender", Integer.parseInt(pSettings.get("privacyGender")));
			}
			this.pSettings = settings;
			this.env=env;
			
			
		}
    }
    
    
    /*
     * test obsolete : the processor apply privacy settings has been modified but the test remained the same
     */
    @Test
    public void test_applyPrivacySettings() throws ScriptException, IOException{
    	HashMap<String,String> pSettings = new HashMap<String,String>();
    	pSettings.put("privacyEmail", "0");
    	pSettings.put("privacyGender", "1");
    	pSettings.put("privacyTitle", "1");
    	pSettings.put("privacyTraces", "1");
    	pSettings.put("privacyGeoloc", "2");
    	pSettings.put("privacyAge", "2");
    	pSettings.put("privacyAddress", "3");
    	ArrayList<String[]> data = applyPrivacySettingsTestDataGenerator(pSettings);
    	Iterator<String[]> it = data.iterator();
    	while ( it.hasNext()){
    		String[] profile = it.next();
	    	Processor p = new ParametrizedProcessor(profile[0], pSettings,"home");	
    		Exchange resp = template.request("direct:apply_privacy",p);
    		assertEquals(profile[1],resp.getProperty("user_context-profile",String.class));
    	}
    }
     

	
    @Override
    protected RouteBuilder createRouteBuilder() {
    	
    	return new APIService(){
        	public void configure() throws Exception {
        		super.configure();
        		ApplyPrivacySettingsJS privacy = new ApplyPrivacySettingsJS();
        		
        		from("direct:apply_privacy").process(privacy);
        	}
        };
        
    }
}