;
(function ($) {
    brite.registerView("LinkedInGroupPostComment",{emptyParent:true}, {
        create: function (data, config) {
            return app.render("tmpl-LinkedInGroupPostComment", {data:data});
        },

        postDisplay: function (data, config) {
        	
        },

        events: {
			"click;.btnGroup":function(e){
        		var param = {}
        		param.groupId = $(".Operator").attr("data-groupId");
        		app.linkedInApi.groupDetails(param).done(function (result) {
	                    if(result.success === true){
	                    	 brite.display("LinkedInGroupDetails", ".LinkedInScreen-content", {result:result.result});
	                    }else{
	                    	alert("can't get the details of group!");
	                    }
	                });
			},
        	"click;.btnPreviousPage":function(e){
        		var param = {}
        		var groupId = $(".GroupId").attr("data-groupId");
        		param.postId = $(".Operator").attr("data-postId");
        		param.start = $(".Operator").attr("data-start")-1;
        		param.count = $(".Operator").attr("data-count");
        		app.linkedInApi.groupPostComments(param).done(function (result) {
             		 $("div.post-container").empty();
              		 brite.display("LinkedInGroupPostComment",$("div.post-container"),{groupId:groupId,postId:param.postId,result:result});
                  });
			},
        	"click;.btnNextPage":function(e){
        		var param = {}
        		param.groupId = $(".Operator").attr("data-groupId");
        		param.start = $(".Operator").attr("data-start")-0+1;
        		param.count = $(".Operator").attr("data-count");
        		app.linkedInApi.groupPostComments(param).done(function (result) {
             		 $("div.post-container").empty();
              		 brite.display("LinkedInGroupPostComment",$("div.post-container"),{groupId:groupId,postId:param.postId,result:result});
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