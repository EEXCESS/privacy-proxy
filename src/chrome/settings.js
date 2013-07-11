function doSwitchEmail() {
	if($(".switch-email").attr("class") == "switch-animate switch-email switch-off"){
		$(".switch-email").removeClass("switch-off");
		$(".switch-email").addClass("switch-on");
	}
	else{
		$(".switch-email").removeClass("switch-on");
		$(".switch-email").addClass("switch-off");
	}
}

function doSwitchGender() {
	if($(".switch-gender").attr("class") == "switch-animate switch-gender switch-off"){
		$(".switch-gender").removeClass("switch-off");
		$(".switch-gender").addClass("switch-on");
		$("#titleSetting").show();
	}
	else{
		$(".switch-gender").removeClass("switch-on");
		$(".switch-gender").addClass("switch-off");
		$("#titleSetting").hide();
		$("#titlePrivacy").attr("checked","false");
	}
}

function doSwitchTitle() {
	if($(".switch-title").attr("class") == "switch-animate switch-title switch-off"){
		$(".switch-title").removeClass("switch-off");
		$(".switch-title").addClass("switch-on");
	}
	else{
		$(".switch-title").removeClass("switch-on");
		$(".switch-title").addClass("switch-off");
	}
}

function ageSlider(id){
	var indication = "What will be send: ";
	var age;
	var birthY = userInfo.birthdate.split('-')[0];
	var birthM = userInfo.birthdate.split('-')[1];
	var birthD = userInfo.birthdate.split('-')[2];
	
	var date = new Date();
	
	var currentY = date.getFullYear();
	var currentD = date.getDate();
	var currentM = date.getMonth()+1;
	
	age = currentY-birthY;
	if((currentM-birthM < 0) || (currentD-birthD < 0)) age - 1;
	
	switch(id){
	case 0:	age = "nothing";
			break;
	case 1: age = age - age%10 + "'s";
			break;
	case 2:	age = age+ " years";
			break;
	case 3: age = userInfo.birthdate;
			break;
	}
	
	$('.ageExample').html(indication+age);
}

function addressSlider(id){
	var indication = "What will be send: ";
	
	switch(id){
	case 0: address = "nothing";
			break;
	case 1: address = userInfo.address.country;
			break;
	case 2: address = region(userInfo.address.postalcode);
			break;
	}
}

window.onload = function() {
	$('.emailSwitch').live("click",doSwitchEmail);
	$('.genderSwitch').live("click",doSwitchGender);
	$('.titleSwitch').live("click",doSwitchTitle);  
	
	$("#slider").slider({
		value:1,
		min: 0,
		max: 3,
		step: 1,
		change: function( event, ui ) {
			ageSlider(ui.value);
		}
	});
	
	$("#sliderAddress").slider({
		value:1,
		min: 0,
		max: 4,
		step: 1,
		change: function( event, ui ) {
			addressSlider(ui.value);
		}
	});
};
