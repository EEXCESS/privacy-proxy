var userInfo = {
	username: localStorage["username"]
};
var idUser;


function initUserInfo(){

	var request ={
		term:{
			_id: localStorage["user_id"]
		}
	};
	
	
	var JSONrequest = JSON.stringify(request);
	
	$.ajax({
	   url: "http://localhost:12564/api/v0/users/profile",
	   type: "POST",
	   contentType: "application/json;charset=UTF-8",
	   data: JSONrequest,
	   complete: function(response){    
	   		userInfo = JSON.parse(response.responseText);
	   		idUser = userInfo["id"];
	   		userInfo = userInfo["values"];
	   		generateProfilePage();	
	   		initSettings();

	   }
	})
};

function generateProfilePage() {
	$(".username").html("Username: " + userInfo.username);
	
	var gravatar = "http://www.gravatar.com/avatar/"+MD5(userInfo.email);
	$('.gravatarProfile').attr('src',gravatar);
	
	$(".email").html("Email: " + userInfo.email);
	$("#fullEmail").html(userInfo.email);
	
	
	$(".title").html("Title: " + userInfo.title);
	$("#fullTitle").html(userInfo.title);
	if(userInfo.title == "") {
		$(".title").html("Title: Not saved yet");
		$("#fullTitle").html("Title: Not saved yet");
	}
	
	$(".lastname").html("Lastname: " + userInfo.lastname);
	if(userInfo.lastname == "") {
		$(".lastname").html("Lastname: Not saved yet");
	}
	
	$(".firstname").html("Firstname: " + userInfo.firstname);
	if(userInfo.firstname == "") {
		$(".firstname").html("Firstname: Not saved yet");
	}
	
	$(".gender").html("Gender: " + userInfo.gender);
	$("#fullGender").html(userInfo.gender);
	if(userInfo.gender == "") {
		$(".gender").html("Gender: Not saved yet");
		$("#fullGender").html("Gender: Not saved yet");
	}
	
	$(".birthdate").html("Birthdate: " + userInfo.birthdate);
	$("#fullAge").html(userInfo.birthdate);
	if(userInfo.birthdate == "") {
		$(".birthdate").html("Birthdate: Not saved yet");
		$("#fullAge").html("Birthdate: Not saved yet");
	}
	$(".topics").html("Topics: " + getTopicsStr());
	if(getTopicsStr() == "") {
		$(".topics").html("Topics : Not defined yet");
	}
	
	generateDateSelect();
	
	generateAddress();
	
	
	
}

function generateDateSelect(){
	
	for (var i=2;i<=31;i++){
		var day = i;
		
		if(day<10) day = "0"+day;
		
		$('.inputBirthdate3').append("<option>"+day+"</option>");
	}
	
	for (var i=2;i<=12;i++){
		var month = i;
		
		if(month<10) month = "0"+month;
		
		$('.inputBirthdate2').append("<option>"+month+"</option>");
	}
	
	
	for (var i=1;i<=120;i++){
		year = 2013-i;
		$('.inputBirthdate1').append("<option>"+year+"</option>");
	}
}

function generateAddress(){
	
	if (userInfo.address == undefined){
		$(".street").html("Address");
		$(".city").html("");
		$(".country").html("");
	}
	else{		
		$(".street").html("Address: " + userInfo.address.street);
		$(".city").html(userInfo.address.postalcode+" "+userInfo.address.city);
		$(".country").html(userInfo.address.country);
		if(userInfo.address.street == "") {
			$(".street").html("Adress: Not saved yet");
			$(".city").html("");
			$(".country").html("");
		}
		
		$('.inputStreet').val(userInfo.address.street);
		$('.inputPostalcode').val(userInfo.address.postalcode);
		$('.inputCity').val(userInfo.address.city);
		$('.inputCountry').val(userInfo.address.country);
	}
}

function doToggleEmail() {

	if ($(".emailChange").css("display") == "none"){
		$(".emailChange").show("slow");
		$('.menuArrowEmail').css("-webkit-transform","rotate(90deg)");		
	}
	else {
		$('.menuArrowEmail').css("-webkit-transform","none");
		$('.emailChange').hide("slow");
	}
}

function doToggleTitle() {

	if ($(".titleChange").css("display") == "none"){
		$(".titleChange").show("slow");
		$('.menuArrowTitle').css("-webkit-transform","rotate(90deg)");		
	}
	else {
		$('.menuArrowTitle').css("-webkit-transform","none");
		$('.titleChange').hide("slow");
	}
}

function doToggleLastname() {

	if ($(".lastnameChange").css("display") == "none"){
		$(".lastnameChange").show("slow");
		$('.menuArrowLastname').css("-webkit-transform","rotate(90deg)");		
	}
	else {
		$('.menuArrowLastname').css("-webkit-transform","none");
		$('.lastnameChange').hide("slow");
	}
}

function doToggleFirstname() {

	if ($(".firstnameChange").css("display") == "none"){
		$(".firstnameChange").show("slow");
		$('.menuArrowFirstname').css("-webkit-transform","rotate(90deg)");		
	}
	else {
		$('.menuArrowFirstname').css("-webkit-transform","none");
		$('.firstnameChange').hide("slow");
	}
}

function doToggleGender() {

	if ($(".genderChange").css("display") == "none"){
		$(".genderChange").show("slow");
		$('.menuArrowGender').css("-webkit-transform","rotate(90deg)");		
	}
	else {
		$('.menuArrowGender').css("-webkit-transform","none");
		$('.genderChange').hide("slow");
	}
}

function doToggleBirthdate() {

	if ($(".birthdateChange").css("display") == "none"){
		$(".birthdateChange").show("slow");
		$('.menuArrowBirthdate').css("-webkit-transform","rotate(90deg)");		
	}
	else {
		$('.menuArrowBirthdate').css("-webkit-transform","none");
		$('.birthdateChange').hide("slow");
	}
}

function doToggleAddress() {

	if ($(".addressChange").css("display") == "none"){
		$(".addressChange").show("slow");
		$('.menuArrowAddress').css("-webkit-transform","rotate(90deg)");		
	}
	else {
		$('.menuArrowAddress').css("-webkit-transform","none");
		$('.addressChange').hide("slow");
	}
}
function doToggleTopics() {

	if ($(".topicsChange").css("display") == "none"){
		if((!(userInfo.topics==null||userInfo.topics==undefined))&&userInfo.topics instanceof Array){
			for(var i=0;i<userInfo.topics.length;i++){
				if(userInfo.topics[i]!=undefined){
					if($('span[name=\"'+userInfo.topics[i].label+'\"]').size()== 0){
						displayTopics(userInfo.topics[i].label);
					}
				}
			}
		}
		$(".topicsChange").show("slow");
		$('.menuArrowTopics').css("-webkit-transform","rotate(90deg)");		
	}
	else {
		$('.menuArrowTopics').css("-webkit-transform","none");
		$('.topicsChange').hide("slow");
	}
}


function checkUpdate(){
	
	var email = $('.inputEmail').val();
	
	if(!validEmail(email)){
		$('.stateEmail').html('This is not a valid email');
	}else{
		
		var body = "{\"term\":{\"data.email\":\""+email+"\"}}";
		
		$.ajax({
		   url: "http://localhost:11564/user/verify",
		   type: "POST",
		   contentType: "application/json;charset=UTF-8",
		   data: body,
		   complete: function(response) {	
			    var responseObject = JSON.parse(response.responseText);
				var taken = responseObject["takenID"];
				if(taken==0) {
					doUpdate("Email");
				}
				else {
					$('.stateEmail').html('Email is already taken');
				}
		   }
		});
	}
}

function updateTitle(){
	doUpdate("Title");
}

function updateLastname(){
	doUpdate("Lastname");
}

function updateFirstname(){
	doUpdate("Firstname");
}

function updateGender(){
	doUpdate("Gender");
}

function updateBirthdate(){
	
	//First, we check the date
	var valid = 0;
	var longMonths = ["01","03","05","07","08","10","12"];
	
	if ($('.inputBirthdate3').val() == "31"){
		for (i=0;i<7;i++){
			if($('.inputBirthdate2').val() == longMonths[i]) {
				valid = 1; 
			}
		}
	}
	else {
		if ($('inputBirthdate2') == "02"){
			if ($('.inputBirthdate3') <= 28){
				valid = 1;
			}
			else {
				if($('.inputBirthdate3') == 29){
					if ((($('.inputBirthdate1')%4)==0) && ($('.inputBirthdate1') != "1900")){
						valid = 1;
					}
				}
			}
		}
		else{
			valid=1;
		}
	}
	
	if(valid){
		userInfo["birthdate"] = $('.inputBirthdate1').val()+"-"+$('.inputBirthdate2').val()+"-"+$('.inputBirthdate3').val();
	
		var userDataJSON = JSON.stringify(userInfo);

		
		$.ajax({
		   url: "http://localhost:12564/api/v0/users/data",
		   type: "POST",
		   contentType: "application/json;charset=UTF-8",
		   data: userDataJSON,
		   beforeSend: function (request)
	       {
	           request.setRequestHeader("traceid", idUser);
	       },
		   complete: function(response) {
				$('.birthdate').html("Birthdate: "+userInfo["birthdate"]);
				$('.stateBirthdate').html("Changes saved");
		   }
		});
	}
	else {
		$('.stateBirthdate').html("This date doesn't exist");
	}
}

function updateAddress(){
	
	userInfo["address"] = {};
	
	userInfo.address["street"] = $('.inputStreet').val();
	userInfo.address["postalcode"] = $('.inputPostalcode').val();
	userInfo.address["city"] = $('.inputCity').val();
	userInfo.address["country"] = $('.inputCountry').val();

	var userDataJSON = JSON.stringify(userInfo);
	
	$.ajax({
	   url: "http://localhost:12564/api/v0/users/data",
	   type: "POST",
	   contentType: "application/json;charset=UTF-8",
	   data: userDataJSON,
	   beforeSend: function (request)
       {
           request.setRequestHeader("traceid", idUser);
       },
	   complete: function(response) {
		    $(".street").html("Address: " + userInfo.address.street);
			$(".city").html(userInfo.address.postalcode+" "+userInfo.address.city);
			$(".country").html(userInfo.address.country);
			$('.stateAddress').html("Changes saved");
	   }
	});
	
}

function doUpdate(field){
	
	userInfo[field.toLowerCase()] = $('.input'+field).val();
	if (userInfo["birthdate"] == "") {
		delete userInfo["birthdate"];
	}
	
	var userDataJSON = JSON.stringify(userInfo);
	
	$.ajax({
	   url: "http://localhost:12564/api/v0/users/data",
	   type: "POST",
	   contentType: "application/json;charset=UTF-8",
	   data: userDataJSON,
	   beforeSend: function (request)
       {
           request.setRequestHeader("traceid", idUser);
       },
	   complete: function(response) {
			$('.'+field.toLowerCase()).html(field+": "+userInfo[field.toLowerCase()]);
			$('.state'+field).html("Changes saved");
			localStorage["privacy_email"] = userInfo["email"];
	   }
	});
}

function validEmail(mail)

{
	var reg = new RegExp('^[a-z0-9]+([_|\.|-]{1}[a-z0-9]+)*@[a-z0-9]+([_|\.|-]{1}[a-z0-9]+)*[\.]{1}[a-z]{2,6}$', 'i');

	if(reg.test(mail))
	{
		return(true);
	}
	else
	{
		return(false);
	}
}

function updateTopics(){
	var values=new Array();
	var tags = document.getElementsByClassName("tag");
	for(var i=0; i<tags.length;i++){
		values[i]={label:tags[i].innerText};
	}
	userInfo.topics=values;
	var userDataJSON = JSON.stringify(userInfo);
	$.ajax({
	   url: "http://localhost:12564/api/v0/users/data",
	   type: "POST",
	   contentType: "application/json;charset=UTF-8",
	   data: userDataJSON,
	   beforeSend: function (request)
       {
           request.setRequestHeader("traceid", idUser);
       },
	   complete: function(response) {
		   
		    $('#topicsTitle').html("Topics : "+getTopicsStr());
			$('.stateTopics').html("Changes saved");
			localStorage["privacy_email"] = userInfo["email"];
	   }
	});
}


function getTopicsStr()
{
	var topicsStr ="";
    if (userInfo.topics instanceof Array){
	for (var i = 0 ; i<userInfo.topics.length;i++){
		   if(topicsStr == ""){
			   topicsStr = topicsStr+userInfo.topics[i].label;
		   }
		   else{
			   topicsStr = topicsStr+", "+userInfo.topics[i].label;
		   }
	  }
    }
	return topicsStr;
}
function doAddTag (){
	
	var newTag = document.createElement('span');
	newTag.setAttribute('class','tag');
	var innerSpan = document.createElement('span');
	var tagValue = document.getElementById('tagsinput_tag').value;
	document.getElementById('tagsinput_tag').value="";
	newTag.setAttribute('name',tagValue);
	innerSpan.innerHTML=tagValue+'<a class="tagsinput-remove-link"></a>';
	newTag.appendChild(innerSpan);
	document.getElementById('tagsinput_tagsinput').insertBefore(newTag,document.getElementById('tagsinput_addTag'));

}


function doRemoveTag(){
	$(this).closest('.tag').remove();
	
}

function displayTopics( topic ){
	var newTag = document.createElement('span');
	newTag.setAttribute('class','tag');
	newTag.setAttribute('name',topic);
	var innerSpan = document.createElement('span');
	document.getElementById('tagsinput_tag').value="";
	innerSpan.innerHTML=topic+'<a class="tagsinput-remove-link"></a>';
	newTag.appendChild(innerSpan);
	document.getElementById('tagsinput_tagsinput').insertBefore(newTag,document.getElementById('tagsinput_addTag'));
}

$(document).ready(function(){
	$('.emailHandle').live("click",doToggleEmail);
	$('.titleHandle').live("click",doToggleTitle);
	$('.lastnameHandle').live("click",doToggleLastname);
	$('.firstnameHandle').live("click",doToggleFirstname);
	$('.genderHandle').live("click",doToggleGender);
	$('.birthdateHandle').live("click",doToggleBirthdate);
	$('.addressHandle').live("click",doToggleAddress);
	$('.topicsHandle').live("click",doToggleTopics);
	
	$('.tagsinput-add').live("click",doAddTag);
	$('.tagsinput-remove-link').live("click",doRemoveTag);
	
	$('.submitEmail').live("click",checkUpdate);
	$('.submitTitle').live("click",updateTitle);
	$('.submitLastname').live("click",updateLastname);
	$('.submitFirstname').live("click",updateFirstname);
	$('.submitGender').live("click",updateGender);
	$('.submitBirthdate').live("click",updateBirthdate);
	$('.submitAddress').live("click",updateAddress);
	$('.submitTopics').live("click",updateTopics);
});
