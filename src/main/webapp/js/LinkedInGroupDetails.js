;
(function ($) {
    brite.registerView("LinkedInGroupDetails",{emptyParent:true}, {
        create: function (data, config) {
            return app.render("tmpl-LinkedInGroupDetails", {data:data});
        },

        postDisplay: function (data, config) {
        	var param = {};
        	param.groupId = $(".GroupId").attr("data-groupId");
        	app.linkedInApi.groupPost(param).done(function (result) {
       		 $("div.post-container").empty();
        		 brite.display("LinkedInGroupPost",$("div.post-container"),{result:result});
            });
        },

        events: {
        	"click;.btnPreviousPage":function(e){
        		var param = {}
        		param.groupId = $(".GroupId").attr("data-groupId");
        		param.start = $(".GroupPostsHead").attr("data-start")-1;
        		param.count = $(".GroupPostsHead").attr("data-count");
        		app.linkedInApi.groupPost(param).done(function (result) {
              		 $("div.post-container").empty();
               		 brite.display("LinkedInGroupPost",$("div.post-container"),{result:result});
                   });
			},
        	"click;.btnNextPage":function(e){
        		var param = {}
        		param.groupId = $(".GroupId").attr("data-groupId");
        		param.start = $(".GroupPostsHead").attr("data-start")-0+1;
        		param.count = $(".GroupPostsHead").attr("data-count");
        		app.linkedInApi.groupPost(param).done(function (result) {
              		 $("div.post-container").empty();
               		 brite.display("LinkedInGroupPost",$("div.post-container"),{result:result});
                   });
			},
			"click;.comments":function(e){
        		var param = {}
        		var groupId = $(".GroupId").attr("data-groupId");
        		param.postId = $(e.target).closest(".postItemDiv").attr("data-postId");
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