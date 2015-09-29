package eu.eexcess.insa;

import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;
import org.junit.Test;

import eu.eexcess.Cst;

public class TestLoggingServices {
	
	private ResourceManager resrcManager = new ResourceManager();
	private String loggingUrl =  Cst.PRIVACY_PROXY_URL + Cst.PATH_LOG + File.separator;
	
	private static final String MODULE_OPENED_CLOSED_INPUT = "log-moduleOpenedClosed.json";
	private static final String MODULE_STATISTICS_INPUT = "log-moduleStatistics.json";
	private static final String ITEM_OPENED_INPUT = "log-itemOpened.json";
	private static final String ITEM_CLOSED_INPUT = "log-itemClosed.json";
	private static final String ITEM_CITED_INPUT = "log-itemCited.json";
	private static final String ITEM_RATED_INPUT = "log-itemRated.json";
	private static final String ITEM_BOOKMARKED_INPUT = "log-itemBookmarked.json";
	
	// Module opened / closed
	@Test
	public void logModuleOpened(){
		JSONObject input = resrcManager.getContent(MODULE_OPENED_CLOSED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_MODULE_OPENED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logModuleOpenedOriginMissing(){
		JSONObject input = resrcManager.getContent(MODULE_OPENED_CLOSED_INPUT);
		input.remove(Cst.TAG_ORIGIN);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_MODULE_OPENED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logModuleClosed(){
		JSONObject input = resrcManager.getContent(MODULE_OPENED_CLOSED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_MODULE_CLOSED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logModuleClosedOriginMissing(){
		JSONObject input = resrcManager.getContent(MODULE_OPENED_CLOSED_INPUT);
		input.remove(Cst.TAG_ORIGIN);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_MODULE_CLOSED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	// Module statistics
	@Test
	public void logModuleStatisticsCollected(){
		JSONObject input = resrcManager.getContent(MODULE_STATISTICS_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_MODULE_STATISTICS_COLLECTED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logModuleStatisticsCollectedOriginMissing(){
		JSONObject input = resrcManager.getContent(MODULE_STATISTICS_INPUT);
		input.remove(Cst.TAG_ORIGIN);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_MODULE_STATISTICS_COLLECTED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	// Item opened / closed
	@Test
	public void logItemOpened(){
		JSONObject input = resrcManager.getContent(ITEM_OPENED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_OPENED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemOpenedOriginMissing(){
		JSONObject input = resrcManager.getContent(ITEM_OPENED_INPUT);
		input.remove(Cst.TAG_ORIGIN);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_OPENED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemClosed(){
		JSONObject input = resrcManager.getContent(ITEM_CLOSED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CLOSED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemClosedOriginMissing(){
		JSONObject input = resrcManager.getContent(ITEM_CLOSED_INPUT);
		input.remove(Cst.TAG_ORIGIN);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CLOSED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	// Item cited
	
	@Test
	public void logItemCitedAsHyperlink(){
		JSONObject input = resrcManager.getContent(ITEM_CITED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_HYPERLINK, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemCitedAsHyperlinkOriginMissing(){
		JSONObject input = resrcManager.getContent(ITEM_CITED_INPUT);
		input.remove(Cst.TAG_ORIGIN);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_HYPERLINK, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemCitedAsImage(){
		JSONObject input = resrcManager.getContent(ITEM_CITED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_IMAGE, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemCitedAsImageOriginMissing(){
		JSONObject input = resrcManager.getContent(ITEM_CITED_INPUT);
		input.remove(Cst.TAG_ORIGIN);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_IMAGE, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemCitedAsText(){
		JSONObject input = resrcManager.getContent(ITEM_CITED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_TEXT, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemCitedAsTextOriginMissing(){
		JSONObject input = resrcManager.getContent(ITEM_CITED_INPUT);
		input.remove(Cst.TAG_ORIGIN);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_TEXT, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	// Item rated / bookmarked
	
	@Test
	public void logItemRated(){
		JSONObject input = resrcManager.getContent(ITEM_RATED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_RATED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemRatedOriginMissing(){
		JSONObject input = resrcManager.getContent(ITEM_RATED_INPUT);
		input.remove(Cst.TAG_ORIGIN);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_RATED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemBookmarked(){
		JSONObject input = resrcManager.getContent(ITEM_BOOKMARKED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_BOOKMARKED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemBookmarkedOriginMissing(){
		JSONObject input = resrcManager.getContent(ITEM_BOOKMARKED_INPUT);
		input.remove(Cst.TAG_ORIGIN);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_BOOKMARKED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
		
}
