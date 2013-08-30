$('.nav-tabs').button();


function oauthMendeley(){
	$.ajax({
		   	url: "http://localhost:11564/oauth/mendeley/init",
		    type: "POST",
		    contentType: "application/json;charset=UTF-8",
		    success:function(response, status, xhr){
		    	var token = xhr.getResponseHeader("oauth_token");
		    	localStorage["token_secret"] = xhr.getResponseHeader("oauth_token_secret");
		    	//window.location = "http://api.mendeley.com/oauth/authorize/?oauth_token="+token;
		    	chrome.tabs.create({'url' : 'http://api.mendeley.com/oauth/authorize/?oauth_token='+token},function(window) {
		    	   });
		    	
		    	/*chrome.windows.create({'url': 'http://api.mendeley.com/oauth/authorize/?oauth_token='+token, 'type': 'detached_panel'}, function(window) {
		    	   });*/
		    }
		});
}


function logout(){
	localStorage.removeItem("privacy_email");
	localStorage.removeItem("username");
	localStorage.removeItem("token_secret");
	localStorage.removeItem("recommend");
	localStorage.removeItem("user_id");
	localStorage.removeItem("recommendation_query");
	$('#username').html("Good Bye !");
	$('#logout_btn').hide();
	$('#sign_in').show();
	$('#mendeley_btn').hide();
	localStorage.removeItem("mendeley_enabled");
}


function username() {
	if(localStorage["username"] != undefined){
		$('#username').html("Welcome "+localStorage["username"]);
		$('#logout_btn').show();
		$('#sign_in').hide();
		$('#mendeley_btn').show();
		if (localStorage["env"] == "home"){
			$('#home').addClass("active");
		}
		else{
			$('#work').addClass("active");
		}
	}
	
	if ( localStorage["mendeley_enabled"]!= undefined && localStorage["mendeley_enabled"]=='true'){
		
		$('#mendeley_btn').attr('value', 'Synchronize Mendeley');
		
	}
	
	document.getElementById("logout_btn").addEventListener('click',logout);
	document.getElementById("mendeley_btn").addEventListener('click',oauthMendeley);
	
}

function recommend() {
	chrome.browserAction.setBadgeText({text:""});
	var recommendation = localStorage["recommend"];
	document.getElementById('recommend').innerHTML = recommendation;
	var recommendation_terms = localStorage[""]
}

document.addEventListener('DOMContentLoaded', function () {
  username();
  recommend();
});

if (localStorage["env"] == "work"){
	$("#workButton").addClass("active");
	$("#displayed_env").text("Environnement is set to : work");
}
else{
	$("#homeButton").addClass("active");
	$("#displayed_env").text("Environnement is set to : home");
}

$("#workButton").on("click",function(){
		localStorage["env"]="work";
		$("#displayed_env").text("Environnement is set to : work");
	});
$("#homeButton").on("click",function(){
		localStorage["env"]="home";
		$("#displayed_env").text("Environnement is set to : home");
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