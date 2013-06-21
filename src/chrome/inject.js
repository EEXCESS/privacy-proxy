/******************************************************
*   This script add users's context to the database   *
******************************************************/


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
*  This function takes the Json (string format) and send it to the proxy
*/

function send_context(contextJson,index,type){
	var url = "http://localhost:12564/api/v0/"+index+"/"+type;
	var method = 'POST';
	var postData = contextJson;
	var async = true;
	var request = new XMLHttpRequest();
	
	request.open(method, url, async);	
	request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	request.send(contextJson);	
}


/*
*    This function create a string readable by elasticsearch in Json
*/

function collect_localStorage() {
	
	chrome.extension.sendRequest({method: "getLocalStorage", key: "privacy_email"}, function(response) {
		get_context(response.data);
		
		
	});
}

function get_context(user_email) {	
	
	// if the user is admin, it only adds data to the database 
	if (user_email=='admin') {
		var content = (document.getElementsByTagName("body")[0].innerText).replace(/\n/g,' '); //suppression des retours charriots
		content = content.replace(/\t/g,'   ');
		content = content.replace(/\"/g,'\'');
	
		
		var myJson = "{\
		\"url\": \""+window.location.protocol+"//"+window.location.hostname+window.location.pathname+"\",\
		\"content\": \""+content+"\"\
		}";send_context(myJson,"recommend","document");
	}
	//if he's not admin, the normal function is executed
	else 
		{
		var date;
		date = date_heure();
		var myJson = "{\
		\"user\": {\
		\"email\":\""+user_email+"\"\
		},\
		\"temporal\": \""+date+"\",\
		\"document\":{ \
		\"url\": \""+window.location.protocol+"//"+window.location.hostname+window.location.pathname+"\",\
		\"title\": \""+document.title+"\"\
		}\
		}";send_context(myJson,"privacy","trace");
	}
	
}

/*
*    On each new page, this function saves the new context
*/

window.onload = collect_localStorage;

/*
First code to test the plugin. To remove ?

var body = document.getElementsByTagName("body")[0];


var content = document.createElement("iframe");
content.setAttribute("style","width: 600px; height: 600px; position: fixed; top: 0; right: 0");
content.setAttribute("src","http://www.wikipedia.com");
content.setAttribute("id","EEXCESS_frame");
body.appendChild(content);
*/
