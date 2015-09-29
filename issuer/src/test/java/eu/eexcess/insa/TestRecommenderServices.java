package eu.eexcess.insa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

	private ResourceManager resrcManager = new ResourceManager(); 

	private String QF1_INPUT = "query-QF1.json";
	private String QF2_INPUT = "query-QF2.json";
	private String QF3_INPUT = "query-QF3.json";
	private String[] PREVIEW_IMAGE_TYPES = {"other", "unknown", "text", "audio", "3d", "image", "video"};


	// recommend QF1 
	@Test
	public void recommendQf1(){
		JSONObject input = resrcManager.getContent(QF1_INPUT);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOMMEND, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void recommendQf1OriginMissing(){
		JSONObject input = resrcManager.getContent(QF1_INPUT);
		input.remove(Cst.TAG_ORIGIN);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOMMEND, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	// recommend QF2 
	@Test
	public void recommendQf2(){
		JSONObject input = resrcManager.getContent(QF2_INPUT);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOMMEND, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void recommendQf2OriginMissing(){
		JSONObject input = resrcManager.getContent(QF2_INPUT);
		input.remove(Cst.TAG_ORIGIN);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_RECOMMEND, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	// getDetails 
	@Test
	public void getDetails(){
		JSONObject input = resrcManager.getContent(QF3_INPUT);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_DETAILS, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void getDetailsOriginMissing(){
		JSONObject input = resrcManager.getContent(QF3_INPUT);
		input.remove(Cst.TAG_ORIGIN);
		Response response = RequestForwarder.forwardPostRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_DETAILS, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	public void getDetailsQueryIdMissing(){
		JSONObject input = resrcManager.getContent(QF3_INPUT);
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
		Boolean success = true;
		for (int i = 0 ; i < PREVIEW_IMAGE_TYPES.length ; i++){
			success = success && getPreviewImage(PREVIEW_IMAGE_TYPES[i]);
		}
		assertTrue(success);
	}

	private Boolean getPreviewImage(String type){
		Map<String, String> params = new HashMap<String, String>();
		params.put(Cst.PARAM_IMAGE_TYPE, type);
		Response response = RequestForwarder.forwardGetRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_PREVIEW_IMAGE, Cst.MEDIA_TYPE_IMAGE, String.class, params);
		return (Status.OK.getStatusCode() == response.getStatus());
	}

	// getPartnerFavIcon
	@Test
	public void getPartnerFavIcon(){
		Boolean success = true;
		Map<String, String> params; 
		Set<String> partnerIds = getRegisteredPartnerIds();
		for (String partnerId : partnerIds){
			params = new HashMap<String, String>();
			params.put(Cst.PARAM_PARTNER_ID, partnerId);
			Response response = RequestForwarder.forwardGetRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_PARTNER_FAVICON, Cst.MEDIA_TYPE_IMAGE, String.class, params);
			success = success && (Status.OK.getStatusCode() == response.getStatus());
		}
		assertTrue(success);
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

}
