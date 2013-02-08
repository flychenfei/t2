(function(){
	brite.registerView("GithubCommits",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-GithubCommits",{commits:data.commits,name:data.name,login:data.login});
		},
		events:{
			"click;.messageTitle":function(event){
				var sha = $(event.target).closest("tr").attr("data-sha");
				var name = $(event.target).closest("table").attr("data-name");
				var login = $(event.target).closest("table").attr("data-login");
				app.githubApi.getCommit({
					name:name,
					login:login,
					sha:sha
				}).pipe(function(json){
					brite.display("GithubCommit",$(".tab-content"),{commit:json.result});
				});
			}
		}
	});
})();