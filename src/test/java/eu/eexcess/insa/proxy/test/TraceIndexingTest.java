package eu.eexcess.insa.proxy.test;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import eu.eexcess.insa.proxy.APIService;
import eu.eexcess.insa.proxy.actions.ApplyPrivacySettingsJS;
import eu.eexcess.insa.recommend.APIRecommendation;


public class TraceIndexingTest extends CamelTestSupport {
JsonFactory factory = new JsonFactory();
ObjectMapper mapper = new ObjectMapper();
HashMap<String,String> simulatedElasticSearchResponses = new HashMap<String,String>();
	
	/*
	 * This tests the traces retrievement functionnality
	 */
	@Test
	public void traceIndexingTest () throws IOException, InterruptedException{
		template.sendBody("direct:reinitialize-index","");
		Thread.sleep(100);
		Date begin = new Date();
		Date end = new Date();
		end.setTime(begin.getTime()+15000);
		String endEvent = "unload";
		String beginEvent = "load";
		
		HashMap<Integer,HashMap<String,String>> traces = new HashMap<Integer,HashMap<String,String>>();
		HashMap<String,String> tr1 = new HashMap<String,String>();
		tr1.put("url","http://www.website1.org/");
		tr1.put("title", "Website about programming");
		traces.put(1, tr1);
		String trace1 = createTrace(tr1.get("url"),tr1.get("title"),"home", begin, end, endEvent, beginEvent);
		template.sendBody("direct:index.trace",trace1);
		
		
		HashMap<String,String> tr2 = new HashMap<String,String>();
		tr2.put("url","http://www.website1.org/page1");
		tr2.put("title", "All you need to know about computers");
		traces.put(2, tr2);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+16000);
		String trace2 = createTrace(tr2.get("url"),tr2.get("title"),"home", begin, end, endEvent, beginEvent);
		template.sendBody("direct:index.trace",trace2);
		
		HashMap<String,String> tr3 = new HashMap<String,String>();
		tr3.put("url","http://www.website2.com/");
		tr3.put("title", "Embedded devices and you");
		traces.put(3, tr3);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+25000);
		String trace3 = createTrace(tr3.get("url"),tr3.get("title"),"home", begin, end, endEvent, beginEvent);
		template.sendBody("direct:index.trace",trace3);
		
		HashMap<String,String> tr4 = new HashMap<String,String>();
		tr4.put("url","http://www.website2.com/register");
		tr4.put("title", "Embedded devices- Registration");
		traces.put(4, tr4);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+25000);
		String trace4 = createTrace(tr4.get("url"),tr4.get("title"),"home", begin, end, endEvent, beginEvent);
		template.sendBody("direct:index.trace",trace4);
		
		HashMap<String,String> tr5 = new HashMap<String,String>();
		tr5.put("url","http://www.website2.com/thanks");
		tr5.put("title", "Embedded devices- Registration confirmed");
		traces.put(5, tr5);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+25000);
		String trace5 = createTrace(tr5.get("url"),tr5.get("title"),"home", begin, end, endEvent, beginEvent);
		template.sendBody("direct:index.trace",trace5);
		

		HashMap<String,String> tr6 = new HashMap<String,String>();
		tr6.put("url","http://www.website2.com/page3");
		tr6.put("title", "Microkernel Vs exokernel");
		traces.put(6, tr6);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+16000);
		String trace6 = createTrace(tr6.get("url"),tr6.get("title"),"home", begin, end, endEvent, beginEvent);
		template.sendBody("direct:index.trace",trace6);
		
		HashMap<String,String> tr7 = new HashMap<String,String>();
		tr7.put("url","http://www.website2.com/page4");
		tr7.put("title", "Embedded web server");
		traces.put(7, tr7);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+25000);
		String trace7 = createTrace(tr7.get("url"), tr7.get("title"),"home", begin, end, endEvent, beginEvent);
		template.sendBody("direct:index.trace",trace7);
		
		HashMap<String,String> tr8 = new HashMap<String,String>();
		tr8.put("url","http://www.website3.org/");
		tr8.put("title", "Java programming");
		traces.put(8, tr8);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+25000);
		String trace8 = createTrace(tr8.get("url"),tr8.get("title"),"home", begin, end, endEvent, beginEvent);
		template.sendBody("direct:index.trace",trace8);
		
		HashMap<String,String> tr9 = new HashMap<String,String>();
		tr9.put("url","http://www.website3.org/42");
		tr9.put("title", "Java programming - algorithm of the week");
		traces.put(9, tr9);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+25000);
		String trace9 = createTrace(tr9.get("url"), tr9.get("title"),"home", begin, end, endEvent, beginEvent);
		template.sendBody("direct:index.trace",trace9);
		
		HashMap<String,String> tr10 = new HashMap<String,String>();
		tr10.put("url","http://www.website3.org/tests");
		tr10.put("title", "Java programming - How to write better tests");
		traces.put(10, tr10);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+25000);
		String trace10 = createTrace(tr10.get("url"),tr10.get("title"),"home", begin, end, endEvent, beginEvent);
		template.sendBody("direct:index.trace",trace10);
		
		StringWriter sWriter = new StringWriter();
		JsonGenerator jg = factory.createJsonGenerator(sWriter);
		jg.writeStartObject();
		jg.writeStringField("pluginId", "TraceIndexingTest.java");
		jg.writeStringField("environnement", "home");
		jg.writeEndObject();
		jg.close();
		String traceRequest= sWriter.toString();
		Thread.sleep(3000);
		Object response = template.sendBody("direct:retrieve.user.traces",ExchangePattern.InOut,traceRequest);
		//System.out.println(response);
		String elasticResponse = (String)(response);
		JsonParser jp = factory.createJsonParser(elasticResponse);
		JsonNode responseNode = mapper.readValue(jp,JsonNode.class);
		
		assertEquals(responseNode.path("hits").path("total").asInt(),10);
		Iterator<JsonNode> it = responseNode.path("hits").path("hits").getElements();
		int cpt = 10;
		while(it.hasNext()){
			JsonNode trace = it.next().path("_source"); 
			assertEquals(trace.path("document").path("title").asText(), traces.get(cpt).get("title"));
			assertEquals(trace.path("document").path("url").asText(), traces.get(cpt).get("url"));
			cpt--;
		}
	}
	
	
	
	/*
	 *  Here we test the endpoint "direct:query.enrich"
	 *   which takes as input a context trace, lookups up the target traces (which are mock in by this test)
	 *   and generates a weighted list of terms.
	 *  
	 *  This test is organized as following:
	 *   -> prepare the mock result data
	 *   -> send a test traces
	 *   -> check that the terms and their weights are consistent
	 */
	@Test
	public void ponderationRequestTest() throws IOException{
		/*
		 * The test messages are sent to the following endpoint : direct:query.enrich
		 * the whole process of defining the recommendation request needs to calls to the database, one to retrieve the user's profile
		 * and the other one to retrieve his traces. In this test the database is simulated, and the results are pre defined and stored
		 * in the simulatedElasticSearchResponses hashmap.   
		 */
		
		
		/*
		 * 3 different use cases are simulated
		 */
	
		
		/*
		 *  case 1 : * user's privacy settings are set to 2 
		 *  		 * the traces come from the same environnement and the same plugin
		 *  		 * the user doesn't have any topic set
		 */
		
		
		// user profile generation
		
		
		String user = createUser(2,new ArrayList<HashMap<String,String>>(), "ponderationrequestest");
		simulatedElasticSearchResponses.put("usersdata", user);
		
		Date begin = new Date();
		Date end = new Date();
		end.setTime(begin.getTime()+250000);
		String endEvent = "unload";
		String beginEvent = "load";
		ArrayList<String> tracesList = new ArrayList<String>();
		String userId = "ponderationrequestest";
		String pluginId = "test";
		
		HashMap<Integer,HashMap<String,String>> traces = new HashMap<Integer,HashMap<String,String>>();
		HashMap<String,String> tr1 = new HashMap<String,String>();
		
		
		tr1.put("url","http://www.website1.org/");
		tr1.put("title", "Cooking pizza");
		traces.put(1, tr1);
		String trace1 = createTrace(tr1.get("url"),tr1.get("title"),"home", begin, end, endEvent, beginEvent, userId, pluginId);
		tracesList.add(trace1);
		
		
		HashMap<String,String> tr2 = new HashMap<String,String>();
		tr2.put("url","http://www.website1.org/page1");
		tr2.put("title", "Pizza garniture");
		traces.put(2, tr2);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+250000);
		String trace2 = createTrace(tr2.get("url"),tr2.get("title"),"home", begin, end, endEvent, beginEvent, userId, pluginId);
		tracesList.add(trace2);
		
		HashMap<String,String> tr3 = new HashMap<String,String>();
		tr3.put("url","http://www.website2.com/");
		tr3.put("title", "Cooking contest");
		traces.put(3, tr3);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+250000);
		String trace3 = createTrace(tr3.get("url"),tr3.get("title"),"home", begin, end, endEvent, beginEvent, userId, pluginId);
		tracesList.add(trace3);
		
		HashMap<String,String> tr4 = new HashMap<String,String>();
		tr4.put("url","http://www.website2.com/register");
		tr4.put("title", "cookies vs pizza");
		traces.put(4, tr4);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+250000);
		String trace4 = createTrace(tr4.get("url"),tr4.get("title"),"home", begin, end, endEvent, beginEvent, userId, pluginId);
		tracesList.add(trace4);
		
		HashMap<String,String> tr5 = new HashMap<String,String>();
		tr5.put("url","http://www.website2.com/thanks");
		tr5.put("title", "How to bake a cake");
		traces.put(5, tr5);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+250000);
		String trace5 = createTrace(tr5.get("url"),tr5.get("title"),"home", begin, end, endEvent, beginEvent, userId, pluginId);
		tracesList.add(trace5);
		

		HashMap<String,String> tr6 = new HashMap<String,String>();
		tr6.put("url","http://www.website2.com/page3");
		tr6.put("title", "Lasagna recipes");
		traces.put(6, tr6);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+250000);
		String trace6 = createTrace(tr6.get("url"),tr6.get("title"),"home", begin, end, endEvent, beginEvent, userId, pluginId);
		tracesList.add(trace6);
		
		HashMap<String,String> tr7 = new HashMap<String,String>();
		tr7.put("url","http://www.website2.com/page4");
		tr7.put("title", "Cake recipes");
		traces.put(7, tr7);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+250000);
		String trace7 = createTrace(tr7.get("url"), tr7.get("title"),"home", begin, end, endEvent, beginEvent, userId, pluginId);
		tracesList.add(trace7);
		
		HashMap<String,String> tr8 = new HashMap<String,String>();
		tr8.put("url","http://www.website3.org/");
		tr8.put("title", "We love cooking");
		traces.put(8, tr8);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+250000);
		String trace8 = createTrace(tr8.get("url"),tr8.get("title"),"home", begin, end, endEvent, beginEvent, userId, pluginId);
		tracesList.add(trace8);
		
		HashMap<String,String> tr9 = new HashMap<String,String>();
		tr9.put("url","http://www.website3.org/42");
		tr9.put("title", "Cooking is what we do");
		traces.put(9, tr9);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+250000);
		String trace9 = createTrace(tr9.get("url"), tr9.get("title"),"home", begin, end, endEvent, beginEvent, userId, pluginId);
		tracesList.add(trace9);
		
		HashMap<String,String> tr10 = new HashMap<String,String>();
		tr10.put("url","http://www.website3.org/tests");
		tr10.put("title", "Survival recipes");
		traces.put(10, tr10);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+250000);
		String trace10 = createTrace(tr10.get("url"),tr10.get("title"),"home", begin, end, endEvent, beginEvent, userId, pluginId);
		tracesList.add(trace10);
		
		simulatedElasticSearchResponses.put("privacytrace", createTracesResponse(tracesList));
		
		Object result = template.sendBody("direct:query.enrich",ExchangePattern.InOut,trace10);
		String ponderatedRequest = (String)(result);
		System.out.println("ponderated request : "+ponderatedRequest);
		JsonParser resultParser = factory.createJsonParser(ponderatedRequest);
		JsonNode pRequest = mapper.readValue(resultParser, JsonNode.class);
		
		HashMap<String,Integer> expectedPonderatedTerms = new HashMap<String,Integer>();
		expectedPonderatedTerms.put("survival",10);
		expectedPonderatedTerms.put("recipes",12);
		expectedPonderatedTerms.put("cooking",7);
		expectedPonderatedTerms.put("what",5);
		expectedPonderatedTerms.put("we",7);
		expectedPonderatedTerms.put("do",5);
		expectedPonderatedTerms.put("love",2);
		expectedPonderatedTerms.put("cake",2);
		expectedPonderatedTerms.put("lasagna",1);
		expectedPonderatedTerms.put("how",1);
		expectedPonderatedTerms.put("bake",1);
		
		Iterator<JsonNode> it = pRequest.path("content").getElements();
		while ( it.hasNext()){
			JsonNode term =it.next();
			assertTrue(expectedPonderatedTerms.containsKey(term.path("term").asText()));
			
			assertEquals ((Object)expectedPonderatedTerms.get( term.path("term").asText() ) , term.path("score").getIntValue() );
			expectedPonderatedTerms.remove(term.path("term").asText());
		}
		assertEquals(expectedPonderatedTerms.size(),0);
		assertEquals(pRequest.path("ponderatedTopics").size(),0);
		
		
		/*
		 *  case 2 : * user's privacy settings are set to 2 
		 *  		 * the traces come from the same environnement and the same plugin
		 *  		 * the user have set some topics for the corresponding environnement
		 */
		
		
		ArrayList<HashMap<String,String>> topics = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> topic1 = new HashMap<String,String>();
		topic1.put("label","chocolate");
		topic1.put("env", "home");
		topic1.put("source", "eexcess");
		topics.add(topic1);
		HashMap<String,String> topic2 = new HashMap<String,String>();
		topic2.put("label","cookies");
		topic2.put("env", "home");
		topic2.put("source", "eexcess");
		topics.add(topic2);
		HashMap<String,String> topic3 = new HashMap<String,String>();
		topic3.put("label","webdesign");
		topic3.put("env", "work");
		topic3.put("source", "eexcess");
		topics.add(topic3);
		
		user = createUser(2,topics, "ponderationrequestest");
		simulatedElasticSearchResponses.remove("usersdata");
		simulatedElasticSearchResponses.put("usersdata", user);
		
		
		
		result = template.sendBody("direct:query.enrich",ExchangePattern.InOut,trace10);
		ponderatedRequest = (String)(result);
		resultParser = factory.createJsonParser(ponderatedRequest);
		pRequest = mapper.readValue(resultParser, JsonNode.class);
		System.out.println("prequest : "+pRequest);
		
		expectedPonderatedTerms.put("survival",10);
		expectedPonderatedTerms.put("recipes",12);
		expectedPonderatedTerms.put("cooking",7);
		expectedPonderatedTerms.put("what",5);
		expectedPonderatedTerms.put("we",7);
		expectedPonderatedTerms.put("do",5);
		expectedPonderatedTerms.put("love",2);
		expectedPonderatedTerms.put("cake",2);
		expectedPonderatedTerms.put("lasagna",1);
		expectedPonderatedTerms.put("how",1);
		expectedPonderatedTerms.put("bake",1);
		
		it = pRequest.path("content").getElements();
		while ( it.hasNext()){
			JsonNode term =it.next();
			assertTrue(expectedPonderatedTerms.containsKey(term.path("term").asText()));
			
			assertEquals ((Object)expectedPonderatedTerms.get( term.path("term").asText() ) , term.path("score").getIntValue() );
			expectedPonderatedTerms.remove(term.path("term").asText());
		}
		assertEquals(expectedPonderatedTerms.size(),0);
		HashMap<String,Integer> expectedPonderatedTopics = new HashMap<String,Integer>();
		expectedPonderatedTopics.put("chocolate", 1);
		expectedPonderatedTopics.put("cookies", 1);
		it = pRequest.path("ponderatedTopics").getElements();
		while ( it.hasNext()){
			JsonNode term =it.next();
			assertTrue(expectedPonderatedTopics.containsKey(term.path("term").asText()));
			
			assertEquals ((Object)expectedPonderatedTopics.get( term.path("term").asText() ) , term.path("value").getIntValue() );
			expectedPonderatedTopics.remove(term.path("term").asText());
		}
		assertEquals(expectedPonderatedTopics.size(),0);
		
		

		/*
		 *  case 3 : * user's privacy settings are set to 0 
		 *  		 * the traces come from the same environnement and the same plugin
		 *  		 * the user didn'ty set any topic
		 */
		
		user = createUser(0,new ArrayList<HashMap<String,String>>(), "ponderationrequestest");
		simulatedElasticSearchResponses.remove("usersdata");
		simulatedElasticSearchResponses.put("usersdata", user);
		
		expectedPonderatedTerms.put("survival",10);
		expectedPonderatedTerms.put("recipes",10);
		
		result = template.sendBody("direct:query.enrich",ExchangePattern.InOut,trace10);
		ponderatedRequest = (String)(result);
		resultParser = factory.createJsonParser(ponderatedRequest);
		pRequest = mapper.readValue(resultParser, JsonNode.class);
		System.out.println("prequest : "+pRequest);
		it = pRequest.path("content").getElements();
		while ( it.hasNext()){
			JsonNode term =it.next();
			assertTrue(expectedPonderatedTerms.containsKey(term.path("term").asText()));
			
			assertEquals ((Object)expectedPonderatedTerms.get( term.path("term").asText() ) , term.path("score").getIntValue() );
			expectedPonderatedTerms.remove(term.path("term").asText());
		}
		assertEquals(expectedPonderatedTerms.size(),0);
		assertEquals(pRequest.path("ponderatedTopics").size(),0);
		
		
		
	}
	
	@Test
	public void recommendationTracesRequest() throws IOException{
		
		
		/*
		 * 1st test case : privacy settings = 2
		 * 				   all the traces are from the same environnement
		 * 				   all the traces are from the same plugin
		 * 
		 */
		String user = createUser(2,new ArrayList<HashMap<String,String>>(), "recommendationTracesRequestTest");
		simulatedElasticSearchResponses.put("usersdata", user);
		
		Date begin = new Date();
		Date end = new Date();
		end.setTime(begin.getTime()+250000);
		String endEvent = "unload";
		String beginEvent = "load";
		ArrayList<String> tracesList = new ArrayList<String>();
		String userId = "recommendationTracesRequestTest";
		String pluginId = "test";
		
		HashMap<Integer,HashMap<String,String>> traces = new HashMap<Integer,HashMap<String,String>>();

		HashMap<String,String> tr10 = new HashMap<String,String>();
		tr10.put("url","http://www.website3.org/tests");
		tr10.put("title", "Survival recipes");
		traces.put(10, tr10);
		begin.setTime(end.getTime());
		end.setTime(begin.getTime()+250000);
		String trace10 = createTrace(tr10.get("url"),tr10.get("title"),"home", begin, end, endEvent, beginEvent, userId, pluginId);
		tracesList.add(trace10);
		
		simulatedElasticSearchResponses.put("privacytrace", createTracesResponse(tracesList));
		
		ParametrizedProcessor p = new ParametrizedProcessor(trace10);
		
		Exchange result = template.request("direct:context.safe.load",p);
		
			
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); 
		
		String beginDate = dateFormat.format(begin);
		String recommendationTracesRequest = result.getProperty("user_context-traces",String.class);
		assertEquals(recommendationTracesRequest,simulatedElasticSearchResponses.get("privacytrace"));
			
			// we need to write the expected traces request
		StringWriter sWriter= new StringWriter ();
		JsonGenerator jg = factory.createJsonGenerator(sWriter);
		jg.writeStartObject();
		jg.writeFieldName("sort");
		jg.writeStartArray();
			jg.writeStartObject();
				jg.writeFieldName("temporal.begin");
				jg.writeStartObject();
					jg.writeStringField("order", "desc");
				jg.writeEndObject();
			jg.writeEndObject();
		jg.writeEndArray();
		jg.writeFieldName("query");
		jg.writeStartObject();
			jg.writeFieldName("bool");
			jg.writeStartObject();
					jg.writeFieldName("must");
					jg.writeStartArray();
						jg.writeStartObject();
							jg.writeFieldName("term");
							jg.writeStartObject();
								jg.writeStringField("trace.user.environnement", "home" );
							jg.writeEndObject();
						jg.writeEndObject();
						jg.writeStartObject();
							jg.writeFieldName("term");
							jg.writeStartObject();
								jg.writeStringField("trace.user.user_id", "recommendationTracesRequestTest" );
							jg.writeEndObject();
						jg.writeEndObject();
						jg.writeStartObject();
							jg.writeFieldName("range");
							jg.writeStartObject();
								jg.writeFieldName("temporal.begin");
								jg.writeStartObject();
									jg.writeStringField("to", beginDate);
									jg.writeBooleanField("include_upper", true);
								jg.writeEndObject();
							jg.writeEndObject();
						jg.writeEndObject();
					jg.writeEndArray();
			jg.writeEndObject();
		jg.writeEndObject();
		jg.writeNumberField("size", 10);
	jg.writeEndObject();
	jg.close();
	
	String expectedQuery = sWriter.toString();
	String actualQuery = simulatedElasticSearchResponses.get("recommendationTracesRequest");
	assertEquals(expectedQuery,actualQuery);
	
	/*
	 * 2nd test case : privacy settings = 1
	 * 				   all the traces are the same environnements
	 * 				   all the traces are from different plugins
	 * 
	 */
	
	user = createUser(1,new ArrayList<HashMap<String,String>>(), "recommendationTracesRequestTest");
	simulatedElasticSearchResponses.put("usersdata", user);
	begin = new Date();
	end = new Date();
	end.setTime(begin.getTime()+250000);
	endEvent = "unload";
	beginEvent = "load";
	tracesList = new ArrayList<String>();
	userId = "recommendationTracesRequestTest";
	pluginId = "test";
	
	traces = new HashMap<Integer,HashMap<String,String>>();
	
	tr10 = new HashMap<String,String>();
	tr10.put("url","http://www.website3.org/tests");
	tr10.put("title", "Survival recipes");
	traces.put(10, tr10);
	begin.setTime(end.getTime());
	end.setTime(begin.getTime()+250000);
	trace10 = createTrace(tr10.get("url"),tr10.get("title"),"home", begin, end, endEvent, beginEvent, userId, pluginId);
	tracesList.add(trace10);
	
	simulatedElasticSearchResponses.put("privacytrace", createTracesResponse(tracesList));
	
	p = new ParametrizedProcessor(trace10);
	
	result = template.request("direct:context.safe.load",p);

	
	beginDate = dateFormat.format(begin);
	recommendationTracesRequest = result.getProperty("user_context-traces",String.class);
	assertEquals(recommendationTracesRequest,simulatedElasticSearchResponses.get("privacytrace"));
		
		// we need to write the expected traces request
	sWriter= new StringWriter ();
	jg = factory.createJsonGenerator(sWriter);
	jg.writeStartObject();
	jg.writeFieldName("sort");
	jg.writeStartArray();
		jg.writeStartObject();
			jg.writeFieldName("temporal.begin");
			jg.writeStartObject();
				jg.writeStringField("order", "desc");
			jg.writeEndObject();
		jg.writeEndObject();
	jg.writeEndArray();
	jg.writeFieldName("query");
	jg.writeStartObject();
		jg.writeFieldName("bool");
		jg.writeStartObject();
				jg.writeFieldName("must");
				jg.writeStartArray();
					jg.writeStartObject();
						jg.writeFieldName("term");
						jg.writeStartObject();
							jg.writeStringField("trace.user.environnement", "home" );
						jg.writeEndObject();
					jg.writeEndObject();
					jg.writeStartObject();
					jg.writeFieldName("term");
					jg.writeStartObject();
						jg.writeStringField("trace.plugin.uuid", pluginId );
					jg.writeEndObject();
				jg.writeEndObject();
					jg.writeStartObject();
						jg.writeFieldName("term");
						jg.writeStartObject();
							jg.writeStringField("trace.user.user_id", "recommendationTracesRequestTest" );
						jg.writeEndObject();
					jg.writeEndObject();
					jg.writeStartObject();
						jg.writeFieldName("range");
						jg.writeStartObject();
							jg.writeFieldName("temporal.begin");
							jg.writeStartObject();
								jg.writeStringField("to", beginDate);
								jg.writeBooleanField("include_upper", true);
							jg.writeEndObject();
						jg.writeEndObject();
					jg.writeEndObject();
				jg.writeEndArray();
		jg.writeEndObject();
	jg.writeEndObject();
	jg.writeNumberField("size", 10);
	jg.writeEndObject();
	jg.close();
	
	expectedQuery = sWriter.toString();
	actualQuery = simulatedElasticSearchResponses.get("recommendationTracesRequest");
	assertEquals(expectedQuery,actualQuery);

	
	/*
	 * 3rd test case : privacy settings = 2, no user id defined
	 * 				   all the traces are the same environnements
	 * 				   all the traces are from the same plugin
	 * 
	 */
	
	user = createUser(1,new ArrayList<HashMap<String,String>>(), "");
	simulatedElasticSearchResponses.put("usersdata", user);
	begin = new Date();
	end = new Date();
	end.setTime(begin.getTime()+250000);
	endEvent = "unload";
	beginEvent = "load";
	tracesList = new ArrayList<String>();
	userId = "";
	pluginId = "test";
	
	traces = new HashMap<Integer,HashMap<String,String>>();
	
	tr10 = new HashMap<String,String>();
	tr10.put("url","http://www.website3.org/tests");
	tr10.put("title", "Survival recipes");
	traces.put(10, tr10);
	begin.setTime(end.getTime());
	end.setTime(begin.getTime()+250000);
	trace10 = createTrace(tr10.get("url"),tr10.get("title"),"home", begin, end, endEvent, beginEvent, userId, pluginId);
	tracesList.add(trace10);
	
	simulatedElasticSearchResponses.put("privacytrace", createTracesResponse(tracesList));
	
	p = new ParametrizedProcessor(trace10);
	
	result = template.request("direct:context.safe.load",p);

	
	beginDate = dateFormat.format(begin);
	recommendationTracesRequest = result.getProperty("user_context-traces",String.class);
	assertEquals(recommendationTracesRequest,simulatedElasticSearchResponses.get("privacytrace"));
		
		// we need to write the expected traces request
	sWriter= new StringWriter ();
	jg = factory.createJsonGenerator(sWriter);
	jg.writeStartObject();
	jg.writeFieldName("sort");
	jg.writeStartArray();
		jg.writeStartObject();
			jg.writeFieldName("temporal.begin");
			jg.writeStartObject();
				jg.writeStringField("order", "desc");
			jg.writeEndObject();
		jg.writeEndObject();
	jg.writeEndArray();
	jg.writeFieldName("query");
	jg.writeStartObject();
		jg.writeFieldName("bool");
		jg.writeStartObject();
				jg.writeFieldName("must");
				jg.writeStartArray();
					jg.writeStartObject();
						jg.writeFieldName("term");
						jg.writeStartObject();
							jg.writeStringField("trace.user.environnement", "home" );
						jg.writeEndObject();
					jg.writeEndObject();
					jg.writeStartObject();
					jg.writeFieldName("term");
					jg.writeStartObject();
						jg.writeStringField("trace.plugin.uuid", pluginId );
					jg.writeEndObject();
				jg.writeEndObject();
					jg.writeStartObject();
						jg.writeFieldName("range");
						jg.writeStartObject();
							jg.writeFieldName("temporal.begin");
							jg.writeStartObject();
								jg.writeStringField("to", beginDate);
								jg.writeBooleanField("include_upper", true);
							jg.writeEndObject();
						jg.writeEndObject();
					jg.writeEndObject();
				jg.writeEndArray();
		jg.writeEndObject();
	jg.writeEndObject();
	jg.writeNumberField("size", 10);
	jg.writeEndObject();
	jg.close();
	
	expectedQuery = sWriter.toString();
	actualQuery = simulatedElasticSearchResponses.get("recommendationTracesRequest");
	assertEquals(expectedQuery,actualQuery);
	
	
	/*
	 * 4th test case : privacy settings = 0, no user id defined
	 * 				  
	 * 
	 */
	user = createUser(0,new ArrayList<HashMap<String,String>>(), "");
	simulatedElasticSearchResponses.put("usersdata", user);
	result = template.request("direct:context.safe.load",p);
	assertEquals("no", result.getProperty("needMoreTraces",String.class));
	recommendationTracesRequest = result.getProperty("user_context-traces",String.class);
	
	sWriter = new StringWriter();
	jg = factory.createJsonGenerator(sWriter);
	
	JsonParser jp = factory.createJsonParser(trace10);
	JsonNode rootNode = mapper.readValue(jp,JsonNode.class);

	ObjectMapper mapper = new ObjectMapper();
	jg.writeStartObject();
		jg.writeFieldName("hits");
		jg.writeStartObject();
			jg.writeNumberField("total", 1);
			jg.writeFieldName("hits");
			jg.writeStartArray();
				jg.writeStartObject();
				jg.writeFieldName("_source");
					mapper.writeTree(jg, rootNode);
				jg.writeEndObject();
			jg.writeEndArray();
		jg.writeEndObject();

		
	jg.writeEndObject();
	jg.close();
	
	expectedQuery = sWriter.toString();
	
	assertEquals(expectedQuery,recommendationTracesRequest);
	
	}
	
	
	
	
	 class ParametrizedProcessor implements Processor{
	    	String body;
			public void process(Exchange exchange) throws Exception {
				Message in = exchange.getIn();
				in.setBody(this.body);
				
			}
			public ParametrizedProcessor ( String body){
				this.body = body;
			}
	 }
	
	public String createTracesResponse ( ArrayList<String> traces) throws JsonGenerationException, IOException{
		
		StringWriter sWriter = new StringWriter();
		JsonGenerator jg = factory.createJsonGenerator(sWriter);
		jg.writeStartObject();
		jg.writeFieldName("hits");
		jg.writeStartObject();
		jg.writeNumberField("total", traces.size());
		jg.writeFieldName("hits");
		jg.writeStartArray();
		Iterator<String> it = traces.iterator();
		while ( it.hasNext()){
			jg.writeStartObject();
			jg.writeStringField("_index", "privacy");
			jg.writeStringField("_type", "trace");
			jg.writeFieldName("_source");
			String trace = it.next();
			JsonParser jp = factory.createJsonParser(trace);
			mapper.writeTree(jg, mapper.readValue(jp, JsonNode.class));
			
			jg.writeEndObject();
		}
		
		jg.writeEndArray();
		
		jg.writeEndObject();
		jg.writeEndObject();
		
		jg.close();

		return sWriter.toString();
	}
	
	/*
	 * This simulates an elasticsearch response 
	 */
	public String createUser(int traceSetting, ArrayList<HashMap<String,String>> topics, String userId) throws IOException{
		
		String userName = "userName";
		String email = "user@email.com";
		String password = "pizza";
		String title = "Mr";
		String lastName = "Mc User";
		String firstName = "user";
		String gender = "M";
		String street = "42 user street";
		String postalCode = "589745";
		String city = "userTown";
		String country = "UserLand";
		String region = "Userstan";
		String district = "Usertown agglomeration";
		
		StringWriter sWriter = new StringWriter();
		JsonGenerator jg = factory.createJsonGenerator(sWriter);
		
		jg.writeStartObject();
		jg.writeFieldName("hits");
		jg.writeStartObject();
		jg.writeNumberField("total",1);
		jg.writeFieldName("hits");
		
		jg.writeStartArray();
		jg.writeStartObject();
		jg.writeStringField("_index","users");
		jg.writeStringField("_type","data");
		jg.writeStringField("_id", userId );
		jg.writeFieldName("_source");
		jg.writeStartObject();
		jg.writeStringField("username", userName);
		jg.writeStringField("email", email);
		jg.writeStringField("password", password);
		jg.writeFieldName("privacy");
		jg.writeStartObject();
		jg.writeStringField("email", "0");
		jg.writeStringField("gender", "0");
		jg.writeStringField("title", "0");
		jg.writeStringField("traces", String.valueOf(traceSetting));
		jg.writeStringField("geoloc", "0");
		jg.writeStringField("birthdate", "0");
		jg.writeStringField("address", "0");
		jg.writeEndObject();
		
		jg.writeStringField("title", title);
		jg.writeStringField("lastname",lastName);
		jg.writeStringField("firstname",firstName);
		jg.writeStringField("gender",gender);
		jg.writeFieldName("address");
		jg.writeStartObject();
		jg.writeStringField("street", street);
		jg.writeStringField("postalcode", postalCode);
		jg.writeStringField("city", city);
		jg.writeStringField("country",country);
		jg.writeStringField("region", region);
		jg.writeStringField("district", district);
		jg.writeEndObject();
		jg.writeFieldName("topics");
		jg.writeStartArray();
		Iterator<HashMap<String,String>> it = topics.iterator();
		while( it.hasNext()){
			HashMap<String,String> topic = it.next();
			jg.writeStartObject();
			jg.writeStringField("label", topic.get("label"));
			jg.writeStringField("env", topic.get("env"));
			jg.writeStringField("source", topic.get("source"));
			jg.writeEndObject();
			
		}
		
		jg.writeEndArray();
	
		jg.writeEndObject();
		jg.writeEndObject();
		jg.writeEndArray();
		jg.writeEndObject();
		jg.writeEndObject();
		
		
		jg.close();
		String u = sWriter.toString();
		
		return u;
	}
	
	public String createTrace(String url, String title, String environnement, Date begin, Date end, String endEvent, String beginEvent) throws IOException{
		return ( createTrace(url,title,environnement,begin,end,endEvent,beginEvent,"","TraceIndexingTest.java"));
		
	}
	
	
	
	
	public String createTrace(String url, String title, String environnement, Date begin, Date end, String endEvent, String beginEvent, String userId, String uuid) throws IOException{
		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); 
		String endDate = dateFormat.format(end);
		String beginDate = dateFormat.format(begin);
		
		
		StringWriter sWriter = new StringWriter();
		JsonGenerator jg = factory.createJsonGenerator(sWriter);
		jg.writeStartObject();
		
		jg.writeFieldName("user");
		jg.writeStartObject();
		if ( !userId.equals("")){
			jg.writeStringField("user_id", userId);
		}
		jg.writeStringField("environnement", environnement);
		jg.writeEndObject();
		
		jg.writeFieldName("plugin");
		jg.writeStartObject();
		jg.writeStringField("version", "1.00");
		jg.writeStringField("uuid", uuid);
		jg.writeEndObject();
		
		jg.writeFieldName("temporal");
		jg.writeStartObject();
		jg.writeStringField("begin", beginDate);
		jg.writeStringField("end", endDate);
		jg.writeEndObject();
		
		jg.writeFieldName("events");
		jg.writeStartObject();
		jg.writeStringField("begin", beginEvent);
		jg.writeStringField("end", endEvent);
		jg.writeEndObject();
		
		jg.writeFieldName("document");
		jg.writeStartObject();
		jg.writeStringField("url", url);
		jg.writeStringField("title", title);
		jg.writeEndObject();
		
		jg.writeFieldName("geolocation");
		jg.writeStartObject();
		jg.writeStringField("country", "FR");
		jg.writeStringField("region", "Rhônes-Alpes");
		jg.writeStringField("district", "Arrondisseument de Lyon");
		jg.writeStringField("place", "Villeurbanne");
		jg.writeStringField("coord", "lat=45.771944,lng=4.890171");
		jg.writeEndObject();
		
		jg.writeEndObject();
		
		jg.close();
		String trace = sWriter.toString();

		return trace;
	}
	
	
	@Override
    protected RouteBuilder createRouteBuilder() throws Exception {
    	
		
		//RouteBuilder rb = new RouteBuilder();
		APIService apiService = new APIService(){
        	public void configure() throws Exception {
        		super.configure();
        		// this route resets the privacy index
        		// warning : all data on this index will be deleted :)
        		
        		from("direct:reinitialize-index")
        			
        			//this deletes the privacy index ( the one with all the traces )
        			.setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
        			.to("http4://localhost:9200/privacy")
        			.setHeader(Exchange.HTTP_METHOD,constant("PUT"))
        			
        			//this creates a new index
        			.to("http4://localhost:9200/privacy/")
        			
        			//this sets the parameter for the new index
        			.process(new Processor(){
						public void process(Exchange exchange) throws Exception {
							factory = new JsonFactory();
							StringWriter sWriter = new StringWriter();
							JsonGenerator jg = factory.createJsonGenerator(sWriter);
							jg.writeStartObject();
							jg.writeFieldName("data");
							jg.writeStartObject();
							jg.writeFieldName("properties");
							jg.writeStartObject();
							
							jg.writeFieldName("plugin.uuid");
							jg.writeStartObject();
							jg.writeStringField("type", "string");
							jg.writeStringField("index", "not_analyzed");
							jg.writeEndObject();
							
							jg.writeFieldName("user.email");
							jg.writeStartObject();
							jg.writeStringField("type", "string");
							jg.writeStringField("index", "not_analyzed");
							jg.writeEndObject();
							
							jg.writeFieldName("temporal.begin");
							jg.writeStartObject();
							jg.writeStringField("type", "date");
							jg.writeEndObject();
							
							jg.writeFieldName("temporal.end");
							jg.writeStartObject();
							jg.writeStringField("type", "date");
							jg.writeEndObject();
							
							jg.writeFieldName("user.user_id");
							jg.writeStartObject();
							jg.writeStringField("type", "string");
							jg.writeStringField("index", "not_analyzed");
							jg.writeEndObject();
							
							jg.writeEndObject();
							jg.writeEndObject();
							jg.writeEndObject();
							jg.close();
							
							String mapping = sWriter.toString();
							
							Message in = exchange.getIn();
							in.setBody(mapping);
						}
        				
        			})
        			.to("http4://localhost:9200/privacy/trace/_mapping")
        		
        		;
        		from("direct:index.trace")
        			.setHeader(Exchange.HTTP_METHOD,constant("POST"))
        			.setProperty("CamelCharsetName",constant("UTF-8"))
        			.to("http4://localhost:9200/privacy/trace")
        		
        		;
        		from("direct:elastic.search")
        			.streamCaching()
        			.setHeader(Exchange.HTTP_URI,simple("http4://localhost:9200/${in.header.ElasticIndex}/${in.header.ElasticType}/_search"))
        			.setHeader(Exchange.HTTP_METHOD,constant("POST"))
    				.setProperty("CamelCharsetName",constant("UTF-8"))
    				.convertBodyTo(String.class)
    				.to("log:test indexingf traces - request?showBody=true")
    				.to("http4://localhost:9200/privacy/trace/_search?sort=temporal.begin:desc&amp;size=50")
    				
        		;
        		from("direct:elastic.userSearch")
        			//.setBody(simple(simulatedElasticSearchResponses.get("${in.header.ElasticIndex}"+"${in.header.ElasticType}")))
        			.process(new Processor(){

						public void process(Exchange exchange) throws Exception {
							// TODO Auto-generated method stub
							Message in = exchange.getIn();
							String index = in.getHeader("ElasticIndex",String.class);
							String type = in.getHeader("ElasticType",String.class);
							if ( index.equals("privacy")&&type.equals("trace")){
								simulatedElasticSearchResponses.put("recommendationTracesRequest", in.getBody(String.class));
							}
							in.setBody(simulatedElasticSearchResponses.get(index+type));
							
						}
        				
        			})
        		;
        		
        		
        	}
        };
        
        apiService.includeRoutes(new APIRecommendation());
        return apiService;
        
    }
}
