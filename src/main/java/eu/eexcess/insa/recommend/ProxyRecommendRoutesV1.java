package eu.eexcess.insa.recommend;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import eu.eexcess.insa.profile.UserProfileJSON2XML;
import eu.eexcess.up.InteractionType;
import eu.eexcess.up.ProxyLogProcessor;


public class ProxyRecommendRoutesV1 extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		// Map a JSON EEXCESS User Profile to an XML EEXCESS Secure User Profile
		Processor convertUserProfile = new UserProfileJSON2XML();
		
		from("direct:recommendation.v1.route")
			.removeHeaders("CamelHttp*")
			.removeHeader("Accept")
			.convertBodyTo(String.class)
			.setProperty("req", body()) // keep query request along the route
			.process(new ProxyLogProcessor(InteractionType.QUERY)) // log query in interactions
			.process(convertUserProfile)
			.setHeader("Content-Type").constant("application/xml")
			.filter(header("fr_url"))
				.setHeader(Exchange.HTTP_URI).header("fr_url")
				.log("Requesting recommendations from: ${in.header.fr_url}")
			.end()
			.to("log:recommender-query?showHeaders=true")
			.to("http4://eexcess.joanneum.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend")
			.convertBodyTo(String.class)
			.to("log:recommender-answer")
			.setHeader("Content-Type").constant("application/json; charset=utf-8")
			.to("xslt://eu/eexcess/insa/xslt/eexcess.xml2json.xsl")
			.process(new ProxyLogProcessor(InteractionType.RESULT)); // log result in interactions
		;
	}
}
