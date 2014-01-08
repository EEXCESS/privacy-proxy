package eu.eexcess.insa.recommend;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;


public class ProxyRecommendRoutesV1 extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		Processor prepareForwaredRecommendationRequest = new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				Message in = exchange.getIn();
			}
		};
		
		from("direct:recommendation.v1.route")
			.to("string-template://templates/stubs/request.dummy.xml?delimiterStart={&delimiterStop=}")
			.removeHeaders("CamelHttp*")
			.setHeader("Content-Type").constant("application/xml")
			.process(prepareForwaredRecommendationRequest)
			.to("log:recommender-query")
			.to("http4://digv536.joanneum.at/eexcess-partner-zbw-1.0-SNAPSHOT/partner/recommend")
			.to("log:recommender-answer")
			
			.setHeader("Content-Type").constant("application/json; charset=utf-8")
			.to("xslt://eu/eexcess/insa/xslt/eexcess.xml2json.xsl");
//			.to("string-template://templates/stubs/recommendations.dummy.json");
		;
	}
}
