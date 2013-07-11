var privacySettings;
var userId

function updateUserInfo(){
	userDataJSON = 
	$.ajax({
		   url: "http://localhost:12564/api/v0/users/data",
		   type: "POST",
		   contentType: "application/json;charset=UTF-8",
		   data: JSON.stringify(privacySettings),
		   beforeSend: function (request)
	       {
	           request.setRequestHeader("traceid", userId);
	       },
		   success: function(response) {
				
		   }
		});
}

function doSwitchEmail() {
	if($(".switch-email").attr("class") == "switch-animate switch-email switch-off"){
		$(".switch-email").removeClass("switch-off");
		$(".switch-email").addClass("switch-on");
		privacySettings["privacy"]["email"] = 1;
	}
	else{
		$(".switch-email").removeClass("switch-on");
		$(".switch-email").addClass("switch-off");
		privacySettings["privacy"]["email"]= 0;
	}
	updateUserInfo();
}

function doSwitchGender() {
	if($(".switch-gender").attr("class") == "switch-animate switch-gender switch-off"){
		$(".switch-gender").removeClass("switch-off");
		$(".switch-gender").addClass("switch-on");
		$("#titleSetting").show();
		privacySettings["privacy"]["gender"] = 1;
	}
	else{
		$(".switch-gender").removeClass("switch-on");
		$(".switch-gender").addClass("switch-off");
		$("#titleSetting").hide();
		$("#titlePrivacy").attr("checked","false");
		privacySettings["privacy"]["gender"] = 0;
	}
	updateUserInfo();
}

function doSwitchTitle() {
	if($(".switch-title").attr("class") == "switch-animate switch-title switch-off"){
		$(".switch-title").removeClass("switch-off");
		$(".switch-title").addClass("switch-on");
		privacySettings["privacy"]["title"] = 1;
	}
	else{
		$(".switch-title").removeClass("switch-on");
		$(".switch-title").addClass("switch-off");
		privacySettings["privacy"]["title"] = 0;
	}
	updateUserInfo();
}

function initializeSettingsDisplay(){
	if(privacySettings.privacy == undefined ){
		privacySettings.privacy={};
	}
	if ( privacySettings.privacy.email == undefined ||  privacySettings.privacy.email == 1 ){
		 privacySettings.privacy.email = 1 ;
		$(".switch-email").removeClass("switch-off");
		$(".switch-email").addClass("switch-on");
	}
	else if(  privacySettings.privacy.email == 0 ){
		$(".switch-email").removeClass("switch-on");
		$(".switch-email").addClass("switch-off");
	}
	if ( privacySettings.privacy.gender == undefined ||  privacySettings.privacy.gender == 1 ){
		 privacySettings.privacy.gender = 1 ;
		 $(".switch-gender").removeClass("switch-off");
		$(".switch-gender").addClass("switch-on");
		$("#titleSetting").show();
	}
	else if(  privacySettings.privacy.gender == 0 ){
		$(".switch-gender").removeClass("switch-on");
		$(".switch-gender").addClass("switch-off");
		$("#titleSetting").hide();
		$("#titlePrivacy").attr("checked","false");
	}
	if ( privacySettings.privacy.title == undefined ||  privacySettings.privacy.title == 1 ){
		 privacySettings.privacy.title = 1 ;
		 $(".switch-title").removeClass("switch-off");
			$(".switch-title").addClass("switch-on");
	}
	else if(  privacySettings.privacy.title == 0 ){
		$(".switch-title").removeClass("switch-on");
		$(".switch-title").addClass("switch-off");
	}
}



$(document).ready(function(){
	$('.emailSwitch').live("click",doSwitchEmail);
	$('.genderSwitch').live("click",doSwitchGender);
	$('.titleSwitch').live("click",doSwitchTitle);
	chrome.runtime.onMessage.addListener(function(message,sender,sendResponse){
		rawInfos = JSON.parse(message.responseText);
		privacySettings = rawInfos["values"];
		userId = rawInfos["id"];
		
		
		initializeSettingsDisplay();
	});
});