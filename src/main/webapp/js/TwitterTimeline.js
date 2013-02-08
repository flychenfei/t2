;(function() {

	/**
	 * View: TwitterTimeline
	 *
	 */
    (function ($) {
        brite.registerView("TwitterTimeline",  {emptyParent:true}, {
            create:function (data, config) {
            	return app.twitterApi.getTimeline().pipe(function(data) {
            		return app.twitterApi.getSuggestions().pipe(function(suggest) {
            			return app.twitterApi.getMentionTimeline().pipe(function(mention) {
            				var timeline = JSON.parse(data.result);
                			var suggestions = JSON.parse(suggest.result);
                			var mentions = JSON.parse(mention.result);
                    		var $html = app.render("tmpl-TwitterTimeline", {timeline : timeline, suggestions : suggestions, mentions : mentions});
                            var $e = $($html);
                            return $e;
            			})
            			
            			
            		})
            		
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
            	},
            	
            	"click;.favorite" : function(event) {
            		var tweet_id = $(event.target).closest(".tweet-container").attr("tweet_id");
            		app.twitterApi.favorite({tweet_id : tweet_id}).pipe(function(data) {
            			console.log("excute favorite");
            			console.log(data);
            		})
            	}
            }
            
        });
    })(jQuery);


})();
