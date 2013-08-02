package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.script.*;
public class ApplyPrivacySettingsJS implements Processor{
	ScriptEngine engine;
	public ApplyPrivacySettingsJS() throws ScriptException {
		InputStream privacyJS= ClassLoader.getSystemResourceAsStream("javascript/PrivacyRules.js");
		ScriptEngineManager factory = new ScriptEngineManager();
		engine = factory.getEngineByName("JavaScript");
		engine.eval(new InputStreamReader(privacyJS));
	}

	public void process(Exchange exchange) throws Exception {
		// foreach attribute for which we have privacy settings do
		// applyPrivacy(...) and replace value with output
	}
	
	public String applyPrivacy(String attribute, String rawValue, int disclosureLevel) throws ScriptException {
		String jsExpr = "privacy.apply('"+attribute+"','"+rawValue+"',"+disclosureLevel+")";
		Object result = engine.eval(jsExpr);
		return result.toString();
	}
	
	public static void main(String[] args) throws ScriptException {
		ApplyPrivacySettingsJS privacy = new ApplyPrivacySettingsJS();
		String attribute ="birthdate";
		String rawValue = "1992-06-03";
		Integer disclosureLevel = 2;
		System.out.println(privacy.applyPrivacy(attribute, rawValue, disclosureLevel));
	}
}
