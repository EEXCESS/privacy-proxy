function doEEXCESSChange() {
	chrome.extension.sendMessage(this.value);
}

document.getElementById("gbqfq").addEventListener("change",doEEXCESSChange);
var body = document.getElementsByTagName("body")[0];

/*
var content = document.createElement("iframe");
content.setAttribute("style","width: 600px; height: 600px; position: fixed; top: 0; right: 0");
content.setAttribute("src","http://www.wikipedia.com");
content.setAttribute("id","EEXCESS_frame");
body.appendChild(content);
*/
