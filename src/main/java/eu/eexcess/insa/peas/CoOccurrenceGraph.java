package eu.eexcess.insa.peas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import eu.eexcess.Config;

/**
 * TODO 
 * @author Thomas Cerqueus
 *
 */
public class CoOccurrenceGraph extends Graph {

	private static final long serialVersionUID = 1L;
	private static final String COLUMN_SEPARATOR = "\t";
	private static final String KEYWORDS_SEPARATOR = " ";
	private static final String MATCHING_CRITERION = "^\\w{3,}$"; // Words with at least 3 characters
	
	public CoOccurrenceGraph(){
		super();
		String path = Config.getValue(Config.GROUP_PROFILE_LOCATION);
		if (!path.startsWith(File.separator)){
			path = File.separator + path;
		}
		init(path);
	}

	public CoOccurrenceGraph(String path){
		super();
		init(path);
	}

	private void init(String path){
		try {
			InputStream in = getClass().getResourceAsStream(path); 
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
			String currentLine;
			while ((currentLine = bufferReader.readLine()) != null) {
				String[] arrayLine = currentLine.split(COLUMN_SEPARATOR);
				String query = arrayLine[2];
				String[] keywords = query.split(KEYWORDS_SEPARATOR);
				List<String> filteredKeywords = new ArrayList<String>();
				// Filtering the keywords
				for (int i = 0 ; i < keywords.length ; i++){
					if (keywords[i].matches(MATCHING_CRITERION)){ 
						filteredKeywords.add(keywords[i].toLowerCase());
					}
				}
				if (filteredKeywords.size() > 1){
					// Add the keywords to the graph
					for (int i = 0 ; i < filteredKeywords.size() ; i++){
						for (int j = (i+1) ; j < filteredKeywords.size() ; j++){
							String w1 = filteredKeywords.get(i);
							String w2 = filteredKeywords.get(j);
							if (!w1.equals(w2)){
								this.incrementWeight(w1, w2);
							}
						}
					}
				}
			}
			bufferReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		CoOccurrenceGraph g = new CoOccurrenceGraph();
		g.extractMaximalCliques();
		
	}
	
}
