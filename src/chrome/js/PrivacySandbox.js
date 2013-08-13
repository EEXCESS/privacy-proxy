function initSandbox(){
	var maxScore = 10;
	var recommendation = localStorage["recommend"];
	$('#results').html(recommendation);
	var recommendation_query = localStorage["recommendation_query"];
	
	var svgQuery = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" height="190"><g transform=translate(100)>';
	{
		var score = 3.5;
		var term  = "Toto";
		
		var barWidth = 250 * score / maxScore;
		
		svgQuery += '<text x="-10" y="10" text-anchor="end" style="fill:black; font-size:14;font-weight:bold;">'+term+'</text>';
		svgQuery += '<rect x="+10" y="0" width="'+barWidth+'" height="10" style="fill: red" />';
		svgQuery += '<text x="'+(barWidth+20)+'" y="10" style="fill:black; font-size:10">'+score+'</text>';
	}
	svgQuery += '</g></svg>';
	$('#recommendation_query').html(svgQuery);
};