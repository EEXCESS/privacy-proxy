package eu.eexcess.insa.lucene.test;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;

import eu.eexcess.insa.lucene.Tokenizer;

public class TokenizerTest {

	@Test
	public void test() {
		String htmlString = "Maven to &eacute;tonant Repository: org.apache.lucene";
		String utf8String = StringEscapeUtils.unescapeHtml4(htmlString);
		List<String> tokens = Tokenizer.tokenize(utf8String);
		System.out.println(tokens);
	}

}
