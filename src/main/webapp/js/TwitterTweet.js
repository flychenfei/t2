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
         			app.twitterApi.getRetweetById({tweet_id : tweet_id}).pipe(function(data) {
         				var tweets = JSON.parse(data.result);
         				var $html = app.render("tweets-template", {tweets : tweets});
         	            var $e = $($html);
         	            $(".tweet-list-container").html($e);
         			})
         		}
         	},
         	
         	"click;.getStatusById" : function() {
         		var status_id = $(".status_id").val();
         		if(status_id.length > 0 ) {
         			app.twitterApi.getStatusById({status_id : status_id}).pipe(function(data) {
         				var status = data.result;
         				console.log("this is in the method getRetweet");
         				console.log(status);
         				var $html = app.render("status-template", status);
         	            var $e = $($html);
         	            $(".status-content-container").html($e);
         			})
         		}
         	},
         	
         	"click;.searchTweets" : function() {
         		var query = $(".query").val();
         		if(query.length > 0 ) {
         			app.twitterApi.searchTweets({query : query}).pipe(function(data) {
         				var tweets = data.result.statuses;
         				var $html = app.render("tweets-template", {tweets : tweets});
         	            var $e = $($html);
         	            $(".search-result-container").html($e);
         			})
         		}
         	}
         
         }
         
     });
})();