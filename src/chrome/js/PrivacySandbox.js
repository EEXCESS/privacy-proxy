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

function findNearest(values, includeLeft, includeRight, value) {
    var nearest = null;
    var diff = null;
    for (var i = 0; i < values.length; i++) {
        if ((includeLeft && values[i] <= value) || (includeRight && values[i] >= value)) {
            var newDiff = Math.abs(value - values[i]);
            if (diff == null || newDiff < diff) {
                nearest = values[i];
                diff = newDiff;
            }
        }
    }
    return nearest;
}

$.fn.addSliderSegmentsUser = function (amount, values) {	
	var range = values[values.length-1]-values[0];
	var gapSum = 0;
	var tracesJson = JSON.parse(localStorage["traces"]);
	var traces = tracesJson["hits"].hits;
	//var arrowHeight = $(".tooltip-arrow").height();
	
	
	var sliderLength = $("#sliderUserTrace").width(); // length in px of the full slider
	var arrowHeight = 5;
	var sliderPosition = $("#sliderUserTrace").position();
	for(var i=amount-1;i>=0;i--){
		var segmentMargin = i==0 ? 0 : (100*(values[i]-values[i-1])/range);
		
		var arrowPositionTop = sliderPosition.top-10;//+(values[i]*sliderLength)/values[amount-1];
		var arrowPositionLeft = sliderPosition.left+(values[i]*sliderLength)/values[amount-1];
		var tooltipPosition = 35 + (values[i]*sliderLength)/values[amount-1];
		var arrowPosition = 0;
		//var tooltipPosition = 0 ;
		gapSum += segmentMargin;
		//var displayedTrace = "toto";
		var displayedTrace = JSON.stringify(traces[amount-1-i]["_source"]["document"].title);
		displayedTrace += "\n";
		displayedTrace += JSON.stringify(traces[amount-1-i]["_source"]["temporal"].begin);
		var segment = "<div class='ui-slider-segment' id='segment-"+i+"' data-sum='"+gapSum+"' style='margin-left: "+segmentMargin+"%;'>"+
		                 "<div class='tooltip top slider-tip' style='display: none'>"+
		                   //"<div class='tooltip-arrow advice' style='margin-left: "+arrowPosition+"px'></div>"+
		                   "<div class='tooltip-arrow advice' style='position:fixed; top:"+arrowPositionTop+";left:"+arrowPositionLeft+";'></div>"+
		                   "<div class='tooltip-inner advice' style='margin-left: "+tooltipPosition+"px; margin-bottom:"+arrowHeight+"px;'>"+displayedTrace+"</div>"+
		                 "</div>"+
		               "</div>";
		//alert(JSON.stringify(traces[i]));
		$(this).prepend(segment);
	}
};

function initSliderUser(){
	var trace = JSON.parse(localStorage["traces"]).hits.hits;
	
	for (i=0;i<10;i++){
		trace[i] = trace[i]["_source"];
	};
	
	var begin = new Date(trace[9].temporal.begin);
	var beginMilli = begin.getTime();
	var end = new Date(trace[0].temporal.begin);
	var endMilli = end.getTime();
	var range = endMilli - beginMilli;
	
	var previousMilli = 0;
  
	
	/* 3 next loops are here to be sure the values will not be :
	 *     - too close
	 *     - out of range
	 *     -too close again
	 *     
	 *     We have to check it 2 times because the second loop put them really close together
	 */
    var values = [0];
	for(i=8;i>=0;i--){
		var current = new Date(trace[i].temporal.begin);
		var currentMilli = current.getTime();
		var diffMilli = currentMilli-beginMilli;
		var test = (((diffMilli-previousMilli)/range)*25);
		if (test < 1) {
			diffMilli = range/25 + previousMilli;
		}
		values.push(diffMilli);
		previousMilli = diffMilli;
	};
		
	for(i=0;i<10;i++){
		if(values[i] > range){
			values[i] = range - ( 9 - i );
		}
	}
	
	var valuesIncorrect = true;
	var j = 9;
	
	while(valuesIncorrect){
		var diff = values[j] - values[j-1];
		if((diff/range)*25 > 1){
			valuesIncorrect = false;
		}
		else{
			values[j-1] = values[j] - (range/25);
		}
		j--;
		if(j<0){
			valuesIncorrect = false;
		}
	}
	
	var slider = $("#sliderUserTrace").slider({
		min: 0,
		max: range,
		values: [range],
		slide: function(event, ui) {
		    var includeLeft = event.keyCode != $.ui.keyCode.RIGHT;
		    var includeRight = event.keyCode != $.ui.keyCode.LEFT;
		    var value = findNearest(values, includeLeft, includeRight, ui.value);
		    slider.slider('values', 0, value);
		    updateRecommendation();
		    //$(this).find('.ui-slider-handle').find(".tooltip-inner").html(ui.value);
		    return false;
		    
		},
		change: function(event, ui) { 
		}
	});
	$("#sliderUserTrace").addSliderSegmentsUser(10, values);
	
	
}