/***************************************************************
*     Background script to access to localStorage at anytime   *
***************************************************************/
var version = "1.00";
console.log("EEXCESS privacy plugin version "+version);

var traceID = new Array();

/*
$.ajax("http://habegger.fr/",{
	success: function(response) { alert(response); },
	error: function(response) { alert("Oooops"); }
});
*/


chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
	console.log("processing brower request");
    if (request.method == "getLocalStorage") {
      sendResponse({data: localStorage[request.key]});
    } else if(request.method == "setDocumentContext") {
    	var url = request.url;
    	var title = request.title;
    	send_context(url, title, sender.tab.id);
    } else if (request.method == "updateDocumentContext") {
    	updateContext(sender.tab.id);
    } else {  
      sendResponse({});
    }
});


/*
*    This function collect the date at this format: YYYY-MM-DDTHH:mmZ (ISO_8601)
*/

function date_heure()
{
        date = new Date();
        year = date.getFullYear();
        month = date.getMonth()+1;
        if(month<10)
        {
                month = "0"+month;
        }
        day = date.getDate();
        if(day<10)
        {
                day = "0"+day;
        }
        hour = date.getHours();
        if(hour<10)
        {
                hour = "0"+hour;
        }
        minute = date.getMinutes();
        if(minute<10)
        {
                minute = "0"+minute;
        }
        result = year + '-' + month +'-'+day+'T'+hour+':'+minute+'Z';
        return result;
}


/*
*  This function makes the Json (string format) and send it to the proxy
*/

function send_context(traceUrl, title, tabID){
	console.log("Putting document context");
	var date = date_heure();
	var trace = {
		user: {
			email: localStorage["privacy_email"]
		},
		temporal: {
			begin: date
		},
		document: {
			url: traceUrl,
			title: title
		}
	};
	
	var traceJSON = JSON.stringify(trace);
	console.log("Context: "+traceJSON);
	$.ajax({
	   url: "http://localhost:12564/api/v0/privacy/trace",
	   type: "POST",
	   contentType: "application/json;charset=UTF-8",
	   data: traceJSON,
	   success: function(response) {
			console.log(response);
			console.log(response["_id"]);
			trace["id"] = response["_id"];
			console.log(trace);
			traceID[tabID] = trace;
			
	   }
	});
}

function updateContext(tabID) {
	console.log("Putting document context");
	var date = date_heure();
	var trace = traceID[tabID];
	console.log(trace);
	var elasticID = trace.id;
	delete(trace.id);
	
	trace.temporal.end = date_heure();
	
	var traceJSON = JSON.stringify(trace);
	console.log("Context: "+traceJSON);
	$.ajax({
	   url: "http://localhost:12564/api/v0/privacy/trace/",
	   type: "POST",
	   contentType: "application/json;charset=UTF-8",
	   data: traceJSON,
	   beforeSend: function(request){request.setRequestHeader("elasticId", elasticID);},
	   success: function(response) {
			console.log(response);
			unset(trace[tabID]);
			
	   }
	});
}