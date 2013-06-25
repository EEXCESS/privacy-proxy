package eu.eexcess.insa.proxy.actions;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.http4.HttpProducer;

public class PrepareResponse implements Processor {

	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String reponse = in.getBody(String.class);
		char c = reponse.substring(reponse.indexOf("\"hits\":{\"total\":")+16).charAt(0);
		String takenID = "{\"takenID\": \""+c+"\"}";
		in.setBody(takenID);
	}

}
