var url = document.location.href;
var query = url.split('?')[1];

var queryComponents = query.split("&");

var token, verifier;

queryComponents.forEach(function(element){
	var component = element.split("=");
	var compName = component[0];
	var compValue = component[1];
	if(compName == "oauth_token"){
		token = compValue;
	};
	if(compName == "oauth_verifier"){
		verifier = compValue;
	}
});

alert('test:'+token);
$.ajax({
   	url: "http://localhost:11564/oauth/mendeley/connect",
    type: "GET",
    contentType: "application/json;charset=UTF-8",
    beforeSend: function (request)
    {
        request.setRequestHeader("oauth_verifier", verifier);
        request.setRequestHeader("oauth_token", token);
        request.setRequestHeader("oauth_token_secret", localStorage["token_secret"]);
    },
    success:function(response, status, xhr){
    	alert(response.getResponseHeaders());
    }
});