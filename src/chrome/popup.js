function logout(){
	localStorage.removeItem("privacy_email");
	localStorage.removeItem("username");
	document.getElementById('username').innerHTML = "Good Bye !";
	document.getElementById('logout_btn').style.visibility = "hidden";
}


function username() {
	if(localStorage["username"] != undefined){
		document.getElementById('username').innerHTML = "Welcome "+localStorage["username"];
		document.getElementById('logout_btn').style.visibility= "visible";
	}
	
	document.getElementById("logout_btn").addEventListener('click',logout);
}

document.addEventListener('DOMContentLoaded', function () {
  username();
});



/*<script>
		chrome.browserAction.setBadgeBackgroundColor({color:[190, 190, 190, 230]});
		chrome.browserAction.setBadgeText({text:"?"});

		chrome.extension.onMessage.addListner(function(v,s,sendResponse)) {
		  alert(v);
		  sendResponse();
		}
		alert("Loaded");

		var notification = webkitNotifications.createHTMLNotification(
		'notification.html'  // html url - can be relative
		);
		notification.show();
		</script>*/