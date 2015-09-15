package eu.eexcess;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class is the configuration file manager. 
 * It implements the Singleton design pattern. 
 * @author Thomas Cerqueus
 */
public class Config {

	// Configuration file
	private static final String PROP_FILE_NAME = "config.properties";
	
	private static Properties prop = null;

	/**
	 * Initialization of the configuration file manager. 
	 */
	public Config(){
		Config.prop = new Properties();
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(Config.PROP_FILE_NAME);
		try {
			if (inputStream != null) {
				prop.load(inputStream);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the value associate with a given key. 
	 * @param key Key of the variable. 
	 * @return Value corresponding to the key.  
	 */
	public static String getValue(String key) {
		String value = null;
		if (Config.prop == null){
			new Config();
		}
		value = prop.getProperty(key);
		return value;
	}

}
