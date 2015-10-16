(function(){
	brite.registerView("GithubIssues",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-GithubIssues",{issues:data.issues,name:data.name,login:data.login,issueState:data.issueState,openCount: data.openCount,closedCount:data.closedCount});
		},
		events:{
			"click;.openIssues":function(event){
				var name = $(event.target).closest("div").attr("data-name");
				var login = $(event.target).closest("div").attr("data-login");
				app.githubApi.getIssues({
					name:name,
					login:login,
					state:"open"
				}).pipe(function(json){
					brite.display("GithubIssues",$(".tab-content"),{issues:json.result.issues,name:name,login:login,issueState:"open",openCount: json.result.openCount,closedCount:json.result.closedCount});
				});
			},
			"click;.closedIssues":function(event){
				var name = $(event.target).closest("div").attr("data-name");
				var login = $(event.target).closest("div").attr("data-login");
				app.githubApi.getIssues({
					name:name,
					login:login,
					state:"closed"
				}).pipe(function(json){
					brite.display("GithubIssues",$(".tab-content"),{issues:json.result.issues,name:name,login:login,issueState:"closed",openCount: json.result.openCount,closedCount:json.result.closedCount});
				});
			}
		}
	});
})();