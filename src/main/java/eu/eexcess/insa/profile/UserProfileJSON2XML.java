package eu.eexcess.insa.profile;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.http4.HttpMessage;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class UserProfileJSON2XML implements Processor {
		public void process(Exchange exchange) throws Exception {
			Message in = exchange.getIn();	
			String is = in.getBody(String.class);
			StringWriter secureProfileXML = new StringWriter();
			convertProfile(is, secureProfileXML);
			in.setBody(secureProfileXML.toString());
		}
		
		public static void convertProfile(String is, Writer secureProfileXML) throws JsonParseException, IOException, XMLStreamException {
			// Get and parse the JSON
			JsonFactory factory = new JsonFactory();
			JsonParser jp = factory.createJsonParser(is);
		    ObjectMapper mapper = new ObjectMapper();
		    JsonNode rawProfileJSON = mapper.readValue(jp, JsonNode.class);
			
		    // Generate the XML
			XMLOutputFactory output = XMLOutputFactory.newInstance();
			XMLStreamWriter xmlWriter = output.createXMLStreamWriter(secureProfileXML);

			xmlWriter.writeStartDocument();
			xmlWriter.writeStartElement("eexcess-secure-user-profile");
			xmlWriter.writeAttribute("firstName","dummy");
			xmlWriter.writeAttribute("lastName","dummy");
			xmlWriter.writeAttribute("birthDay","2013-10-14T05:06:44.550+02:00");
			
			// Interests
			{
				xmlWriter.writeStartElement("interests");
				JsonNode interests = rawProfileJSON.path("eexcess-user-profile").path("interests").path("interest");
				for(int i= 0; i<interests.size(); i++) {
					JsonNode it = interests.get(i);
					xmlWriter.writeStartElement("interest");
					String weight = it.path("weight").asText();
					String content = it.path("text").asText();
					xmlWriter.writeAttribute("weight", weight);
					xmlWriter.writeCharacters(content);
					xmlWriter.writeEndElement();					
				}
				xmlWriter.writeEndElement();
			}
			
			// Context-list
			{
				xmlWriter.writeStartElement("context-list");
				JsonNode interests = rawProfileJSON.path("eexcess-user-profile").path("context-list").path("context");
				for(int i= 0; i<interests.size(); i++) {
					JsonNode it = interests.get(i);
					xmlWriter.writeStartElement("context");
					String weight = it.path("weight").asText();
					String content = it.path("text").asText();
					xmlWriter.writeAttribute("weight", weight);
					xmlWriter.writeCharacters(content);
					xmlWriter.writeEndElement();					
				}
				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();
			xmlWriter.flush();
		}
}
