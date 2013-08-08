var ageTooltips= {};
var addressTooltips= {};
var geolocTooltips= {};
var tracesTooltips= ["Only the current page will be sent","Your account traces on this computer will be sent","Your account traces on all computers will be sent"];

function updateUserInfo(){
	userDataJSON = JSON.stringify(userInfo);
	$.ajax({
		   url: "http://localhost:12564/api/v0/users/privacy_settings",
		   type: "POST",
		   contentType: "application/json;charset=UTF-8",
		   data: userDataJSON,
		   beforeSend: function (request)
	       {
	           request.setRequestHeader("traceid", idUser);
	       },
		   complete: function(response) {
			   $('.stateSettings').html("Changes saved");
		   }
		});
}

function doSwitchEmail() {
	if($(".switch-email").attr("class") == "switch-animate switch-email switch-off"){
		$(".switch-email").removeClass("switch-off");
		$(".switch-email").addClass("switch-on");
		userInfo.privacy.email = "1";
		$("#list_settings").find(".email").html("Email: " + userInfo.email);
	}
	else{
		$(".switch-email").removeClass("switch-on");
		$(".switch-email").addClass("switch-off");
		userInfo.privacy.email= "0";
		$("#list_settings").find(".email").html("Email: nothing");
	}
}

function doSwitchGender() {
	
	if($(".switch-gender").attr("class") == "switch-animate switch-gender switch-off"){
		$(".switch-gender").removeClass("switch-off");
		$(".switch-gender").addClass("switch-on");
		$("#titleSetting").show();
		userInfo["privacy"]["gender"] = 1;
		$("#list_settings").find(".gender").html("Gender: " + userInfo.gender);
	}
	else{
		$(".switch-gender").removeClass("switch-on");
		$(".switch-gender").addClass("switch-off");
		$("#titleSetting").hide();
		userInfo["privacy"]["gender"] = 0;
		$("#list_settings").find(".gender").html("Gender: nothing");
		$(".switch-title").removeClass("switch-on");
		$(".switch-title").addClass("switch-off");
		userInfo["privacy"]["title"] = 0;
		$("#list_settings").find(".title").html("Title: nothing");
	}
}

function doSwitchTitle() {
	
	if($(".switch-title").attr("class") == "switch-animate switch-title switch-off"){
		$(".switch-title").removeClass("switch-off");
		$(".switch-title").addClass("switch-on");
		userInfo["privacy"]["title"] = 1;
		$("#list_settings").find(".title").html("Title: "+userInfo.title);

	}
	else{
		$(".switch-title").removeClass("switch-on");
		$(".switch-title").addClass("switch-off");
		userInfo["privacy"]["title"] = 0;
		$("#list_settings").find(".title").html("Title: nothing");
	}
}

function triggerUpdateGeoloc() {
	
	navigator.geolocation.getCurrentPosition(function(position){
        var latitude = position.coords.latitude;
        var longitude = position.coords.longitude;
        
        url = "http://api.geonames.org/findNearbyPostalCodesJSON?lat="+latitude+"&lng=" + longitude +"&username=eexcess.insa";

    	$.ajax({
    	   url: url,
    	   type: "GET",
    	   contentType: "text/json;charset=UTF-8",
    	   success: function(response) {
    		   coord = "lat="+latitude+",lng="+longitude;
    		   $(".geoloc span").html("Geolocation: Latitude "+latitude+" , Longitude "+longitude);
    		   doUpdateGeoloc(response,coord);
    	   }
    	});
    });
}

function doUpdateGeoloc(geoname,coord){
	
	//if((userInfo. == undefined) || (userInfo.birthdate == "")) $('#ageSetting').hide();
	
	geolocTooltips[0] = "nothing";
	geolocTooltips[1] = geoname.postalCodes[0].countryCode;
	geolocTooltips[2] = geoname.postalCodes[0].adminName1;
	geolocTooltips[3] = geoname.postalCodes[0].adminName3;
	geolocTooltips[4] = geoname.postalCodes[0].placeName;
	geolocTooltips[5] = coord;
	
	settingsGeolocReady();
}

function triggerUpdateAddress(){
	
	
	if((userInfo.address == undefined) || (userInfo.address.street == "")) {
		$('#addressSetting').hide();
	}
	else{
		doUpdateAddress();
	}
}

function doUpdateAddress(){
	
	addressTooltips[0] = "nothing";
	addressTooltips[1] = JSON.parse(privacy.apply("address",JSON.stringify(userInfo.address),1)).country;
	addressTooltips[2] = JSON.parse(privacy.apply("address",JSON.stringify(userInfo.address),2)).region;
	addressTooltips[3] = JSON.parse(privacy.apply("address",JSON.stringify(userInfo.address),3)).district;
	addressTooltips[4] = JSON.parse(privacy.apply("address",JSON.stringify(userInfo.address),4)).city;
	
	var address = JSON.parse(privacy.apply("address",JSON.stringify(userInfo.address),5));
	addressTooltips[5] = userInfo.address.street+", "+userInfo.address.postalcode+" "+userInfo.address.city+", "+userInfo.address.country;
	
	settingsAddressReady();
}


function initializeSettingsDisplay(){
	if(userInfo.privacy == undefined ){
		userInfo.privacy={};
	}
	if ( userInfo.privacy.email == undefined || userInfo.privacy.email == "" ||  userInfo.privacy.email == "1" ){
		userInfo.privacy.email = "1" ;
		$(".switch-email").removeClass("switch-off");
		$(".switch-email").addClass("switch-on");
		$("#list_settings").find(".email span").html(userInfo.email);
	}
	else if(  userInfo.privacy.email == "0" ){
		$(".switch-email").removeClass("switch-on");
		$(".switch-email").addClass("switch-off");
		$("#list_settings").find(".email span").html("nothing");
	}
	if ( userInfo.privacy.gender == undefined || userInfo.privacy.gender == "" || userInfo.privacy.gender == "1" ){
		userInfo.privacy.gender = "1" ;
		 $(".switch-gender").removeClass("switch-off");
		$(".switch-gender").addClass("switch-on");
		$("#list_settings").find(".gender span").html(userInfo.gender);
		$("#titleSetting").show();
	}
	else if(  userInfo.privacy.gender == "0" ){
		$(".switch-gender").removeClass("switch-on");
		$(".switch-gender").addClass("switch-off");
		$("#titleSetting").hide();
		$("#list_settings").find(".gender span").html("nothing");
		$("#titlePrivacy").attr("checked","false");
	}
	if ( userInfo.privacy.title == undefined || userInfo.privacy.title == "" ||  userInfo.privacy.title == "1" ){
		userInfo.privacy.title = "1" ;
		 $(".switch-title").removeClass("switch-off");
			$(".switch-title").addClass("switch-on");
			$("#list_settings").find(".title span").html(userInfo.title);
	}
	else if(  userInfo.privacy.title == "0" ){
		$(".switch-title").removeClass("switch-on");
		$(".switch-title").addClass("switch-off");
		$("#list_settings").find(".title span").html("nothing");
	}
	if ( userInfo.privacy.age == undefined || userInfo.privacy.age == ""){
		userInfo.privacy.age = "0" ;
	}
	if ( userInfo.privacy.address == undefined || userInfo.privacy.address == ""){
		userInfo.privacy.address = "0" ;
	}	
	if ( userInfo.privacy.geoloc == undefined || userInfo.privacy.geoloc == ""){
		userInfo.privacy.geoloc = "0" ;
	}	
	if ( userInfo.privacy.traces == undefined || userInfo.privacy.traces == ""){
		userInfo.privacy.traces = "0" ;
	}	
	
	if(userInfo.title == undefined || userInfo.title == "") $("#titleSetting").hide();
	
	if((userInfo.gender != undefined) && (userInfo.gender != "")) {
		$('#genderSetting').show();
	}
	if((userInfo.title != undefined) && (userInfo.title != "") && (userInfo.gender == 1)) {
		$('#titleSetting').show();
	}
	
	
	//endInit();
}
	
function doUpdateAge(){
	
	if((userInfo.birthdate == undefined) || (userInfo.birthdate == "")) {
		$('#ageSetting').hide();
	}
	else{
		var indication = "What will be send: ";
		
		ageTooltips[0] = "nothing";
		ageTooltips[1] = JSON.parse(privacy.apply("birthdate",userInfo.birthdate,1)).decade;
		ageTooltips[2] = JSON.parse(privacy.apply("birthdate",userInfo.birthdate,2)).age;
		ageTooltips[3] = JSON.parse(privacy.apply("birthdate",userInfo.birthdate,3)).date;

		
		settingsAgeReady();
	}	
}

function initSettings(){
	triggerUpdateAddress();
	doUpdateAge();
	triggerUpdateGeoloc();
	initializeSettingsDisplay();
	settingsTracesReady();
}

function tracesHoverIn(){
	if ($(this).find(".tooltip-inner").html() == "undefined"){
		$(this).find(".tooltip-inner").html(tracesTooltips[parseInt(userInfo.privacy.traces)]);
	}
	$(this).find(".slider-tip").show();
}

function tracesHoverOut(){
	$(this).find(".slider-tip").hide();
}

function geolocHoverIn(){
	if ($(this).find(".tooltip-inner").html() == "undefined"){
		$(this).find(".tooltip-inner").html(geolocTooltips[parseInt(userInfo.privacy.geoloc)]);
	}
	$(this).find(".slider-tip").show();
}

function geolocHoverOut(){
	$(this).find(".slider-tip").hide();
}


function addressHoverIn(){
	if ($(this).find(".tooltip-inner").html() == "undefined"){
		$(this).find(".tooltip-inner").html(addressTooltips[parseInt(userInfo.privacy.address)]);
	}
	$(this).find(".slider-tip").show();
}

function addressHoverOut(){
	$(this).find(".slider-tip").hide();
}

function ageHoverIn(){
	if ($(this).find(".tooltip-inner").html() == "undefined"){
		$(this).find(".tooltip-inner").html(privacy.apply("birthdate",userInfo.birthdate,parseInt(userInfo.privacy.age)));
	}
	$(this).find(".slider-tip").show();
}

function ageHoverOut(){
	$(this).find(".slider-tip").hide();
}

function hoverIn(){
	$(this).find(".slider-tip").show();
}

function hoverOut(){
	$(this).find(".slider-tip").hide();
}

function settingsAgeReady(){
	

	$("#slider").slider({
		value:parseInt(userInfo.privacy.age),
		min: 0,
		max: 3,
		step: 1,
		create:function(){
			$("#fullAge").html(ageTooltips[3]);
			$(this).children(".ui-slider-handle").html('<div class="tooltip top slider-tip"><div class="tooltip-arrow"></div><div class="tooltip-inner">' + ageTooltips[parseInt(userInfo.privacy.age)] + '</div></div>');
			$(this).find(".slider-tip").hide();
			$("#list_settings").find(".birthdate span").html(ageTooltips[parseInt(userInfo.privacy.age)]);
			if(userInfo.privacy.age == "0"){
				$(this).find('.tooltip-inner').css("margin-left","85px");
				$(this).find('.tooltip-arrow').css("margin-left","-52px");
			}
			if(userInfo.privacy.age == "3"){
				$(this).find('.tooltip-inner').css("margin-left","-60px");
				$(this).find('.tooltip-arrow').css("margin-left","20px");
			}
		},
		slide: function( event, ui ) {
			if(ui.value == 0){
				$(this).find('.ui-slider-handle').find('.tooltip-inner').css("margin-left","85px");
				$(this).find('.ui-slider-handle').find('.tooltip-arrow').css("margin-left","-52px");
			}
			else if(ui.value == 3){
				$(this).find('.ui-slider-handle').find('.tooltip-inner').css("margin-left","-60px");
				$(this).find('.ui-slider-handle').find('.tooltip-arrow').css("margin-left","20px");
			}
			else {
				$(this).find('.ui-slider-handle').find('.tooltip-inner').css("margin-left","0");
				$(this).find('.ui-slider-handle').find('.tooltip-arrow').css("margin-left","-10px");
			}
			userInfo.privacy.age = "" + ui.value;
			$("#list_settings").find(".birthdate span").html(ageTooltips[parseInt(userInfo.privacy.age)]);
			$(this).find('.ui-slider-handle').find(".tooltip-inner").html(ageTooltips[parseInt(userInfo.privacy.age)]);
		},
		stop: function(){
			$(".slider-tip").hide();
		}
	}).addSliderSegments(4,ageTooltips);
}

function settingsAddressReady(){
	
	$("#sliderAddress").find(".slider-tip").html(addressTooltips[parseInt(userInfo.privacy.address)]);
	
	$("#sliderAddress").slider({
		value:parseInt(userInfo.privacy.address),
		min: 0,
		max: 5,
		step: 1,
		rang: "min",
		create:function(){
			$("#fullAddress").html(addressTooltips[5]);
			$("#list_settings").find(".street span").html(addressTooltips[parseInt(userInfo.privacy.address)]);
			$(this).children(".ui-slider-handle").html('<div class="tooltip top slider-tip"><div class="tooltip-arrow"></div><div class="tooltip-inner">' + addressTooltips[parseInt(userInfo.privacy.address)] + '</div></div>');
			$(this).find(".slider-tip").hide();
			if(userInfo.privacy.address == "0"){
				$(this).find('.tooltip-inner').css("margin-left","85px");
				$(this).find('.tooltip-arrow').css("margin-left","-52px");
			}
			if(userInfo.privacy.address == "5"){
				$(this).find('.tooltip-inner').css("margin-left","-60px");
				$(this).find('.tooltip-arrow').css("margin-left","20px");
			}
		},
		slide: function( event, ui ) {
			if(ui.value == 0){
				$(this).find('.ui-slider-handle').find('.tooltip-inner').css("margin-left","85px");
				$(this).find('.ui-slider-handle').find('.tooltip-arrow').css("margin-left","-52px");
			}
			else if(ui.value == 5){
				$(this).find('.ui-slider-handle').find('.tooltip-inner').css("margin-left","-60px");
				$(this).find('.ui-slider-handle').find('.tooltip-arrow').css("margin-left","20px");
			}
			else {
				$(this).find('.ui-slider-handle').find('.tooltip-inner').css("margin-left","0");
				$(this).find('.ui-slider-handle').find('.tooltip-arrow').css("margin-left","-10px");
			}
			userInfo.privacy.address = "" + ui.value;
			$("#list_settings").find(".street span").html(addressTooltips[parseInt(userInfo.privacy.address)]);

			$(this).find('.ui-slider-handle').find(".tooltip-inner").html(addressTooltips[parseInt(userInfo.privacy.address)]);
		},
		stop: function(){
			$(".slider-tip").hide();
		}
	}).addSliderSegments(6,addressTooltips);
};

function settingsGeolocReady(){
	
	if (userInfo.privacy.geoloc == undefined) userInfo.privacy.geoloc = "0";
	
	$("#sliderGeoloc").slider({
		value:parseInt(userInfo.privacy.geoloc),
		min: 0,
		max: 5,
		step: 1,
		create:function(){
			$("#fullGeoloc").html(geolocTooltips[5]);	
			$(this).children(".ui-slider-handle").html('<div class="tooltip top slider-tip"><div class="tooltip-arrow"></div><div class="tooltip-inner">' + geolocTooltips[parseInt(userInfo.privacy.geoloc)] + '</div></div>');
			$("#list_settings").find(".geoloc span").html(geolocTooltips[parseInt(userInfo.privacy.geoloc)]);
			$(this).find(".slider-tip").hide();
			if(userInfo.privacy.geoloc == "0"){
				$(this).find('.tooltip-inner').css("margin-left","85px");
				$(this).find('.tooltip-arrow').css("margin-left","-52px");
			}
			if(userInfo.privacy.geoloc == "5"){
				$(this).find('.tooltip-inner').css("margin-left","-60px");
				$(this).find('.tooltip-arrow').css("margin-left","20px");
			}
		},
		slide: function( event, ui ) {
			if(ui.value == 0){
				$(this).find('.ui-slider-handle').find('.tooltip-inner').css("margin-left","85px");
				$(this).find('.ui-slider-handle').find('.tooltip-arrow').css("margin-left","-52px");
			}
			else if(ui.value == 5){
				$(this).find('.ui-slider-handle').find('.tooltip-inner').css("margin-left","-60px");
				$(this).find('.ui-slider-handle').find('.tooltip-arrow').css("margin-left","20px");
			}
			else {
				$(this).find('.ui-slider-handle').find('.tooltip-inner').css("margin-left","0");
				$(this).find('.ui-slider-handle').find('.tooltip-arrow').css("margin-left","-10px");
			}
			userInfo.privacy.geoloc = "" + ui.value;
			$("#list_settings").find(".geoloc span").html(geolocTooltips[parseInt(userInfo.privacy.geoloc)]);
			$(this).find('.ui-slider-handle').find(".tooltip-inner").html(geolocTooltips[parseInt(userInfo.privacy.geoloc)]);
		},
		stop: function(){
			$(".slider-tip").hide();
		}
	}).addSliderSegments(6,geolocTooltips);
}

function settingsTracesReady(){
	
	if (userInfo.privacy.traces == undefined) userInfo.privacy.traces = "0";
	
	$("#sliderTraces").slider({
		value:parseInt(userInfo.privacy.traces),
		min: 0,
		max: 2,
		step: 1,
		create:function(){
			$("#fullTraces").html(tracesTooltips[2]);
			$(this).children(".ui-slider-handle").html('<div class="tooltip top slider-tip"><div class="tooltip-arrow"></div><div class="tooltip-inner">' + tracesTooltips[parseInt(userInfo.privacy.traces)] + '</div></div>');
			$(this).find(".slider-tip").hide();
			$("#list_settings").find(".traces span").html(tracesTooltips[parseInt(userInfo.privacy.traces)]);
			if(userInfo.privacy.traces == "0"){
				$(this).find('.tooltip-inner').css("margin-left","85px");
				$(this).find('.tooltip-arrow').css("margin-left","-52px");
			}
			if(userInfo.privacy.traces == "2"){
				$(this).find('.tooltip-inner').css("margin-left","-60px");
				$(this).find('.tooltip-arrow').css("margin-left","20px");
			}
		},
		slide: function( event, ui ) {
			if(ui.value == 0){
				$(this).find('.ui-slider-handle').find('.tooltip-inner').css("margin-left","85px");
				$(this).find('.ui-slider-handle').find('.tooltip-arrow').css("margin-left","-52px");
			}
			else if(ui.value == 2){
				$(this).find('.ui-slider-handle').find('.tooltip-inner').css("margin-left","-60px");
				$(this).find('.ui-slider-handle').find('.tooltip-arrow').css("margin-left","20px");
			}
			else {
				$(this).find('.ui-slider-handle').find('.tooltip-inner').css("margin-left","0");
				$(this).find('.ui-slider-handle').find('.tooltip-arrow').css("margin-left","-10px");
			}
			userInfo.privacy.traces = "" + ui.value;
			$("#list_settings").find(".traces span").html(tracesTooltips[parseInt(userInfo.privacy.traces)]);
			$(this).find('.ui-slider-handle').find(".tooltip-inner").html(tracesTooltips[parseInt(userInfo.privacy.traces)]);
		},
		stop: function(){
			$(".slider-tip").hide();
		}
	}).addSliderSegments(3,tracesTooltips);
}




$(document).ready(function(){
	
	var docUrl = $(document)[0].URL;
	var pluginUrl = docUrl.split('/')[0]+"//"+docUrl.split('/')[2]+'/';
	
	$('.tabTraces').live("click",function(){document.location = pluginUrl+"traces.html"});
	$('.tabProfile').live("click",function(){document.location = pluginUrl+"profile.html"});
	
	initSandbox();
	initUserInfo();
	
	$('.ui-slider-segment').live("mouseenter",hoverIn).live("mouseleave",hoverOut);
	
	$("#slider").find(".ui-slider-handle").live("mouseenter",ageHoverIn).live("mouseleave",ageHoverOut);
	$("#sliderAddress").find(".ui-slider-handle").live("mouseenter",addressHoverIn).live("mouseleave",addressHoverOut);
	$("#sliderGeoloc").find(".ui-slider-handle").live("mouseenter",geolocHoverIn).live("mouseleave",geolocHoverOut);
	$("#sliderTraces").find(".ui-slider-handle").live("mouseenter",tracesHoverIn).live("mouseleave",tracesHoverOut);

	$('.emailSwitch').live("click",doSwitchEmail);
	$('.genderSwitch').live("click",doSwitchGender);
	$('.titleSwitch').live("click",doSwitchTitle);  
	$('.submitSetting').live("click",updateUserInfo);

});



