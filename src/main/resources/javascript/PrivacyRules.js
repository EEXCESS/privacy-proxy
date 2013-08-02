var privacy = privacy || {
	version: "1.0"
};

privacy.birthdate = {
	levels: 4,
	apply: function (raw, level) {
		return "nothing yet";
	}
}

privacy.apply = function(attribute, rawValue, disclosureLevel) {
	var result = "";
	
	if(privacy[attribute] && privacy[attribute].apply) {
		result = privacy[attribute].apply(rawValue, disclosureLevel);
	} else {
		if(disclosureLevel > 0) {
			result =  rawValue;
		} else {
			result = null;
		}
	}
	java.lang.System.out.println("Privacy for "+attribute+" ("+rawValue+") = "+result);
	return result;
}


/*
 * Example:
 *    privacy.apply("birthdate", "1992-06-03", 1) 
 *    should output  "20's"
 *    
 */