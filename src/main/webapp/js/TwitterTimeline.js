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
            		console.log(timeline);
            		var $html = app.render("tmpl-TwitterTimeline", {timeline : timeline});
            		
                    var $e = $($html);
                    return $e;
            	});
            },
            postDisplay:function (data, config) {
            	
            },
            
            events:{
            	"click;.retweet" : function(event) {
            		var tweet_id = $(event.target).closest(".tweet-container").attr("tweet_id");
            		app.twitterApi.retweet({tweet_id : tweet_id}).pipe(function(data) {
            			console.log("excute retweet");
            			console.log(data);
            		})
            	}
            }
            
        });
    })(jQuery);


})();
