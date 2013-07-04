/************************************************************************
*    Script to collect the traces kept about the user (only his data)   *
************************************************************************/

/*
* This function gets the user's data and put them into traces.html
*/

function parseUrl(url) {
	url = url.split(':')[1];
	host = url.split('/')[0];
	host = 'http://'+host;
	return(host);
}

function getDelay(begin, end){
	
	if (end == undefined) {
		end=begin;
	}
	
	begin = begin.split('Z')[0];
	end = end.split('Z')[0];
	
	var timeBegin = begin.split('T')[1];
	var dayBegin = begin.split('T')[0];
	var timeEnd = end.split('T')[1];
	var dayEnd = end.split('T')[0];
	
	var delayYear = dayEnd.split('-')[0]-dayBegin.split('-')[0];
	var delayMonth = dayEnd.split('-')[1]-dayBegin.split('-')[1];
	var delayDay = dayEnd.split('-')[2]-dayBegin.split('-')[2];
	
	var delayHour = timeEnd.split(':')[0]-timeBegin.split(':')[0];
	var delayMinute = timeEnd.split(':')[1]-timeBegin.split(':')[1];
	var delaySecond = timeEnd.split(':')[2]-timeBegin.split(':')[2];
	
	//alert(delayYear+ '-'+delayMonth+'-'+delayDay+'T'+delayHour+':'+delayMinute+':'+delaySecond);
	
	var result = "Time spent: "
	
	if (delayYear != 0) {
		result = result + delayYear + " year";
		}
	else if(delayMonth != 0){
		result = result + delayMonth + " month";
	}
	else if(delayDay != 0){
		result = result + delayDay + " day";
	}
	else if (delayHour != 0){
		if(delayHour ==1 && delayMinute <=10){
			result = result + (delayMinute+60) + " minutes";
		}
		else {
			result = result + delayHour + " hour";
			if (delayHour!=1) result= result +"s";
		}
	}
	else if (delayMinute !=0){
		if(delayMinute ==1 && delaySecond <=40){
			result = result + (delaySecond+60) + " seconds";
		}
		else {
			result = result + delayMinute + " minute";
			if (delayMinute!=1) result= result +"s";
		}
		
	}
	else {
		result = result + delaySecond + " second";
		if (delaySecond!=1) result= result +"s";
	}
	
	return result;
}

function parseDate(date){
	if(date == undefined) {
		return('Error in date format');
	}
	else{
		date = date.split('Z')[0];  //remove the Z character
		var dateParts = date.split('T');
		var dayPart = dateParts[0];
		var timePart = dateParts[1];
		
		var daySplit = dayPart.split('-');
		var year = Number(daySplit[0]);
		var month = Number(daySplit[1]) - 1;
		var day = Number(daySplit[2]);
		
		var monthsTab = new Array("January","February","March","April","May","June","July","August","September","October","November","December");
		month = monthsTab[month];
		
		dateFormated = day + " " + month + " " + year + " at " + timePart;
		return(dateFormated);
	}
}

function traces(user_id,email) {
	// a query is send to the elasticsearch database
	var url = "http://localhost:11564/user/traces";
	var method = 'POST';
	var async = false;
	var request = new XMLHttpRequest();
	
	var body = '';
	if(user_id != '') {
		body = body + "{\"term\":{\"trace.plugin.uuid\": \""+user_id+"\"}}";
	}
	if (user_id != '' && email!='') {
		body = body + ",";
	}
	if(email != '') {
		body = body + "{\"term\":{\"trace.user.email\": \""+email+"\"}}";
	}	
	
	request.open(method, url, async);	
	request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	request.send(body);
	
	
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
		var clone = $('#template').clone(true);
		
		clone.css("display","inherit");
		
		clone.appendTo('#list_trace');
		
		var beginDate = parseDate(content["_source"].temporal.begin);
		var endDate = parseDate(content["_source"].temporal.end);
		
		$(clone).find('.historyEnd').html('('+endDate+')');
		$(clone).find('.delay').html(getDelay(content["_source"].temporal.begin,content["_source"].temporal.end))
		
		$(clone).find('.historyBegin').html('('+beginDate+')');
		
		$(clone).find('.traces').html(content["_source"].document.title);
		$(clone).find('.traces').attr("href",content["_source"].document.url);
		
		var parsedUrl = parseUrl(content["_source"].document.url);
		var faviconUrl = parsedUrl+"/favicon.ico";
		$(clone).find('.host').html(parsedUrl);
		$(clone).find('.favicon').attr("src",faviconUrl);
		
		if (content["_source"].user.email != undefined){
			$(clone).find('.userEmail').html(content["_source"].user.email);
			var gravatar = "http://www.gravatar.com/avatar/"+MD5(content["_source"].user.email);
			$(clone).find('.userGravatar').attr('src',gravatar);
			$(clone).find('.userInformations').css("display","inline")
		}
		
		
		
		$(clone).find('.jsonDetail').JSONView(content["_source"]);

	}
}


/*
*    This function collect the user email from the localStorage (via background.js)
*    More informations may be collected later
*/

function doReloadTraces(){

	for(var i=0;i<50;i++){
		var id = "list"+i;
		$('#'+id).remove();
	}
	
	if($(this).is(':checked')){
		if($('#inputPluginTraces').is(':checked')) {
			chrome.extension.sendRequest({method: "getLocalStorage", key: "uuid"}, function(response) {
				var uuidPlugin = response.data;
				chrome.extension.sendRequest({method: "getLocalStorage", key: "privacy_email"}, function(response) {
					traces(uuidPlugin,response.data);
 				 });
  			});
		}
		else {
			chrome.extension.sendRequest({method: "getLocalStorage", key: "privacy_email"}, function(response) {
				traces('',response.data);
 			 });
		}
	}
	else {
		if($('#inputPluginTraces').is(':checked')) {
			chrome.extension.sendRequest({method: "getLocalStorage", key: "uuid"}, function(response) {
				traces(response.data,'');
  			});
		}
		else {
			traces('','');
 		}
		
	}
		
}

function doLoadTracesOther(){

	for(var i=0;i<50;i++){
		var id = "list"+i;
		$('#'+id).remove();
	}
	
	if($(this).is(':checked')){
		if($('#inputUserTraces').is(':checked')) {
			chrome.extension.sendRequest({method: "getLocalStorage", key: "uuid"}, function(response) {
				var uuidPlugin = response.data;
				chrome.extension.sendRequest({method: "getLocalStorage", key: "privacy_email"}, function(response) {
					traces(uuidPlugin,response.data);
 				 });
  			});
		}
		else {
			chrome.extension.sendRequest({method: "getLocalStorage", key: "uuid"}, function(response) {
				traces(response.data,'');
 			 });
		}
	}
	else {
		if($('#inputUserTraces').is(':checked')) {
			chrome.extension.sendRequest({method: "getLocalStorage", key: "privacy_email"}, function(response) {
				traces('',response.data);
  			});
		}
		else {
			traces('','');
 		}
		
	}
		
}

function doToggleDetails() {
	var d = $(this).closest('.details').find('.jsonDetail');
	d.toggle("drop");
	/*if(d.is(":visible")) {
		d.effect("drop","down");
	} else {
		d.toggle("drop");	
	}*/
}

$('#inputUserTraces').live("change",doReloadTraces);
$('#inputPluginTraces').live("change",doReloadTraces);
$('.detailHandle').live("click",doToggleDetails);

$(document).ready(function () {
  chrome.extension.sendRequest({method: "getLocalStorage", key: "uuid"}, function(response) {
	traces(response.data,'');
  });
  
});