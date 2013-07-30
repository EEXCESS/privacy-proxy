function initSandbox(){
	var recommendation = localStorage["recommend"];
	$('#results').html(recommendation);
	var recommendation_query = localStorage["recommendation_query"];
	$('#recommendation_query').html(recommendation_query);
};