package eu.eexcess.insa.proxy.policy;

import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class XACMLEnforcmentProcessor implements Processor {

	
	/**
	 * Transforms the input profile (given as message body) respects the user's policy (given as header)
	 * and outputs a "policy respectful" version of the profile
	 * 
	 *
	 * IN BODY: Complete JSON user profile
	 * IN HEADER "XACMLPolicy": User's XACML policy
	 * 
	 * 
	 */
	@Override
	public void process(Exchange ex) throws Exception {
		Message in = ex.getIn();
		InputStream jsonProfileStream = in.getBody(InputStream.class);
		InputStream xacmlPolicy		  = in.getHeader("XACMLPolicy", InputStream.class);

		String policyRespectfulProfile = in.getHeader("expected", String.class);
		// TODO Generate policyRespectful profile
				
		in.setBody(policyRespectfulProfile);
	}

}
