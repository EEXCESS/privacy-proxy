package eu.eexcess.insa.recommend;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import eu.eexcess.insa.profile.UserProfileJSON2XML;


public class ProxyRecommendRoutesV1 extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		// Map a JSON EEXCESS User Profile to an XML EEXCESS Secure User Profile
		Processor convertUserProfile = new UserProfileJSON2XML();
		
		from("direct:recommendation.v1.route")
			.removeHeaders("CamelHttp*")
			.process(convertUserProfile)
			.to("log:recommender-query")
			.setHeader("Content-Type").constant("application/xml")
//			.to("http4://digv539.joanneum.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend")
			.filter(header("fr_url"))
				.setHeader(Exchange.HTTP_URI).header("fr_url")
				.log("Requesting recommendations from: ${in.header.fr_url}")
			.end()
			.to("http4://eexcess.joanneum.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend")
			// .to("http4://digv536.joanneum.at/eexcess-partner-zbw-1.0-SNAPSHOT/partner/recommend")
			.convertBodyTo(String.class)
			.to("log:recommender-answer")
			
			.setHeader("Content-Type").constant("application/json; charset=utf-8")
			.to("xslt://eu/eexcess/insa/xslt/eexcess.xml2json.xsl");
//			.to("string-template://templates/stubs/recommendations.dummy.json");
		;
	}
}
