;(function() {

	/**
	 * View: TwitterScreen
	 *
	 */
    (function ($) {
        brite.registerView("TwitterScreen",  {parent:".MainScreen-main", emptyParent:true}, {
            create:function (data, config) {
            	return $.when(app.twitterApi.getUserInfo(),app.twitterApi.getUserTimeline()).pipe(function(data,timeline) {
        			var $html = app.render("tmpl-TwitterScreen", {user : data.result, timeline : JSON.parse(timeline.result)});
                    var $e = $($html);
                    return $e;
            	});
            },
            postDisplay:function (data, config) {
            	
            },
            
            events:{
            	"click;.nav-tabs li" : function(event) {
            		var $tabcontent = $(".tab-content");
            		$(".nav-tabs li").removeClass("active");
            		$(event.target).closest("li").addClass("active");
            		if($(event.target).closest("li").hasClass("profile")) {
            			brite.display("TwitterScreen");
            		}
            		if($(event.target).closest("li").hasClass("timeline")) {
            			brite.display("TwitterTimeline", $tabcontent);
            		}
            		if($(event.target).closest("li").hasClass("tweets")) {
            			brite.display("TwitterTweet", $tabcontent);
            		}
            		if($(event.target).closest("li").hasClass("directMsg")) {
            			brite.display("TwitterDirectMsg", $tabcontent);
            		}
            		
            	},
            	
            	"click;.go" : function() {
            		var status = $(".status").val();
            		if(status.length > 0 ) {
            			app.twitterApi.postStatus({status : status}).pipe(function(data) {
            				alert("Send successfully!");
            			})
            		}
            	},
            	
            	"click;.destroy" : function(event) {
            		var tweet_id = $(event.target).closest(".tweet-container").attr("tweet_id");
            		app.twitterApi.destroyTweet({tweet_id : tweet_id}).pipe(function(data) {
            			console.log("excute destroy");
            			console.log(data);
            			//brite.display("TwitterScreen");
            		})
            	}
            }
            
        });
    })(jQuery);


})();
