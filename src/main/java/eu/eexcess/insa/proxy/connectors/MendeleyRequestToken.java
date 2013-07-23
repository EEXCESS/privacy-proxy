package eu.eexcess.insa.proxy.connectors;


import java.net.URLEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.codec.binary.Hex;




public class MendeleyRequestToken implements Processor { 

	final private String consummerKey = "e13d6dfff14e3174b84d9bca29cd6082051d51fd6";
	final private String consummerSecret = "6faa629a8339f4bf61d9f41be70c536f";


	public void process(Exchange exchange) throws Exception {
		
		String authorizationHeader = "";
		
		// consummer key
	   authorizationHeader+=URLEncoder.encode("oauth_consummer_key", "UTF8");
	   authorizationHeader+="=\"";
	   authorizationHeader+=URLEncoder.encode(consummerKey, "UTF8");
	   authorizationHeader+="\",";
	   
	   // signature method
	   authorizationHeader+=URLEncoder.encode("oauth_signature_method", "UTF8");
	   authorizationHeader+="=\"";
	   authorizationHeader+=URLEncoder.encode("HMAC-SHA1", "UTF8");
	   authorizationHeader+="\",";
	   
	   // signature
	   authorizationHeader+=URLEncoder.encode("oauth_signature", "UTF8");
	   authorizationHeader+="=\"";
	//   authorizationHeader+=URLEncoder.encode(hmacSha1(key,consummerSecret), "UTF8");
	   authorizationHeader+="\",";
	}
	
	
	public static String hmacSha1(String value, String key) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes();           
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());

            // Convert raw bytes to Hex
            byte[] hexBytes = new Hex().encode(rawHmac);

            //  Covert array of Hex bytes to a String
            return new String(hexBytes, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
