;(function() {

	/**
	 * View: TwitterTimeline
	 *
	 */
    (function ($) {
        brite.registerView("TwitterTimeline",  {emptyParent:true}, {
            create:function (data, config) {
            	return app.twitterApi.getTimeline().pipe(function(data) {
            		var timeline = JSON.parse(data.result);
            		var $html = app.render("tmpl-TwitterTimeline", {timeline : timeline});
            		
                    var $e = $($html);
                    return $e;
            	});
            },
            postDisplay:function (data, config) {
            	
            },
            
            events:{
            	
            }
            
        });
    })(jQuery);


})();
