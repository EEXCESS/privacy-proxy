package eu.eexcess.insa;

import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.core.util.StringKeyStringValueIgnoreCaseMultivaluedMap;

// TODO documentation
public class RequestForwarder {

	private enum Method { POST, GET, OPTIONS }
	
	//******************
	//** Post methods **
	//******************
	
	// With input
	
	public static Response forwardPostRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, MultivaluedMap<String, String> params, String input){
		return forwardRequest(Method.POST, serviceUrl, returnedTypeName, returnedTypeClass, params, input);
	}
	
	public static Response forwardPostRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, Map<String, String> map, String input){
		MultivaluedMap<String, String> params = new StringKeyStringValueIgnoreCaseMultivaluedMap();
		for (String key : map.keySet()){
			String value = map.get(key);
			params.putSingle(key, value);
		}
		return forwardRequest(Method.POST, serviceUrl, returnedTypeName, returnedTypeClass, params, input);
	}
	
	public static Response forwardPostRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, String input){
		return forwardRequest(Method.POST, serviceUrl, returnedTypeName, returnedTypeClass, null, input);
	}
	
	// Without input
	
	public static Response forwardPostRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, MultivaluedMap<String, String> params){
		return forwardRequest(Method.POST, serviceUrl, returnedTypeName, returnedTypeClass, params, null);
	}
	
	public static Response forwardPostRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, Map<String, String> map){
		MultivaluedMap<String, String> params = new StringKeyStringValueIgnoreCaseMultivaluedMap();
		for (String key : map.keySet()){
			String value = map.get(key);
			params.putSingle(key, value);
		}
		return forwardRequest(Method.POST, serviceUrl, returnedTypeName, returnedTypeClass, params, null);
	}
	
	//*****************
	//** Get methods **
	//*****************
	
	public static Response forwardGetRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, MultivaluedMap<String, String> params){
		return forwardRequest(Method.GET, serviceUrl, returnedTypeName, returnedTypeClass, params, null);
	}
	
	public static Response forwardGetRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, Map<String, String> map){
		MultivaluedMap<String, String> params = new StringKeyStringValueIgnoreCaseMultivaluedMap();
		for (String key : map.keySet()){
			String value = map.get(key);
			params.putSingle(key, value);
		}
		return forwardRequest(Method.GET, serviceUrl, returnedTypeName, returnedTypeClass, params, null);
	}
	
	public static Response forwardGetRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass){
		return forwardRequest(Method.GET, serviceUrl, returnedTypeName, returnedTypeClass, null, null);
	}
	
	//*****************
	//** Options methods **
	//*****************
	
	public static Response forwardOptionsRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, MultivaluedMap<String, String> params){
		return forwardRequest(Method.OPTIONS, serviceUrl, returnedTypeName, returnedTypeClass, params, null);
	}
	
	public static Response forwardOptionsRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, Map<String, String> map){
		MultivaluedMap<String, String> params = new StringKeyStringValueIgnoreCaseMultivaluedMap();
		for (String key : map.keySet()){
			String value = map.get(key);
			params.putSingle(key, value);
		}
		return forwardRequest(Method.OPTIONS, serviceUrl, returnedTypeName, returnedTypeClass, params, null);
	}
	
	public static Response forwardOptionsRequest(String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass){
		return forwardRequest(Method.OPTIONS, serviceUrl, returnedTypeName, returnedTypeClass, null, null);
	}
	
	//********************
	//** Implementation **
	//********************
	
	private static Response forwardRequest(Method method, String serviceUrl, String returnedTypeName, Class<?> returnedTypeClass, MultivaluedMap<String, String> params,String input){
		Response resp;
		Client client = Client.create();
		WebResource webResource = client.resource(serviceUrl);
		if (params != null){
			webResource = client.resource(serviceUrl).queryParams(params);
		}
		if (method.equals(Method.GET) || method.equals(Method.POST) || method.equals(Method.OPTIONS)){
			ClientResponse r = null; 
			Builder builder = webResource.accept(returnedTypeName).type(returnedTypeName);
			int status;
			if (method.equals(Method.GET)){
				r = builder.get(ClientResponse.class);
			} else if (method.equals(Method.POST)){
				if (input != null){
					r = builder.post(ClientResponse.class, input);
				} else {
					r = builder.post(ClientResponse.class);
				}
			} else {
				r = builder.options(ClientResponse.class);
			}
			status = r.getStatus();
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
