function doEEXCESSChange() {
	//alert('test2');
}


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

function send_context(contextJson){
	var url = "http://localhost:8888/api/v0/privacy/trace";
	var method = 'POST';
	var postData = contextJson;
	var async = true;
	var request = new XMLHttpRequest();
	
	request.open(method, url, async);	
	request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	request.send(contextJson);	
}

function get_context() {
	var context = new Array();
	context["test"] = "{ \"document\": \"Google\"}";
	var date;
	date = date_heure();
	var myJson = "{\
	\"temporal\": \""+date+"\",\
	\"document\":{ \
	\"url\": \""+window.location.protocol+"//"+window.location.hostname+window.location.pathname+"\",\
	\"title\": \""+document.title+"\"\
	}\
	}";

	send_context(myJson);

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
