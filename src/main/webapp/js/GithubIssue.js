(function(){
	brite.registerView("GithubIssue",{emptyParent:false},{
		create:function(data,config){
			var view = this;
			view.issue = data.issue;
			view.info = data.info;
			return app.render("tmpl-GithubIssue",{
				issue:data.issue,
				comments:data.comments,
				layout:data.layout
				});
		},
		events:{
			"click;.dialogCloseBtn":function(event){
				this.$el.remove();
			},
			"click;.comment-edit":function(){
				var view = this;
				view.$el.find(".body-content").addClass("hide");
				view.$el.find(".body-edit").removeClass("hide");
				view.$el.find("#commentBodyBox").val(view.issue.body);
			},
			"click;.cancel-btn":function(){
				var view = this;
				var $updateBtn = view.$el.find(".update-btn");
				var $editBox = view.$el.find("#commentBodyBox");

				view.$el.find(".body-content").removeClass("hide");
				view.$el.find(".body-edit").addClass("hide");

				$editBox.prop("disabled",false);
				$updateBtn.removeClass("disabled").html("Update comment");
			},
			"click;.update-btn":function(event){
				var view = this;
				var $updateBtn = $(event.target);
				var $editBox = view.$el.find("#commentBodyBox");
				var info = view.info;
				var body = $editBox.val();

				$updateBtn.addClass("disabled").html("Updating...");
				$editBox.prop("disabled",true);
				app.githubApi.editIssue({
					name:info.name,
					login:info.login,
					body:body,
					number:info.issueNumber
				}).pipe(function(result){
					if(result.success == true){
						view.$el.find(".body-content").removeClass("hide").html(marked(body));
						view.$el.find(".body-edit").addClass("hide");
						view.issue.body = body;
					}else{
						alert("Description update failed.");
					}
					//reset the box and button status
					$editBox.prop("disabled",false);
					$updateBtn.removeClass("disabled").html("Update comment");
				})
			}
		}
	});
})();