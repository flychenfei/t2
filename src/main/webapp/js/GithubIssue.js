(function(){
	brite.registerView("GithubIssue",{emptyParent:false},{
		create:function(data){
			var view = this;
			view.issue = data.issue;
			view.info = data.info;
			Handlebars.registerPartial("issue-comment-add",Handlebars.templates["issue-comment-add"]);
			return app.render("tmpl-GithubIssue",{
				issue:data.issue,
				comments:data.comments,
				layout:data.layout,
				avatarUrl:data.avatarUrl
				});
		},
		events:{
			"click;.dialogCloseBtn":function(){
				this.$el.remove();
			},
			"click;.comment-edit":function(event){
				var view = this;
				var $issueComment = $(event.target).closest(".issue-comment");
				$issueComment.find(".body-content").addClass("hide");
				$issueComment.find(".body-edit").removeClass("hide");
				$issueComment.find(".edit-box").val(view.issue.body);
			},
			"click;.comments-edit":function(event){
				var $issueComment = $(event.target).closest(".issue-comment");
				var body = $issueComment.find(".comment-header").attr("data-body");
				$issueComment.find(".body-content-comment").addClass("hide");
				$issueComment.find(".body-edit-comment").removeClass("hide");
				$issueComment.find(".edit-box").val(body);
			},
			"click;.cancel-btn":function(event){
				var $issueComment = $(event.target).closest(".issue-comment");
				var $updateBtn = $issueComment.find(".update-btn");
				var $editBox = $issueComment.find(".edit-box");

				$issueComment.find(".body-content").removeClass("hide");
				$issueComment.find(".body-edit").addClass("hide");

				$editBox.prop("disabled",false);
				$updateBtn.removeClass("disabled").html("Update comment");
			},
			"click;.comment-cancel-btn":function(event){
				var $issueComment = $(event.target).closest(".issue-comment");
				var $updateBtn = $issueComment.find(".comment-update-btn");
				var $editBox = $issueComment.find(".edit-box");

				$issueComment.find(".body-content-comment").removeClass("hide");
				$issueComment.find(".body-edit-comment").addClass("hide");

				$editBox.prop("disabled",false);
				$updateBtn.removeClass("disabled").html("Update comment");
			},
			"click;.update-btn":function(event){
				var view = this;
				var $updateBtn = $(event.target);
				var $issueComment = $updateBtn.closest(".issue-comment");
				var $editBox = $issueComment.find(".edit-box");
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
						$issueComment.find(".body-content").removeClass("hide").html(marked(body));
						$issueComment.find(".body-edit").addClass("hide");
						view.issue.body = body;
					}else{
						alert("Description update failed.");
					}
					//reset the box and button status
					$editBox.prop("disabled",false);
					$updateBtn.removeClass("disabled").html("Update comment");
				})
			},
			"click;.comment-update-btn":function(event){
				var id = $(event.target).closest("div").attr("data-id");
				var view = this;
				var $updateBtn = $(event.target);
				var $issueComment = $updateBtn.closest(".issue-comment");
				var $editBox = $issueComment.find(".edit-box");
				var info = view.info;
				var body = $editBox.val();

				$updateBtn.addClass("disabled").html("Updating...");
				$editBox.prop("disabled",true);
				app.githubApi.editComment({
					name:info.name,
					login:info.login,
					body:body,
					commentId:id
				}).pipe(function(result){
					if(result.success == true){
						$issueComment.find(".body-content-comment").removeClass("hide").html(marked(body));
						$issueComment.find(".body-edit-comment").addClass("hide");
						$issueComment.find(".comment-header").attr("data-body",body);
					}else{
						alert("Description update failed.");
					}
					//reset the box and button status
					$editBox.prop("disabled",false);
					$updateBtn.removeClass("disabled").html("Update comment");
				})
			},
			"click;.edit":function(event){
				var $issueTitle = $(event.target).closest(".dialogHead");
				$issueTitle.find(".title-edit").removeClass("hide");
				$issueTitle.find(".dialogTitle").addClass("hide");
				$issueTitle.find(".title-edit-box").val(this.issue.title);
			},
			"click;.title-save":function(event){
				var view = this;
				var $saveBtn = $(event.target);
				var $issueTitle = $saveBtn.closest(".dialogHead");
				var newTitle = $issueTitle.find(".title-edit-box").val();
				var info = this.info;
				$saveBtn.addClass("disabled").html("Saving...");
				$issueTitle.find(".cancel").addClass("disabled");
				app.githubApi.editTitle({
					name:info.name,
					login:info.login,
					number:info.issueNumber,
					title:newTitle
				}).pipe(function(result){
					$saveBtn.removeClass("disabled",false).html("Save");
					$issueTitle.find(".cancel").removeClass("disabled");
					if(result.success == true){
						$issueTitle.find(".dialogTitle").removeClass("hide");
						$issueTitle.find(".title-edit").addClass("hide");
						$issueTitle.find(".dialogTitle-content").html(newTitle);
						$(".message[data-issue-id = '"+view.issue.number+"']").find(".messageTitle").html(newTitle);
						view.issue.title = newTitle;
					}else{
						alert("Description update failed.");
					}
				})
			},
			"click;.comments-remove":function(event){
				var info = this.info;
				var commentId = $(event.target).closest(".issue-comment").attr("comment-number");
				var option = window.confirm("Are you sure you want to delete this?");
				if(option == true){
					app.githubApi.deleteComment({
						name:info.name,
						login:info.login,
						commentId:commentId
					}).pipe(function(result){
						console.info(result);
						if(result.success == true){
							$(".issue-comment[comment-number = '"+commentId+"']").addClass("hide");
						}else{
							alert("Description delete failed.");
						}
					})
				}
			},
			"click;.cancel":function(event){
				var $issueTitle = $(event.target).closest(".dialogHead");
				$issueTitle.find(".dialogTitle").removeClass("hide");
				$issueTitle.find(".title-edit").addClass("hide");
			},
			"click;.comment-add":function(event){
				var info = this.info;
				var view = this;
				var $commentBody = $(event.target).closest(".comment-body");
				var issueId = $commentBody.attr("issue-id");
				var comment = $commentBody.find(".comment-add-text").val();
				if(comment == null){
					alert("Comment can't null");
				}else{
					app.githubApi.addComment({
						name:info.name,
						login:info.login,
						issueId:issueId,
						comment:comment
					}).pipe(function(result){
						if(result.success == true){
							view.$el.find(".issue-comments").append(render("issue-comment-add",{comment:result.result}));
							$commentBody.find(".comment-add-text").val("");
						}
					});
				}
			}
		}
	});
})();