/*************************************************************
*    This script saves the user email in the localStorage.   *
*    More informations may be add later.                     *
*************************************************************/


function valideIDs(){

	var username = document.forms["register"].elements[0].value;
	var email = document.forms["register"].elements[1].value;
	
	var body = "{\"term\":{\"data.username\":\""+username+"\"}},{\"term\":{\"data.email\":\""+email+"\"}}";

	if(validEmail(email)){
		$.ajax({
		   url: "http://localhost:11564/user/verify",
		   type: "POST",
		   contentType: "application/json;charset=UTF-8",
		   data: body,
		   success: function(response) {	
				var taken = response["takenID"];
				if(taken==0) {
					register();
				}
				else {
					document.getElementById('successSignup').innerHTML='Username or Email is already taken';
				}
		   },
		   error: function(response){
		   		alert('error: '+response.status);
		   }
		});
	}
	else {
		document.getElementById('successSignup').innerHTML='Invalid Email';
	}

}

function register(){

	/*
	* TODO : verifier la disponibilite  document.forms["register"].elements[0].value
	* Already done in valideIDs
	*/
	
	var username = document.forms["register"].elements[0].value;
	var email = document.forms["register"].elements[1].value;
	var password = MD5(document.forms["register"].elements[2].value);	
	
	
	
	var userData = {	
		username: username,
		email: email,
		password: password		
	};
	
	var userDataJSON = JSON.stringify(userData);
	
	$.ajax({
	   url: "http://localhost:12564/api/v0/users/data",
	   type: "POST",
	   contentType: "application/json;charset=UTF-8",
	   data: userDataJSON,
	   success: function(response) {
			document.getElementById('successSignup').innerHTML='Registration Successfull';
			localStorage["privacy_email"] = response["email"];
			localStorage["username"] = response["username"];
			document.getElementById("successLogin").innerHTML = "Successful Sign in";
			document.location.href="traces.html";
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


function login() {
	
	var id = document.forms["login"].elements[1].value;
	var password = MD5(document.forms["login"].elements[1].value);	

	if(validEmail(id)) {
		var field = "email";
	}
	else {
		var field = "username";
	}
	
	if(id != ''){
		var userData = "{\"term\":{\"data."+field+"\":\""+id+"\"}},{\"term\":{\"data.password\":\""+password+"\"}}";
	
		$.ajax({
		   	url: "http://localhost:11564/user/login",
		    type: "POST",
		    contentType: "application/json;charset=UTF-8",
		    data: userData,
		    success: function(response) {	
				var loginValid = response["loginValid"];
				if(loginValid==0) {
					document.getElementById("successLogin").innerHTML = "Wrong identification";
				}
				else {
					localStorage["privacy_email"] = response["email"];
					localStorage["username"] = response["username"];
					document.getElementById("successLogin").innerHTML = "Successful Sign in";
					document.location.href="traces.html";
				}
		    }
		});
	}
	
}

function click_btn() {
	document.getElementById("sign_up").addEventListener('click',valideIDs);
	document.getElementById("sign_in").addEventListener('click',login);
	
	document.getElementById("passwordSignin").addEventListener('focus',function(){
		if(this.type=='text') this.type='password';
		if(this.value=='Password') this.value='';
	});
	
	document.getElementById("passwordSignin").addEventListener('blur',function(){
		if(this.value==''){
			this.type='text';
			this.value='Password';
		}
	});
	
	document.getElementById("idSignin").addEventListener('focus',function(){
		if(this.value=='Username/Email')document.getElementById("idSignin").value='';
	});
	
	document.getElementById("idSignin").addEventListener('blur',function(){
		if(this.value==''){
			this.value='Username/Email';
		}
	});
	
	document.getElementById("passwordSignup").addEventListener('focus',function(){
		if(this.type=='text')document.getElementById("passwordSignup").type='password';
		if(this.value=='Password')document.getElementById("passwordSignup").value='';
	});
	
	document.getElementById("passwordSignup").addEventListener('blur',function(){
		if(this.value==''){
			this.value='Password';
			this.type='text';
		}
	});
	
	document.getElementById("emailSignup").addEventListener('focus',function(){
		if(this.value=='Email')document.getElementById("emailSignup").value='';
	});
	
	document.getElementById("emailSignup").addEventListener('blur',function(){
		if(this.value==''){
			this.value='Email';
		}
	});
	
	document.getElementById("fullName").addEventListener('focus',function(){
		if(this.value=='Full Name')document.getElementById("fullName").value='';
	});

	document.getElementById("fullName").addEventListener('blur',function(){
		if(this.value==''){
			this.value='Full Name';
		}
	});

}


document.addEventListener('DOMContentLoaded', function () {
  click_btn();
});