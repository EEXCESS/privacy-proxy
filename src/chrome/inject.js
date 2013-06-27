/******************************************************
*   This script add users's context to the database   *
******************************************************/


/*
*    This function create a string readable by elasticsearch in Json
*/

// sendDocumentContext
function collect_context() {
	console.log("Loaded");
    var documentContext = {
    	method: "setDocumentContext",
    	url : window.location.protocol + window.location.hostname + window.location.pathname,
    	title : document.title
    };
    
    console.log("Sending docContext");
	chrome.extension.sendRequest(documentContext, function(){});
	console.log("docContext sent.");
}

function sendUpdateContext() {
	var documentContext = {
    	method: "updateDocumentContext"
    };
    
    console.log("Sending docContext");
	chrome.extension.sendRequest(documentContext, function(){});
	console.log("docContext sent.");
}

/*
*    On each new page, this function saves the new context
*/
window.addEventListener("load",collect_context);
window.addEventListener("unload",sendUpdateContext);
