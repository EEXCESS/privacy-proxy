package eu.eexcess.insa.profile;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
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
		
		private static void writeAttribute(XMLStreamWriter writer, String attribute, String value) throws XMLStreamException {
			if(!value.equals("")) {
				writer.writeAttribute(attribute, value);
			}
		}
		
		public static void convertProfile(String is, Writer secureProfileXML) throws JsonParseException, IOException, XMLStreamException {
			// Get and parse the JSON
			JsonFactory factory = new JsonFactory();
			JsonParser jp = factory.createJsonParser(is);
		    ObjectMapper mapper = new ObjectMapper();
		    JsonNode rawProfileJSON = mapper.readValue(jp, JsonNode.class).path("eexcess-user-profile");
			
		    // Generate the XML
			XMLOutputFactory output = XMLOutputFactory.newInstance();
			XMLStreamWriter xmlWriter = output.createXMLStreamWriter(secureProfileXML);

			xmlWriter.writeStartDocument();
			xmlWriter.writeStartElement("eexcess-secure-user-profile");
			writeAttribute(xmlWriter, "firstName",rawProfileJSON.path("firstname").asText());
			writeAttribute(xmlWriter, "lastName",rawProfileJSON.path("lastname").asText());
			writeAttribute(xmlWriter, "birthDay",rawProfileJSON.path("birthdate").asText());
			writeAttribute(xmlWriter, "gender",rawProfileJSON.path("gender").asText());
			
			// History
			xmlWriter.writeStartElement("history");
				JsonNode history = rawProfileJSON.path("history");
				for (int i = 0; i < history.size(); i++) {
					JsonNode historyItem = history.get(i);
					xmlWriter.writeStartElement("visit");
					xmlWriter.writeAttribute("lastVisitTime", historyItem.path("lastVisitTime").asText());
					xmlWriter.writeAttribute("title", historyItem.path("title").asText());
					xmlWriter.writeCharacters(historyItem.path("url").asText());
					xmlWriter.writeEndElement();
				}
			xmlWriter.writeEndElement();
			
			// Interests
			{
				xmlWriter.writeStartElement("interests");
				JsonNode interests = rawProfileJSON.path("interests").path("interest");
				for(int i= 0; i<interests.size(); i++) {
					JsonNode it = interests.get(i);
					xmlWriter.writeStartElement("interest");
					String weight = it.path("weight").asText();
					String content = it.path("text").asText();
					String uri = it.path("uri").asText();
					if(!uri.equals("")) {
						xmlWriter.writeAttribute("uri", uri);
					}
					xmlWriter.writeAttribute("weight", weight);
					xmlWriter.writeCharacters(content);
					xmlWriter.writeEndElement();					
				}
				xmlWriter.writeEndElement();
			}
			
			// Context-list
			{
				xmlWriter.writeStartElement("context-list");
				JsonNode interests = rawProfileJSON.path("context-list").path("context");
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
