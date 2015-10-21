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
	
	private Util resrcManager = new Util();
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
		JSONObject input = resrcManager.getJsonContent(MODULE_OPENED_CLOSED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_MODULE_OPENED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logModuleOpenedOriginMissing(){
		JSONObject input = resrcManager.getJsonContent(MODULE_OPENED_CLOSED_INPUT);
		input = Util.removeOrigin(input);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_MODULE_OPENED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logModuleOpenedOriginAttributeMissing(){
		Response response;
		String url = loggingUrl + Cst.INTERACTION_MODULE_OPENED;
		JSONObject input = resrcManager.getJsonContent(MODULE_OPENED_CLOSED_INPUT);
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
	public void logModuleClosed(){
		JSONObject input = resrcManager.getJsonContent(MODULE_OPENED_CLOSED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_MODULE_CLOSED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logModuleClosedOriginMissing(){
		JSONObject input = resrcManager.getJsonContent(MODULE_OPENED_CLOSED_INPUT);
		input = Util.removeOrigin(input);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_MODULE_CLOSED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logModuleClosedOriginAttributeMissing(){
		Response response;
		String url = loggingUrl + Cst.INTERACTION_MODULE_CLOSED;
		JSONObject input = resrcManager.getJsonContent(MODULE_OPENED_CLOSED_INPUT);
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
	
	// Module statistics
	@Test
	public void logModuleStatisticsCollected(){
		JSONObject input = resrcManager.getJsonContent(MODULE_STATISTICS_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_MODULE_STATISTICS_COLLECTED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logModuleStatisticsCollectedOriginMissing(){
		JSONObject input = resrcManager.getJsonContent(MODULE_STATISTICS_INPUT);
		input = Util.removeOrigin(input);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_MODULE_STATISTICS_COLLECTED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logModuleStatisticsCollectedOriginAttributeMissing(){
		Response response;
		String url = loggingUrl + Cst.INTERACTION_MODULE_STATISTICS_COLLECTED;
		JSONObject input = resrcManager.getJsonContent(MODULE_STATISTICS_INPUT);
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
	
	// Item opened / closed
	@Test
	public void logItemOpened(){
		JSONObject input = resrcManager.getJsonContent(ITEM_OPENED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_OPENED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemOpenedOriginMissing(){
		JSONObject input = resrcManager.getJsonContent(ITEM_OPENED_INPUT);
		input = Util.removeOrigin(input);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_OPENED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemOpenedOriginAttributeMissing(){
		Response response;
		String url = loggingUrl + Cst.INTERACTION_ITEM_OPENED;
		JSONObject input = resrcManager.getJsonContent(ITEM_OPENED_INPUT);
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
	public void logItemClosed(){
		JSONObject input = resrcManager.getJsonContent(ITEM_CLOSED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CLOSED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemClosedOriginMissing(){
		JSONObject input = resrcManager.getJsonContent(ITEM_CLOSED_INPUT);
		input = Util.removeOrigin(input);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CLOSED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemClosedOriginAttributeMissing(){
		Response response;
		String url = loggingUrl + Cst.INTERACTION_ITEM_CLOSED;
		JSONObject input = resrcManager.getJsonContent(ITEM_CLOSED_INPUT);
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
	
	// Item cited
	
	@Test
	public void logItemCitedAsHyperlink(){
		JSONObject input = resrcManager.getJsonContent(ITEM_CITED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_HYPERLINK, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemCitedAsHyperlinkOriginMissing(){
		JSONObject input = resrcManager.getJsonContent(ITEM_CITED_INPUT);
		input = Util.removeOrigin(input);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_HYPERLINK, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemCitedAsHyperlinkOriginAttributeMissing(){
		Response response;
		String url = loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_HYPERLINK;
		JSONObject input = resrcManager.getJsonContent(MODULE_OPENED_CLOSED_INPUT);
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
	public void logItemCitedAsImage(){
		JSONObject input = resrcManager.getJsonContent(ITEM_CITED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_IMAGE, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemCitedAsImageOriginMissing(){
		JSONObject input = resrcManager.getJsonContent(ITEM_CITED_INPUT);
		input = Util.removeOrigin(input);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_IMAGE, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemCitedAsImageOriginAttributeMissing(){
		Response response;
		String url = loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_IMAGE;
		JSONObject input = resrcManager.getJsonContent(ITEM_CITED_INPUT);
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
	public void logItemCitedAsText(){
		JSONObject input = resrcManager.getJsonContent(ITEM_CITED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_TEXT, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemCitedAsTextOriginMissing(){
		JSONObject input = resrcManager.getJsonContent(ITEM_CITED_INPUT);
		input = Util.removeOrigin(input);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_TEXT, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemCitedAsTextOriginAttributeMissing(){
		Response response;
		String url = loggingUrl + Cst.INTERACTION_ITEM_CITED_AS_TEXT;
		JSONObject input = resrcManager.getJsonContent(ITEM_CITED_INPUT);
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
	
	// Item rated / bookmarked
	
	@Test
	public void logItemRated(){
		JSONObject input = resrcManager.getJsonContent(ITEM_RATED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_RATED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemRatedOriginMissing(){
		JSONObject input = resrcManager.getJsonContent(ITEM_RATED_INPUT);
		input = Util.removeOrigin(input);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_RATED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemRatedOriginAttributeMissing(){
		Response response;
		String url = loggingUrl + Cst.INTERACTION_ITEM_RATED;
		JSONObject input = resrcManager.getJsonContent(ITEM_RATED_INPUT);
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
	public void logItemBookmarked(){
		JSONObject input = resrcManager.getJsonContent(ITEM_BOOKMARKED_INPUT);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_BOOKMARKED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemBookmarkedOriginMissing(){
		JSONObject input = resrcManager.getJsonContent(ITEM_BOOKMARKED_INPUT);
		input = Util.removeOrigin(input);
		Response response = RequestForwarder.forwardPostRequest(loggingUrl + Cst.INTERACTION_ITEM_BOOKMARKED, MediaType.APPLICATION_JSON, String.class, input.toString());
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logItemBookmarkedOriginAttributeMissing(){
		Response response;
		String url = loggingUrl + Cst.INTERACTION_ITEM_BOOKMARKED;
		JSONObject input = resrcManager.getJsonContent(ITEM_BOOKMARKED_INPUT);
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
	
	// Options calls
	
	@Test
	public void logOptions(){
		Response response = RequestForwarder.forwardOptionsRequest(loggingUrl  + Cst.INTERACTION_ITEM_BOOKMARKED, MediaType.APPLICATION_JSON, String.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
		
}
