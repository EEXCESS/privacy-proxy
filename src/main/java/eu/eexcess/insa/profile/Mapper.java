package eu.eexcess.insa.profile;

import org.apache.camel.Message;

public class Mapper {
	public static void fillHeader(Message in, String headerName, String value) {
		if(
				in.getHeader(headerName,String.class) != null &&
				!"".equals(in.getHeader(headerName,String.class))
		) {
			if(value != null && !"".equals(value)) {
				in.setHeader(headerName, value);
			}
		}
	}
}
