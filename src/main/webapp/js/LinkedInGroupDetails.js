;
(function ($) {
    brite.registerView("LinkedInGroupDetails",{emptyParent:true}, {
        create: function (data, config) {
            return app.render("tmpl-LinkedInGroupDetails", {data:data});
        },

        postDisplay: function (data, config) {
        	
        },

        events: {
        	"click;.btnPrevious":function(e){
        		var param = {}
        		param.currentId = $(e.target).attr("data-currentId");
        		brite.display("GoogleDriveFiles",".GoogleScreen-content",{
					results: function(){
					    return app.googleDriveApi.previousList(param);
				    }
				});
			}
        },
        docEvents: {
           
        },
        daoEvents: {
        }
    });

    function openFolder(){
    	 var param = this.param || {};
    	 brite.display("GoogleDriveFiles",".GoogleScreen-content",{
				results: function(){
				    return app.googleDriveApi.childList(param);
			    }
			});
    }
    
})(jQuery);