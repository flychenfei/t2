(function(){
	brite.registerView("GithubNewIssue",{emptyParent:false},{
		create:function(data,config){
			return app.render("tmpl-GithubNewIssue",{
				name:data.name,
				login:data.login,
				avatarUrl:data.avatarUrl,
				layout:data.layout});
		},
		events:{
			"click;.dialogCloseBtn":function(event){
				this.$el.remove();
			},
			"click;.btn.cancel":function(event){
				this.$el.remove();
			},
			"click;.newissue":function(event){
				var view = this;
				var NewIssue = $(event.target).closest(".dialogContent");
				var name = $(event.target).closest("div").attr("data-name");
				var login = $(event.target).closest("div").attr("data-login");
				var title = $(".issue-title",NewIssue);
				var body = $(".issue-body",NewIssue);
				if($(title).val() == "" || $(body).val() == ""){
					alert("title or body is not null");
				}else{
					app.githubApi.newIssue({
						name:name,
						login:login,
						title:$(title).val(),
						body:$(body).val()
					}).done(function(json){
						console.info(json);
						refresh(view,json,name,login);
					});
				}
			}
		}
	});

	function refresh(view,json,repoName,login){
		if (json.success) {
			app.githubApi.getIssues({
				name:repoName,
				login:login,
				state:"open"
			}).pipe(function(json){
				view.$el.remove();
				brite.display("GithubIssues",$(".tab-content"),{issues:json.result.issues,name:repoName,login:login,issueState:"open",openCount: json.result.openCount,closedCount:json.result.closedCount});
			});
		} else {
			alert("New Issue Failed");
		}
	}
})();