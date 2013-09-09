package eu.eexcess.insa.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.NameValuePair;
import org.apache.commons.codec.binary.Base64;

import eu.eexcess.insa.commons.Strings;

public class OAuthSigningProcessor implements Processor {
	public String calcBaseString( Exchange exchange ) {
		Message in = exchange.getIn();
		

		List<String> urlQueryParameters = getHTTPQueryParameters(in.getHeader(Exchange.HTTP_QUERY,String.class));
		
		for(String paramKey: OauthConstants.parameters) {
			if(exchange.getProperty(paramKey,String.class) != null){
				StringBuffer s = new StringBuffer();
				s.append(Strings.encodeURL(paramKey));
				s.append("=");
				s.append(Strings.encodeURL(exchange.getProperty(paramKey,String.class)));
				urlQueryParameters.add(s.toString());
			}
		}	
		java.util.Collections.sort(urlQueryParameters);
			
		StringBuffer parametersString = new StringBuffer();
		Iterator<String> iter = urlQueryParameters.iterator(); 
		
		while(iter.hasNext()){
			if( parametersString.length() !=0 ){
				parametersString.append("&");		
			}
			parametersString.append(iter.next().replace("+", "%20"));
		}
		
		StringBuffer baseString = new StringBuffer();
		baseString.append(in.getHeader(Exchange.HTTP_METHOD));
		baseString.append('&');
		
		baseString.append(Strings.encodeURL(in.getHeader(Exchange.HTTP_BASE_URI, String.class)));
		baseString.append(Strings.encodeURL(in.getHeader(Exchange.HTTP_PATH, String.class)));
		baseString.append('&');
	
		baseString.append(Strings.encodeURL(parametersString.toString()));
		String res =  baseString.toString();
		//exchange.setProperty("oauth_baseString",res);
		return res;
		
	}
	
	public void process(Exchange exchange) throws Exception {
		
		String baseString = calcBaseString( exchange );
		
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(Strings.encodeURL(exchange.getProperty("oauth_consumer_secret",String.class)));
		sBuffer.append("&");
		if(exchange.getProperty("oauth_token_secret",String.class) != null ){
			sBuffer.append(Strings.encodeURL(exchange.getProperty("oauth_token_secret",String.class)));
		}
		
		
		String key = sBuffer.toString();
		 // Get an hmac_sha1 key from the raw key bytes
        byte[] keyBytes = key.getBytes();           
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

        // Get an hmac_sha1 Mac instance and initialize with the signing key
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);

        // Compute the hmac on input data bytes
        byte[] rawHmac = mac.doFinal(baseString.toString().getBytes());

        // Convert raw bytes to Hex
       // byte[] b64Bytes = Base64.encodeBase64(rawHmac);

        //  Covert array of Hex bytes to a String		
		String signature = new String(Base64.encodeBase64String(rawHmac));
		exchange.setProperty("oauth_signature",signature);
		

	}
	
	public List<String> getHTTPQueryParameters(String queryComponent){
		List<NameValuePair> valPair = URLEncodedUtils.parse(queryComponent,Charset.forName("UTF-8"));
		List<String> encodedValPair = new ArrayList<String>();
		
		for(NameValuePair vp: valPair) {
			StringBuffer s = new StringBuffer();
			s.append(Strings.encodeURL(vp.getName()));
			s.append("=");
			if(vp.getValue()!=null){
				s.append(Strings.encodeURL(vp.getValue()));
			}
			encodedValPair.add(s.toString());
		}
	
		return encodedValPair;
		
	}

}
