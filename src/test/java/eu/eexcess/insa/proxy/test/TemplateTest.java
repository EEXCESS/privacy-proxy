package eu.eexcess.insa.proxy.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.retry.ExhaustedRetryException;

import eu.eexcess.insa.oauth.OAuthSigningProcessor;
import eu.eexcess.insa.proxy.APIService;

public class TemplateTest extends CamelTestSupport {
	//http://tools.ietf.org/html/rfc5849
	
    @Produce(uri = "string-template:templates/profile.tm")
    protected ProducerTemplate template;
	private Processor prepMessageProcessor = new Processor() {
		public void process(Exchange exchange) throws Exception {
			Message in = exchange.getIn();
			in.setHeader("profileUserName","profileUserName");
			in.setHeader("profileEmail","profileEmail");
			in.setHeader("profilePassword","profilePassword");
			in.setHeader("profileAddressPostalCode","profileAddressPostalCode");
			in.setHeader("profilePrivacyEmail","profilePrivacyEmail");
			in.setHeader("profilePrivacyGender","profilePrivacyGender");
			in.setHeader("profilePrivacyGender","profilePrivacyGender");
			in.setHeader("profilePrivacyTitle","profilePrivacyTitle");
			in.setHeader("profilePrivacyTraces","profilePrivacyTraces");
			in.setHeader("profilePrivacyGeoloc","profilePrivacyGeoloc");
			in.setHeader("profilePrivacyAge","profilePrivacyAge");
			in.setHeader("profilePrivacyAddress","profilePrivacyAddress");
			in.setHeader("profileTitle","profileTitle");
			in.setHeader("profileLastName","profileLastName");
			in.setHeader("profileFirstName","profileFirstName");
			in.setHeader("profileGender","profileGender");
			in.setHeader("profileBirthDate","profileBirthDate");
			in.setHeader("profileAddressStreet","profileAddressStreet");
			in.setHeader("profileAddressCity","profileAddressCity");
			in.setHeader("profileAddressCountry","profileAddressCountry");
			in.setHeader("profileTopics",new String[] { "{\"label\":\"topic1\",\"env\":\"nothing\"}", "{\"label\":\"topic2\",\"env\":\"home\"}" });

		}
	};

    @Test
    public void test() throws Exception {
    	String expectedStringBase = "{"+
    "\"username\": \"profileUserName\","+
    "\"email\": \"profileEmail\","+
    "\"password\": \"profilePassword\","+
    "\"privacy\": {"+
        "\"email\": \"profilePrivacyEmail\","+
        "\"gender\": \"profilePrivacyGender\","+
        "\"title\": \"profilePrivacyTitle\","+
        "\"traces\": \"profilePrivacyTraces\","+
        "\"geoloc\": \"profilePrivacyGeoloc\","+
        "\"age\": \"profilePrivacyAge\","+
        "\"address\": \"profilePrivacyAddress\""+
    "},"+
    "\"title\": \"profileTitle\","+
    "\"lastname\": \"profileLastName\","+
    "\"firstname\": \"profileFirstName\","+
    "\"gender\": \"profileGender\","+
    "\"birthdate\": \"profileBirthDate\","+
    "\"address\": {"+
        "\"street\": \"profileAddressStreet\","+
        "\"postalcode\": \"profileAddressPostalCode\","+
        "\"city\": \"profileAddressCity\","+
        "\"country\": \"profileAddressCountry\""+
    "},"+
    "\"topics\": ["+
    	 
    	  "{ \"label\": \"topic1\" , \"env\":\"nothing\"},"+
    	  "{ \"label\": \"topic2\" , \"env\":\"home\"}"+
    	
    "]"+
"}";
    	String expectedString = expectedStringBase.replaceAll("\\s+","");
    	Exchange resp = template.send(prepMessageProcessor);
		String resultRaw = resp.getOut().getBody(String.class);
		System.out.println(resultRaw);
		String result = resultRaw.replaceAll("\\s+","");
		System.out.println(result);
	    
		assertEquals(expectedString, result);
	    
    }
     
    @Override
    protected RouteBuilder createRouteBuilder() {
        return new APIService();
    }
}