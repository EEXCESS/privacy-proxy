/*************************************************************
*    This script saves the user email in the localStorage.   *
*    More informations may be add later.                     *
*************************************************************/

function register(){

	localStorage.setItem("privacy_email", document.forms["register"].elements[0].value);
	window.privacy_email = document.forms["register"].elements[0].value;
	
}

function click_btn() {
	document.getElementById("sign_up").addEventListener('click',register)
}


document.addEventListener('DOMContentLoaded', function () {
  click_btn();
});