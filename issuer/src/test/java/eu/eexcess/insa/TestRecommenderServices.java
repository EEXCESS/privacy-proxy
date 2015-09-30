package eu.eexcess.insa;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import eu.eexcess.Cst;

public class TestRecommenderServices {

	private Util resrcManager = new Util(); 

	private String QF1_INPUT = "query-QF1.json";
	private String QF2_INPUT = "query-QF2.json";
	private String QF3_INPUT = "query-QF3.json";
	private String[] PREVIEW_IMAGE_TYPES = {"other", "unknown", "text", "audio", "3d", "image", "video"};
	
	// recommend QF1 
	@Test
	public void recommendQf1(){
		JSONObject input = resrcManager.getJsonContent(QF1_INPUT);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOMMEND, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void recommendQf1OriginMissing(){
		JSONObject input = resrcManager.getJsonContent(QF1_INPUT);
		input = Util.removeOrigin(input);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOMMEND, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void recommendQf1OriginAttributeMissing(){
		Response response;
		String url = Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOMMEND;
		JSONObject input = resrcManager.getJsonContent(QF1_INPUT);
		JSONObject input1 = Util.removeOriginUserId(input);
		response = RequestForwarder.forwardPostRequest(url, MediaType.APPLICATION_JSON, String.class, input1.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		JSONObject input2 = Util.removeOriginClientType(input);
		response = RequestForwarder.forwardPostRequest(url, MediaType.APPLICATION_JSON, String.class, input2.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		JSONObject input3 = Util.removeOriginClientVersion(input);
		response = RequestForwarder.forwardPostRequest(url, MediaType.APPLICATION_JSON, String.class, input3.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		JSONObject input4 = Util.removeOriginModule(input);
		response = RequestForwarder.forwardPostRequest(url, MediaType.APPLICATION_JSON, String.class, input4.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	// recommend QF2 
	@Test
	public void recommendQf2(){
		JSONObject input = resrcManager.getJsonContent(QF2_INPUT);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOMMEND, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void recommendQf2OriginMissing(){
		JSONObject input = resrcManager.getJsonContent(QF2_INPUT);
		input = Util.removeOrigin(input);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOMMEND, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void recommendQf2OriginAttributeMissing(){
		Response response;
		String url = Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOMMEND;
		JSONObject input = resrcManager.getJsonContent(QF2_INPUT);
		JSONObject input1 = Util.removeOriginUserId(input);
		response = RequestForwarder.forwardPostRequest(url, MediaType.APPLICATION_JSON, String.class, input1.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		JSONObject input2 = Util.removeOriginClientType(input);
		response = RequestForwarder.forwardPostRequest(url, MediaType.APPLICATION_JSON, String.class, input2.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		JSONObject input3 = Util.removeOriginClientVersion(input);
		response = RequestForwarder.forwardPostRequest(url, MediaType.APPLICATION_JSON, String.class, input3.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		JSONObject input4 = Util.removeOriginModule(input);
		response = RequestForwarder.forwardPostRequest(url, MediaType.APPLICATION_JSON, String.class, input4.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	// getDetails 
	@Test
	public void getDetails(){
		JSONObject input = resrcManager.getJsonContent(QF3_INPUT);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_DETAILS, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void getDetailsOriginMissing(){
		JSONObject input = resrcManager.getJsonContent(QF3_INPUT);
		input = Util.removeOrigin(input);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_DETAILS, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void getDetailsOriginAttributeMissing(){
		Response response;
		String url = Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOMMEND;
		JSONObject input = resrcManager.getJsonContent(QF3_INPUT);
		JSONObject input1 = Util.removeOriginUserId(input);
		response = RequestForwarder.forwardPostRequest(url, MediaType.APPLICATION_JSON, String.class, input1.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		JSONObject input2 = Util.removeOriginClientType(input);
		response = RequestForwarder.forwardPostRequest(url, MediaType.APPLICATION_JSON, String.class, input2.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		JSONObject input3 = Util.removeOriginClientVersion(input);
		response = RequestForwarder.forwardPostRequest(url, MediaType.APPLICATION_JSON, String.class, input3.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		JSONObject input4 = Util.removeOriginModule(input);
		response = RequestForwarder.forwardPostRequest(url, MediaType.APPLICATION_JSON, String.class, input4.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	public void getDetailsQueryIdMissing(){
		JSONObject input = resrcManager.getJsonContent(QF3_INPUT);
		input.remove(Cst.TAG_QUERY_ID);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_DETAILS, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	// getRegisteredPartners
	@Test
	public void getRegisteredPartners(){
		Response response = RequestForwarder.forwardGetRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_REGISTERED_PARTNERS, MediaType.APPLICATION_JSON, String.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

	// getPreviewImage
	@Test
	public void getPreviewImage(){
		for (int i = 0 ; i < PREVIEW_IMAGE_TYPES.length ; i++){
			getPreviewImage(PREVIEW_IMAGE_TYPES[i]);
		}
	}

	private void getPreviewImage(String type){
		Map<String, String> params = new HashMap<String, String>();
		params.put(Cst.PARAM_IMAGE_TYPE, type);
		Response response = RequestForwarder.forwardGetRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_PREVIEW_IMAGE, Cst.MEDIA_TYPE_IMAGE, String.class, params);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

	// getPartnerFavIcon
	@Test
	public void getPartnerFavIcon(){
		Map<String, String> params; 
		Set<String> partnerIds = getRegisteredPartnerIds();
		for (String partnerId : partnerIds){
			params = new HashMap<String, String>();
			params.put(Cst.PARAM_PARTNER_ID, partnerId);
			Response response = RequestForwarder.forwardGetRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_PARTNER_FAVICON, Cst.MEDIA_TYPE_IMAGE, String.class, params);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
		}
	}

	private Set<String> getRegisteredPartnerIds(){
		Set<String> ids = new TreeSet<String>();
		Response response = RequestForwarder.forwardGetRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_REGISTERED_PARTNERS, MediaType.APPLICATION_JSON, String.class);
		String output = response.getEntity().toString();
		JSONObject jsonOutput = new JSONObject(output);
		if (jsonOutput.has(Cst.TAG_PARTNERS)){
			JSONArray partners = jsonOutput.getJSONArray(Cst.TAG_PARTNERS);
			for (int i = 0 ; i < partners.length() ; i++){
				JSONObject partner = partners.getJSONObject(i);
				if (partner.has(Cst.TAG_PARTNER_ID)){
					String partnerId = partner.getString(Cst.TAG_PARTNER_ID);
					ids.add(partnerId);
				}
			}
		}
		return ids;
	}
	
	// Options calls
	
	@Test
	public void recommendOptions(){
		Response response = RequestForwarder.forwardOptionsRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOMMEND, MediaType.APPLICATION_JSON, String.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void getDetailsOptions(){
		Response response = RequestForwarder.forwardOptionsRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_DETAILS, MediaType.APPLICATION_JSON, String.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void suggestCategoriesOptions(){
		Response response = RequestForwarder.forwardOptionsRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_SUGGEST_CATEGORIES, MediaType.APPLICATION_JSON, String.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void recognizeEntityOptions(){
		Response response = RequestForwarder.forwardOptionsRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOGNIZE_ENTITY, MediaType.APPLICATION_JSON, String.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

}
