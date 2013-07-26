package eu.eexcess.insa.profile;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;


import org.apache.camel.Message;

public class Mapper {
	public static void fillHeader(Message in, String headerName, String value) {
		if(
				in.getHeader(headerName,String.class) == null ||
				"".equals(in.getHeader(headerName,String.class))
		) {
			if(value != null && !"".equals(value)) {
				in.setHeader(headerName, value);
			}
		}
	
	}
	
	public static void fillHeader(Message in, String headerName, String[] values) {
		HashSet<String> valueSet = new HashSet<String>();
		String[] existingValues = in.getHeader( headerName , String[].class);
		if(existingValues != null) {
			valueSet.addAll(Arrays.asList(existingValues));
		}
		
		if(values != null) {
			valueSet.addAll(Arrays.asList(values));
		}
		
		if(valueSet.size() > 0) {
			String[] newValues = new String[valueSet.size()];
			newValues = valueSet.toArray(newValues);
			in.setHeader(headerName, newValues );	
		}	
	}
}
