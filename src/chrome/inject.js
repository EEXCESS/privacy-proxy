function doEEXCESSChange() {
	//alert('test2');
}

function send_context(contextJson){
	var url = "http://localhost:8888/api/v0/eexcess/trace";
	var method = "POST";
	var postData = contextJson;
	var async = true;

	var request = new XMLHttpRequest();
	
	request.open(method, url, async);
	request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	request.send(postData);
	
	
	
}


function get_context() {
	//alert(window.location.protocol+'//'+window.location.hostname+window.location.pathname);
	var context = new Array();
	context["test"] = "lbalba";
	
	send_context(context);

}






window.onload = get_context;
//document.getElementById("gbqfq").addEventListener('change',doEEXCESSChange);
var body = document.getElementsByTagName("body")[0];

/*
var content = document.createElement("iframe");
content.setAttribute("style","width: 600px; height: 600px; position: fixed; top: 0; right: 0");
content.setAttribute("src","http://www.wikipedia.com");
content.setAttribute("id","EEXCESS_frame");
body.appendChild(content);
*/
