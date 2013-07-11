package eu.eexcess.insa.lucene;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class Tokenizer {
	public static List<String> tokenize(String content) {
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_41);
		List<String> tokens = new ArrayList<String>();

		TokenStream stream = null;
		try {
			stream = analyzer.tokenStream("content", new StringReader(content));
			stream.reset();
            while(stream.incrementToken()) {
            	String tok = stream.getAttribute(CharTermAttribute.class).toString();
            	tokens.add(tok);
            }
            stream.end();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(stream != null) {
					stream.close();
				}
			} catch (IOException e) {
			}
		}
		analyzer.close();
		return tokens;
	}
}
