(function() {
	 brite.registerView("TwitterTweet",  {emptyParent:true}, {
         create:function (data, config) {
    		var $html = app.render("tmpl-TwitterTweet");
            var $e = $($html);
            return $e;
         },
         postDisplay:function (data, config) {
         	
         },
         
         events:{
        	 
        	 "click;.getRetweetById" : function() {
         		var tweet_id = $(".tweet_id").val();
         		if(tweet_id.length > 0 ) {
         			console.log("tweet_id" + tweet_id);
         			app.twitterApi.getRetweetById({tweet_id : tweet_id}).pipe(function(data) {
         				var tweets = JSON.parse(data.result);
         				var $html = app.render("tweets-template", {tweets : tweets});
         	            var $e = $($html);
         	            $(".tweet-list-container").html($e);
         			})
         		}
         	}
         
         }
         
     });
})();