/*************************************************************
*    This script saves the user email in the localStorage.   *
*    More informations may be add later.                     *
*************************************************************/


function valideUsername(){

	var username = document.forms["register"].elements[0].value;
	var email = document.forms["register"].elements[1].value;
	
	var body = "{\"term\":{\"data.username\":\""+username+"\"}},{\"term\":{\"data.email\":\""+email+"\"}}";

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

function register(){

	/*
	* TODO : verifier la disponibilite  document.forms["register"].elements[0].value
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
	   }
	});
	
}

function click_btn() {
	document.getElementById("sign_up").addEventListener('click',valideUsername)
}


document.addEventListener('DOMContentLoaded', function () {
  click_btn();
});