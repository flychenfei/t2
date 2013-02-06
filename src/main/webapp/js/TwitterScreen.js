;(function() {

	/**
	 * View: TwitterScreen
	 *
	 */
    (function ($) {
        brite.registerView("TwitterScreen",  {parent:".MainScreen-main", emptyParent:true}, {
            create:function (data, config) {
            	return app.twitterApi.getUserInfo().pipe(function(data) {
            		var $html = app.render("tmpl-TwitterScreen", {user : data.result});
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
            	},
            	
            	"click;.go" : function() {
            		var status = $(".status").val();
            		if(status.length > 0 ) {
            			app.twitterApi.postStatus({status : status}).pipe(function(data) {
            				alert("Send successfully!");
            			})
            		}
            	}
            }
            
        });
    })(jQuery);


})();
