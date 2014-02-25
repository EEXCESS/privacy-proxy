package eu.eexcess.insa.profile;

import static org.junit.Assert.*;

import org.codehaus.jackson.JsonParseException;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.stream.XMLStreamException;

public class UserProfileJSON2XMLTest {

	@Test
	public void test() {
		/*
		StringWriter writer = new StringWriter();
		InputStream is = ClassLoader.getSystemResourceAsStream("templates/stubs/request.dummy.json");
		try {
			UserProfileJSON2XML.convertProfile(is, writer);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(writer.toString());
		*/
	}

}
