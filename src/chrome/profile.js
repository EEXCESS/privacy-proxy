var userInfo = {
	username: localStorage["username"]
};
var idUser;


$(document).ready(function(){
	var request ={
		term:{
			email: localStorage["privacy_email"]
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
	   		delete userInfo["id"];
	   		generateProfilePage();
	   }
	})
});

function generateProfilePage() {
	$(".username").html("Username: " + userInfo.username);
	
	var gravatar = "http://www.gravatar.com/avatar/"+MD5(userInfo.email);
	$('.gravatarProfile').attr('src',gravatar);
	
	$(".email").html("Email: " + userInfo.email);
	
	$(".title").html("Title: " + userInfo.title);
	if(userInfo.title == "") {
		$(".title").html("Title: Not saved yet");
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
	if(userInfo.gender == "") {
		$(".gender").html("Gender: Not saved yet");
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

function doUpdate(field){
	
	userInfo[field.toLowerCase()] = $('.input'+field).val();
	
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
	   success: function(response) {
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


$(document).ready(function(){
	$('.emailHandle').live("click",doToggleEmail);
	$('.titleHandle').live("click",doToggleTitle);
	$('.lastnameHandle').live("click",doToggleLastname);
	$('.firstnameHandle').live("click",doToggleFirstname);
	$('.genderHandle').live("click",doToggleGender);
	
	$('.submitEmail').live("click",checkUpdate);
	$('.submitTitle').live("click",updateTitle);
	$('.submitLastname').live("click",updateLastname);
	$('.submitFirstname').live("click",updateFirstname);
	$('.submitGender').live("click",updateGender);
});