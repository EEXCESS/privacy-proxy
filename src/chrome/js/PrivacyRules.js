var privacy = privacy || {
	version: "1.0"
};

privacy.birthdate = {
	levels: 4,
	apply: function (raw, level) {
		
	}
}

privacy.apply = function(attribute, rawValue, disclosureLevel) {
	if(privacy[attribute] && privacy[attribute].apply) {
		return privacy[attribute].apply(rawValue, discoluserLevel);
	} else {
		if(discoluserLevel > 0) {
			return rawValue;
		} else {
			return null;
		}
	}
}


/*
 * Example:
 *    privacy.apply("birthdate", "1992-06-03", 1) 
 *    should output  "20's"
 *    
 */