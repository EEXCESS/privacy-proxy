function granularityBirthdate(value, privacy){
	var age;
	var birthY = value.split('-')[0];
	var birthM = value.split('-')[1];
	var birthD = value.split('-')[2];
	
	var date = new Date();
	
	var currentY = date.getFullYear();
	var currentD = date.getDate();
	var currentM = date.getMonth()+1;
	
	age = currentY-birthY;
	if((currentM-birthM < 0) || (currentD-birthD < 0)) age - 1;
	
	switch(privacy){
	case 0: 
		return "nothing";
		break;
	case 1:
		return(age - age%10+'\'s');
		break;
	case 2:
		return(age + 'years');
		break;
	case 3:
		return value;
		break;
	};
}

function granularity(attribute, value, privacyLevel){
	switch(attribute){
	case "birthdate":
		return granularityBirthdate(value,privacyLevel);
		break;
	case "address":
		return granularityAddress(value,privacyLevel);
		break;
	case "email":
		return granularityEmail(value,privacyLevel);
		break;
	case "title":
		return granularityTitle(value,privacyLevel);
		break;
	case "gender":
		return granularityGender(value,privacyLevel);
		break;
	case "geolocation":
		return granularityGeolocation(value,privacyLevel);
		break;
	}
	
}