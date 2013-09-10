/*
document.getElementById("apiBaseUri").addEventListener('focus',function(){
	if(this.value=='API base URI'||this.value==localStorage["API_BASE_URI"])document.getElementById("apiBaseUri").value='';
});
*/
document.getElementById("apiBaseUri").addEventListener('blur',function(){
	if(this.value==''){
		this.value=localStorage["API_BASE_URI"];
	}
});

	$("#apiBaseUri").val(localStorage["API_BASE_URI"]);
	$("#updateURI").live("click",function(){
		localStorage["API_BASE_URI"]=$("#apiBaseUri").val();
		$('#successURIUpdate').text('API base URI updated');
	});