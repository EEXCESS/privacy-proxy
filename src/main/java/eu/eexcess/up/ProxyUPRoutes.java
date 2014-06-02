package eu.eexcess.up;

import org.apache.camel.builder.RouteBuilder;

public class ProxyUPRoutes extends RouteBuilder {
	private static final String BASE_API = "servlet://api/v1/";

	@Override
	public void configure() throws Exception {
		from(BASE_API + "log/rating")
			.convertBodyTo(String.class)
			.setProperty("req", body())
			.process(new ProxyLogProcessor(InteractionType.RATING));
		
		from(BASE_API + "log/rclose")
		.convertBodyTo(String.class)
		.setProperty("req", body())
		.process(new ProxyLogProcessor(InteractionType.RESULT_CLOSE));
		
		from(BASE_API + "log/rview")
		.convertBodyTo(String.class)
		.setProperty("req", body())
		.process(new ProxyLogProcessor(InteractionType.RESULT_VIEW));
		
		from(BASE_API + "log/show_hide")
		.convertBodyTo(String.class)
		.setProperty("req", body())
		.process(new ProxyLogProcessor(InteractionType.SHOW_HIDE));
		
		from(BASE_API + "disambiguate")
			.removeHeaders("CamelHttp*")
			.to("http://zaire.dimis.fim.uni-passau.de:8282/code-disambiguationproxy/disambiguation/categorysuggestion");
		
		from(BASE_API + "log/facetScape")
		.convertBodyTo(String.class)
		.to("log:facetScape?level=TRACE");
	}

}
