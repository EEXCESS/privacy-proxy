/***************************************************************
*     Background script to access to localStorage at anytime   *
***************************************************************/
var version = "1.00";
console.log("EEXCESS privacy plugin version "+version);

var uuidUser = localStorage["uuid"];


if (uuidUser == undefined) {
	uuidUser = randomUUID();
	localStorage["uuid"] = uuidUser;
}


/**
 * Create and return a "version 4" RFC-4122 UUID string.
 */
 
function randomUUID() {
  var s = [], itoh = '0123456789ABCDEF';
 
  // Make array of random hex digits. The UUID only has 32 digits in it, but we
  // allocate an extra items to make room for the '-'s we'll be inserting.
  for (var i = 0; i < 36; i++) s[i] = Math.floor(Math.random()*0x10);
 
  // Conform to RFC-4122, section 4.4
  s[14] = 4;  // Set 4 high bits of time_high field to version
  s[19] = (s[19] & 0x3) | 0x8;  // Specify 2 high bits of clock sequence
 
  // Convert to hex chars
  for (var i = 0; i < 36; i++) s[i] = itoh[s[i]];
 
  // Insert '-'s
  s[8] = s[13] = s[18] = s[23] = '-';
 
  return s.join('');
}



var arrayTraceID = new Array();

/*
$.ajax("http://habegger.fr/",{
	success: function(response) { alert(response); },
	error: function(response) { alert("Oooops"); }
});
*/


chrome.extension.onRequest.addListener(function(request, sender, sendResponse) {
	console.log("processing brower request");
    if (request.method == "getPrivacyEmail") {
      sendResponse({data: localStorage["privacy_email"]});
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
        second = date.getSeconds();
        if(second<10)
        {
                second = "0"+second;
        }
        result = year + '-' + month +'-'+day+'T'+hour+':'+minute+':'+second+'Z';
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
		plugin: {
			version: version,
			uuid: localStorage["uuid"]
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
			arrayTraceID[tabID] = trace;
			
	   }
	});
}

function updateContext(tabID) {
	console.log("Putting document context");
	var date = date_heure();
	var trace = arrayTraceID[tabID];
	console.log(trace);
	var headerTraceID = trace.id;
	delete(trace.id);
	
	trace.temporal.end = date_heure();
	
	var traceJSON = JSON.stringify(trace);
	console.log("Context: "+traceJSON);
	$.ajax({
	   url: "http://localhost:12564/api/v0/privacy/trace",
	   type: "POST",
	   contentType: "application/json;charset=UTF-8",
	   data: traceJSON,
	   headers:{"traceId": headerTraceID},
	   success: function(response) {
			console.log(response);
			delete(arrayTraceID[tabID]);			
	   }
	});
}