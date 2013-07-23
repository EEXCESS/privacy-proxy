package eu.eexcess.insa.proxy.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import org.junit.Test;

import eu.eexcess.insa.proxy.actions.PrepareRecommendationTermsPonderation;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class RecommendationTermsPonderationTest {
	PrepareRecommendationTermsPonderation ponderator = new PrepareRecommendationTermsPonderation();
	JsonNode testNode;
	

	public RecommendationTermsPonderationTest() {
		

		testNode = null;
		initTestData(); // test node initialisation
	}
	
	
	

	private void initTestData()
	{
		File testData= new File ("/home/gaetan/git/privacy-proxy/src/main/java/eu/eexcess/insa/proxy/test/traces.json") ;
		if ( testData.exists()){
			JsonFactory factory = new JsonFactory();
			FileInputStream reader;
			
		    JsonParser jp;
			try {
				reader = new FileInputStream(testData);
				jp = factory.createJsonParser(reader);
				ObjectMapper mapper = new ObjectMapper();
				testNode = mapper.readValue(jp, JsonNode.class);
				
			} catch (JsonParseException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}

		}
		else{
			System.out.println("Error : test datafile unavailable");
		}
	}
  
	
	
	
	@Test
	public void testObselWeight() {
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(2013,6,2,10,0,0);  // the date on which you want to base the test
		
		Date dateTest = calendar.getTime();
		
		Double[] expecteds = new Double[] {0.631,  0.112 , 0.0 , 0.064 , 0.0, 0.001, 0.028, 0.001, 0.064, 0.0}; // TODO
		
		List<Integer> resultList = ponderator.obselWeight(testNode,dateTest);
		
		Double[] actuals = new Double[resultList.size()];
		
		resultList.toArray(actuals);
		System.out.println(resultList);
		assertArrayEquals(expecteds, actuals);
		
	}

	
	@Test
	public void testTokenization() {
		JsonNode hitsJson = testNode.path("hits").path("hits");
		String[][] expected = {
				new String[] {"camel","testing","maven","Recherche","Google"},
				new String[] {"Apache", "Camel", "Testing"},
				new String[] {"camel", "testing", "spring", "Recherche", "Google"},
				new String[] {"Loose", "Bits", "Browserless", "AJAX", "Testing", "with","Rhino", "and", "Envjs", "Part"},
				new String[] {"rhino", "jquery", "ajax",  "Recherche", "Google"},
				new String[] {"Twitter"},
				new String[] {"Java", "based", "JavaScript", "unit", "testing", "with", "Rhino", "Juggling", "Bits"},
				new String[] {"junit", "javasctript", "rhino",  "Recherche", "Google"},
				new String[] {"Twitter"},
				new String[] {"Eclipse", "IDE", "for", "JavaScript", "Web", "Developers", "Eclipse", "Packages"},
		};
		int count = 0;
		Iterator<JsonNode> it = hitsJson.getElements();
		while(it.hasNext()) {
			JsonNode hitJson = it.next();
			String titleContent = hitJson.path("_source").path("document").path("title").asText();
			
			List<String> actuals =  ponderator.tokenize(titleContent);
			String[] actualsArr = new String[actuals.size()];
			actuals.toArray(actualsArr);
			
			assertArrayEquals(expected[count], actualsArr);
			count++;
		}
	}
}
