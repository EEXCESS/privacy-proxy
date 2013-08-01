// Some general UI pack related JS
// Extend JS String with repeat method
String.prototype.repeat = function(num) {
    return new Array(num + 1).join(this);
};

(function($) {

  // Add segments to a slider
  $.fn.addSliderSegments = function (amount,content) {
	  for(var i=0;i<amount;i++){
			if (i == (amount-1)) {
				var segment = "<div class='ui-slider-segment' id='segment-"+i+"' style='margin-left: 3px;'><div class='tooltip top slider-tip' style='display: none'><div class='tooltip-arrow advice' style='margin-left: -52px'></div><div class='tooltip-inner advice' style='margin-left: 85px'>"+content[amount-i-1]+"</div></div></div>";
			}
			else {
				var segmentGap = (100 - 5) / (amount - 1) + "%";
				var margin = 98.5 - i*(100 - 5) / (amount - 1);
				if(i == 0){
					var segment = "<div class='ui-slider-segment' id='segment-"+i+"' style='margin-left: "+segmentGap+";'><div class='tooltip top slider-tip' style='margin-left:"+margin+"%; display:none;'><div class='tooltip-arrow advice' style='margin-left: 20px;'></div><div class='tooltip-inner advice' style='margin-left: -60px;'>"+content[amount-1-i]+"</div></div></div>";
				}
				else{
					var segment = "<div class='ui-slider-segment' id='segment-"+i+"' style='margin-left: "+segmentGap+";'><div class='tooltip top slider-tip' style='margin-left:"+margin+"%; display: none;'><div class='tooltip-arrow advice'></div><div class='tooltip-inner advice'>"+content[amount - 1 - i]+"</div></div></div>";
				};
			};
			$(this).prepend(segment);
	  }
  };

  $(function() {
  
    // Todo list
    $(".todo li").click(function() {
        $(this).toggleClass("todo-done");
    });

    // Custom Select
    $("select[name='herolist']").selectpicker({style: 'btn-primary', menuStyle: 'dropdown-inverse'});

    // Tooltips
    $("[data-toggle=tooltip]").tooltip("show");

    // Tags Input
    $(".tagsinput").tagsInput();

    // jQuery UI Sliders
    var $slider = $("#slider");
    if ($slider.length) {
      $slider.slider({
        min: 1,
        max: 5,
        value: 2,
        orientation: "horizontal",
        range: "min"
      }).addSliderSegments($slider.slider("option").max);
    }

    // Placeholders for input/textarea
    $("input, textarea").placeholder();

    // Make pagination demo work
    $(".pagination a").on('click', function() {
      $(this).parent().siblings("li").removeClass("active").end().addClass("active");
    });

    $(".btn-group a").on('click', function() {
      $(this).siblings().removeClass("active").end().addClass("active");
    });

    // Disable link clicks to prevent page scrolling
    $('a[href="#fakelink"]').on('click', function (e) {
      e.preventDefault();
    });

    // Switch
    $("[data-toggle='switch']").wrap('<div class="switch" />').parent().bootstrapSwitch();
    
  });
  
})(jQuery);