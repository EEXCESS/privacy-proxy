/************************************************************************
*    Script to collect the traces kept about the user (only his data)   *
************************************************************************/

/*
* This function gets the user's data and put them into traces.html
*/

function traces(user_email) {
	// a query is send to the elasticsearch database
	var url = "http://localhost:11564/user/traces";
	var method = 'POST';
	var async = false;
	var request = new XMLHttpRequest();
	
	// The query can be made on the proxy (like now) or here (like on comments)
	
	//var elasticSearchQuery = "{\"query\":{\"bool\":{\"must\":[{\"text\":{\"trace.user.email\":\""+user_email+"\"}}]}}}";
	request.open(method, url, async);	
	request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	//request.send(elasticSearchQuery);
	request.send(user_email);
	
	
	// useful data are collected from the response
	var traces = request.responseText;
	var tracesJson = JSON.parse(traces);
	var sources = tracesJson["hits"].hits;
	var nbTraces = tracesJson["hits"].total;
	
	
	//  Add the datas as a list to traces.html
	document.getElementById('nbTraces').innerHTML = nbTraces + " traces dans l'historique";
	if(nbTraces>50) nbTraces = 50;                    // only the first 50 results are displayed
	
	for(var i=0;i<nbTraces;i++) {                                             
		var content = sources[i];
		var newLink_li = document.createElement('li');
		
		var id_li = 'list'+i;
		
		newLink_li.id = id_li;
		
		document.getElementById('list_trace').appendChild(newLink_li);
		
		var newLink = document.createElement('h4');
		var newLinkText = document.createTextNode( '  ('+content["_source"].temporal+')');
		
		var link = document.createElement('a');
		var linkText = document.createTextNode(content["_source"].document.title);  
		 
		newLink.id    = 'history';
		newLink.class = 'todo-name';
		
		link.id = 'traces';
		link.href = content["_source"].document.url;
		 	 
		link.appendChild(linkText);
	 	document.getElementById(id_li).appendChild(link);
	 	
		newLink.appendChild(newLinkText);  	 
		document.getElementById(id_li).appendChild(newLink);
		

	}
}

/*
*    This function collect the user email from the localStorage (via background.js)
*    More informations may be collected later
*/

document.addEventListener('DOMContentLoaded', function () {

  chrome.extension.sendRequest({method: "getLocalStorage", key: "privacy_email"}, function(response) {
	traces(response.data);
  });

});