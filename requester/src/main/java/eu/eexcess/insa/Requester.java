package eu.eexcess.insa;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import eu.eexcess.Cst;

/**
 * This class defines all the services offered by the Privacy Proxy. 
 * @author Thomas Cerqueus
 * @version 2.0
 */
@Path(Cst.PATH)
public class Requester {
	
	/**
	 * Initialization of the Privacy Proxy. 
	 */
	public Requester(){
	}

	/**
	 * Service providing recommendations for a query. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @param queryStr Query of format QF1 or QF2 (both are supported). 
	 * @return A set of recommendations. The format is RF1 (respectively RF2) if the format of the query is QF1 (respectively QF2). 
	 * @see QueryEngine
	 */
	@POST
	@Path(Cst.PATH_RECOMMEND)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecommendations(@Context HttpServletResponse servletResp, 
			String query) {
		Response resp = Response.ok().build();
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return resp;
	}

	/**
	 * Default service providing recommendations for a query. 
	 * It does not do anything else than returning the header. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @return An empty response with status OK. 
	 * @see QueryEngine
	 */
	@OPTIONS
	@Path(Cst.PATH_RECOMMEND)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecommendations(@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp) {
		Response resp = Response.ok().build();
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		servletResp.setHeader(Cst.ACA_METHODS_KEY, Cst.ACA_POST);
		return resp;
	}
	
	/**
	 * Service providing detailed information of a set of resources. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @param detailsStr
	 * @return A set of detailed results (aka document badges). 
	 */
	@POST
	@Path(Cst.PATH_GET_DETAILS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDetails(@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp, 
			String detailsStr) {
		Response resp = Response.ok().build();
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return resp;
	}
	
	/**
	 * Default service providing detailed information of a set of resources. 
	 * It does not do anything else than returning the header. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @return An empty response with status OK. 
	 */
	@OPTIONS
	@Path(Cst.PATH_GET_DETAILS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDetails(@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp) {
		Response resp = Response.ok().build();
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		servletResp.setHeader(Cst.ACA_METHODS_KEY, Cst.ACA_POST);
		return resp;
	}

	/**
	 * XXX Not sure this service is used. 
	 * Service logging interactions. 
	 * @param servletResp HTTP response. 
	 * @param input XXX Not sure this parameter is used. 
	 * @return TODO
	 */
	@POST
	@Path(Cst.PATH_LOG)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response log(@Context HttpServletResponse servletResp, @Context UriInfo uriInfo, String input) {
		
		Response resp = forwardRequest(Cst.SERVICE_LOG, uriInfo.getPathParameters());
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return resp;
	}
	
	/**
	 * Default service logging interactions. 
	 * It does not do anything else than returning the header. 
	 * @param servletResp HTTP response. 
	 * @return An empty response with status OK. 
	 */
	@OPTIONS
	@Path(Cst.PATH_LOG)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response log(@Context HttpServletResponse servletResp) {
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		servletResp.setHeader(Cst.ACA_METHODS_KEY, Cst.ACA_POST);
		return Response.ok().build();
	}
	
	/**
	 * Service providing access to the co-occurrence graph. 
	 * The co-occurrence graph is supposed to be up-to-date at any time (no caching).   
	 * @param servletResp HTTP response. 
	 * @return A co-occurrence graph. 
	 * @see eu.eexcess.insa.peas.CacheReader
	 */
	@GET
	@Path(Cst.PATH_GET_CO_OCCURRENCE_GRAPH)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCoOccurrenceGraph(@Context HttpServletResponse servletResp, @Context UriInfo uriInfo) {
		Response resp = forwardRequest(Cst.SERVICE_GET_CO_OCCURRENCE_GRAPH, MediaType.APPLICATION_JSON, String.class, uriInfo.getPathParameters());
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		return resp;
	}
	
	/**
	 * Services providing the set of cliques contained in the co-occurrence graph. 
	 * Each clique is a graph. 
	 * As the computation of cliques is very time-consuming, a cached version is returned. 
	 * The frequency at which the cache is updated is not fixed (yet): every day, every hour, etc.  
	 * @param servletResp HTTP response. 
	 * @return A set of cliques. 
	 * @see eu.eexcess.insa.peas.Clique
	 */
	@GET
	@Path(Cst.PATH_GET_CLIQUES)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMaximalCliques(@Context HttpServletResponse servletResp, @Context UriInfo uriInfo) {
		Response resp = forwardRequest(Cst.SERVICE_GET_MAXIMAL_CLIQUES, MediaType.APPLICATION_JSON, String.class, uriInfo.getPathParameters());
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return resp;
	}
	
	/**
	 * Service returning the list of registered partners. 
	 * This service only forwards the query to the federated recommender, 
	 * and returns the result. 
	 * @param servletResp HTTP response. 
	 * @return The list of partners registered on the federated recommender. 
	 */
	@GET
	@Path(Cst.PATH_GET_REGISTERED_PARTNERS)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegisteredPartners(@Context HttpServletResponse servletResp, @Context UriInfo uriInfo) {
		Response resp = forwardRequest(Cst.SERVICE_GET_REGISTERED_PARTNERS, MediaType.APPLICATION_JSON, String.class, uriInfo.getPathParameters());
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return resp;
	}
	
	/**
	 * Service providing the favicon of a given partner. 
	 * @param servletResp HTTP response. 
	 * @param partnerId Identifier of a partner. 
	 * @return An image (image/png). 
	 */
	@GET
	@Path(Cst.PATH_GET_PARTNER_FAVICON)
	@Produces(Cst.MEDIA_TYPE_IMAGE)
	public Response getPartnerFavIcon(@Context HttpServletResponse servletResp, @Context UriInfo uriInfo) {
		Response resp = forwardRequest(Cst.SERVICE_GET_PARTNER_FAVICON, Cst.MEDIA_TYPE_IMAGE, InputStream.class, uriInfo.getQueryParameters());
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return resp;
	}

	/**
	 * Service providing a default image if a media is missing. 
	 * @param servletResp HTTP response. 
	 * @param uriInfo Request URI. 
	 * @return An image (image/png). 
	 */
	@GET
	@Path(Cst.PATH_GET_PREVIEW_IMAGE)
	@Produces(Cst.MEDIA_TYPE_IMAGE)
	public Response getPreviewImage(@Context HttpServletResponse servletResp, @Context UriInfo uriInfo) {
		Response resp = forwardRequest(Cst.SERVICE_GET_PREVIEW_IMAGE, Cst.MEDIA_TYPE_IMAGE, InputStream.class, uriInfo.getQueryParameters());
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return resp;
	}
	
	protected Response forwardRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, MultivaluedMap<String, String> params){
		Response resp;
		Client client = Client.create();
		WebResource webResource = client.resource(serviceUrl);
		if (params != null){
			webResource = client.resource(serviceUrl).queryParams(params);
		}
		ClientResponse r = webResource.accept(returnedTypeName).type(returnedTypeName).get(ClientResponse.class);
		int status = r.getStatus();
		if (status == Response.Status.OK.getStatusCode()){
			Object output = r.getEntity(returnedTypeClass);
			resp = Response.ok().entity(output).build();
		} else {
			resp = Response.status(status).build();
		}
		return resp;
	}
	
	protected Response forwardRequest(String serviceUrl, MultivaluedMap<String, String> params){
		Response resp;
		Client client = Client.create();
		WebResource webResource = client.resource(serviceUrl);
		if (params != null){
			webResource = client.resource(serviceUrl).queryParams(params);
		}
		ClientResponse r = webResource.get(ClientResponse.class);
		int status = r.getStatus();
		resp = Response.status(status).build();
		return resp;
	}
}