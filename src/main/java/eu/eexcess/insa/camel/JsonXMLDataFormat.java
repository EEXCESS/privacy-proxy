package eu.eexcess.insa.camel;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConverter;
import org.apache.camel.spi.DataFormat;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.semsaas.jsonxml.JsonXMLReader;
import com.semsaas.jsonxml.XMLJsonGenerator;

public final class JsonXMLDataFormat implements DataFormat {

	/**
	 * Transforms some form of xjson (XMLized json data) into a json stream
	 */
    public void marshal(Exchange exchange, Object xmlSource, OutputStream jsonStream) throws Exception {
    	TypeConverter tc = exchange.getContext().getTypeConverter();
    	Source source = tc.tryConvertTo(Source.class, xmlSource);
    	
		XMLJsonGenerator handler = new XMLJsonGenerator();
		handler.setOutputStream(jsonStream);
		SAXResult result = new SAXResult(handler);
		transform(source, result);
    }

    /**
     * Transforms a json stream into an xjson DOM Document
     */
    public Object unmarshal(Exchange exchange, InputStream jsonStream) throws Exception {
    	XMLReader reader = XMLReaderFactory.createXMLReader(JsonXMLReader.class.getCanonicalName());
    	InputStreamReader stringReader = new InputStreamReader(jsonStream);
		SAXSource saxSource = new SAXSource(reader, new InputSource(stringReader));
		DOMResult domResult = new DOMResult();
		
		transform(saxSource, domResult);
        return domResult.getNode();
    }

	private static void transform(Source source, Result result) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(source, result);
	}
}
