package eu.eexcess.insa.commons;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Strings {

	public static String encodeURL(String data) {
		try {
			return URLEncoder.encode(data, "UTF8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			// Should never get here since we have UTF8
			return null;
		}
	}
}
