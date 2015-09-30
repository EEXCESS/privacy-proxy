package eu.eexcess.insa;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;
import org.junit.Test;

import eu.eexcess.Cst;

public class TestExternalServices {
	
	private Util resrcManager = new Util(); 

	private String SUGGEST_CATEGORIES_INPUT = "suggestCategories.json";
	private String RECOGNIZE_ENTITY_INPUT = "recognizeEntity.json";
	
	@Test
	public void getSuggestCategories(){
		JSONObject input = resrcManager.getJsonContent(SUGGEST_CATEGORIES_INPUT);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_SUGGEST_CATEGORIES, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void getRecognizeEntity(){
		JSONObject input = resrcManager.getJsonContent(RECOGNIZE_ENTITY_INPUT);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOGNIZE_ENTITY, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
}
