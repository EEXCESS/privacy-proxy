package eu.eexcess.insa;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import eu.eexcess.Cst;

public class TestPeasServices {
	
	@Test
	public void getCoOccurrenceGraph(){
		Response response = RequestForwarder.forwardGetRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_CO_OCCURRENCE_GRAPH, MediaType.APPLICATION_JSON, String.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void getMaximalCliques(){
		Response response = RequestForwarder.forwardGetRequest(Cst.PRIVACY_PROXY_URL + Cst.PATH_GET_CLIQUES, MediaType.APPLICATION_JSON, String.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
}
