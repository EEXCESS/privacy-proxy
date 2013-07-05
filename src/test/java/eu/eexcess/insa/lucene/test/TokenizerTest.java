package eu.eexcess.insa.lucene.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import eu.eexcess.insa.lucene.Tokenizer;

public class TokenizerTest {

	@Test
	public void test() {
		List<String> tokens = Tokenizer.tokenize("Maven Repository: org.apache.lucene");
		System.out.println(tokens);
	}

}
