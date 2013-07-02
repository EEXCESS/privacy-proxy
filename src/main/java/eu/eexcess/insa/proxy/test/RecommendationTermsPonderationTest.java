package eu.eexcess.insa.proxy.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import eu.eexcess.insa.proxy.actions.PrepareRecommendationTermsPonderation;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class RecommendationTermsPonderationTest {
	PrepareRecommendationTermsPonderation ponderator = new PrepareRecommendationTermsPonderation();
	JsonNode testNode;
	
	public RecommendationTermsPonderationTest() {
		

		testNode = null; // TODO
	}
	
	@Test
	public void testObselWeight() {
		Double[] expecteds = new Double[] {2.3, 3.0,5.0, 6.0}; // TODO
		
		List<Double> resultList = ponderator.obselWeight(testNode);
		
		Double[] actuals = new Double[resultList.size()];
		resultList.toArray(actuals);
		assertArrayEquals(expecteds, actuals);
		
	}

	
	@Test
	public void testTokenization() {
		JsonNode hitsJson = testNode.path("hits").path("hits");
		String[][] expected = {
				new String[] {"camel","testing","maven","Recherche","Google"},
				new String[] {"Apache", "Camel", "Testing"},
				new String[] {"camel", "testing", "spring", "Recherche", "Google"},
				new String[] {"Loose", "Bits", "Browserless", "AJAX", "Testing", "with","Rhino", "and", "Envjs", "Part", "2"},
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
