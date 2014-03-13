/******************************************************
*   This script add users's context to the database   *
******************************************************/


/*
*    This function create a string readable by elasticsearch in Json
*/
// sendDocumentContext
function collect_context(event) {
	console.log("Loaded");
    var documentContext = {
    	method: "documentContext",
    	url : window.location.protocol + window.location.hostname + window.location.pathname,
    	title : document.title,
    	content: extract_words(),
    	event: event
    };
    
    console.log("Sending docContext");
	chrome.extension.sendRequest(documentContext, function(){});
	console.log("docContext sent.");
}


function extract_words() {
	var sWords = document.body.innerText.toLowerCase().trim().replace(/[,;.]/g,'').split(/[\s\/]+/g).sort();
	var iWordsCount = sWords.length; // count w/ duplicates
 
	// array of words to ignore
	var ignore = ['and','the','to','a','of','for','as','i','with','it','is','on','that','this','can','in','be','has','if'];
	ignore = (function(){
		var o = {}; // object prop checking > in array checking
		var iCount = ignore.length;
		for (var i=0;i<iCount;i++){
			o[ignore[i]] = true;
		}
		return o;
	}());
 
	var counts = {}; // object for math
	for (var i=0; i<iWordsCount; i++) {
		var sWord = sWords[i];
		if (!ignore[sWord]) {
			counts[sWord] = counts[sWord] || 0;
			counts[sWord]++;
		}
	}
 
	var arr = []; // an array of objects to return
	for (sWord in counts) {
		arr.push({
			text: sWord,
			frequency: counts[sWord]
		});
	}
 
	// sort array by descending frequency | http://stackoverflow.com/a/8837505
	return arr.sort(function(a,b){
		return (a.frequency > b.frequency) ? -1 : ((a.frequency < b.frequency) ? 1 : 0);
	});
}

chrome.runtime.onMessage.addListener(function(request, sender, sendResponse) {
	if (request.method == "getContext") {
	
		collect_context(request.event);
	}
});


function sendRequest(event) {
	
	var documentRequest = {
		method: "newRequest",
		event: event
	}
	
	chrome.extension.sendRequest(documentRequest, function(){});
}

/*
*    On each new page, this function saves the new context
*/
//chrome.tabs.onActivated.addListener(collect_context("focus"));
window.addEventListener("load",sendRequest("load"));
window.addEventListener("unload",sendRequest("unload"));

