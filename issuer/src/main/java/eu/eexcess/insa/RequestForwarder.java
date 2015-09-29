package eu.eexcess.insa;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

// TODO documentation
public class RequestForwarder {

	private enum Method { POST, GET }
	
	public static Response forwardPostRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, MultivaluedMap<String, String> params){
		return forwardRequest(Method.POST, serviceUrl, returnedTypeName, returnedTypeClass, params, null);
	}
	
	public static Response forwardPostRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, MultivaluedMap<String, String> params, String input){
		return forwardRequest(Method.POST, serviceUrl, returnedTypeName, returnedTypeClass, params, input);
	}
	
	public static Response forwardGetRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, MultivaluedMap<String, String> params){
		return forwardRequest(Method.GET, serviceUrl, returnedTypeName, returnedTypeClass, params, null);
	}
	
	private static Response forwardRequest(Method method, String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, MultivaluedMap<String, String> params, String input){
		Response resp;
		Client client = Client.create();
		WebResource webResource = client.resource(serviceUrl);
		if (params != null){
			webResource = client.resource(serviceUrl).queryParams(params);
		}
		if (method.equals(Method.GET) || method.equals(Method.POST)){
			ClientResponse r; 
			int status;
			if (method.equals(Method.GET)){
				r = webResource.accept(returnedTypeName).type(returnedTypeName).get(ClientResponse.class); // XXX check it's correct
				status = r.getStatus();
			} else {
				if (input != null){
					r = webResource.accept(returnedTypeName).type(returnedTypeName).post(ClientResponse.class, input);
				} else {
					r = webResource.accept(returnedTypeName).type(returnedTypeName).post(ClientResponse.class);
				}
				status = r.getStatus();
			}
			if (status == Response.Status.OK.getStatusCode()){
				Object output = r.getEntity(returnedTypeClass);
				resp = Response.ok().entity(output).build();
			} else {
				resp = Response.status(status).build();
			}
		} else {
			resp = Response.status(Status.BAD_REQUEST).build();
		}
		return resp;
	}
	
}
