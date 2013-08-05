var privacy = privacy || {
	version: "1.0"
};

privacy.birthdate = {
	levels: 4,
	apply: function (raw, level) {
		var age;
		var birthY = raw.split('-')[0];
		var birthM = raw.split('-')[1];
		var birthD = raw.split('-')[2];
		
		var date = new Date();
		
		var currentY = date.getFullYear();
		var currentD = date.getDate();
		var currentM = date.getMonth()+1;
		
		age = currentY-birthY;
		if((currentM-birthM < 0) || (currentD-birthD < 0)) age - 1;
		
		switch(level){
		case 0: 
			return "nothing";
			break;
		case 1:
			return(age - age%10+'\'s');
			break;
		case 2:
			return(age + ' years');
			break;
		case 3:
			return raw;
			break;
		};
	}
}

privacy.address = {
	levels: 6,
	apply: function(raw,level){
		
	}
}

privacy.apply = function(attribute, rawValue, disclosureLevel) {
	if(privacy[attribute] && privacy[attribute].apply) {
		return privacy[attribute].apply(rawValue, disclosureLevel);
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