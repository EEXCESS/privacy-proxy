$('.nav-tabs').button();

function logout(){
	localStorage.removeItem("privacy_email");
	localStorage.removeItem("username");
	$('#username').html("Good Bye !");
	$('#logout_btn').hide();
	$('#sign_in').show();
}


function username() {
	if(localStorage["username"] != undefined){
		$('#username').html("Welcome "+localStorage["username"]);
		$('#logout_btn').show();
		$('#sign_in').hide();
		
		if (localStorage["env"] == "home"){
			$('#home').addClass("active");
		}
		else{
			$('#work').addClass("active");
		}
	}
	
	document.getElementById("logout_btn").addEventListener('click',logout);
}

function recommend() {
	chrome.browserAction.setBadgeText({text:""});
	var recommendation = localStorage["recommend"];
	document.getElementById('recommend').innerHTML = recommendation;
}

document.addEventListener('DOMContentLoaded', function () {
  username();
  recommend();
});

if (localStorage["env"] == "work"){
	$("#workButton").addClass("active");
}
else{
	$("#homeButton").addClass("active");
}

$("#workButton").on("click",function(){localStorage["env"]="work";});
$("#homeButton").on("click",function(){localStorage["env"]="home";});

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