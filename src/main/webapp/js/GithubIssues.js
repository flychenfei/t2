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
			},
			"click;.message":function(event){
				var name = $(event.target).closest("table").attr("data-name");
				var login = $(event.target).closest("table").attr("data-login");
				var issueNumber = $(event.target).closest("td").attr("data-issue-id");
				app.githubApi.getIssue({
					name:name,
					login:login,
					issueNumber:issueNumber
				}).pipe(function(json){
					brite.display("GithubIssue",$("body"),{
						issue:json.result.issue,
						comments:json.result.comment,
						layout:{
							left:'20%',
							top:"100px",
							width:'60%',
							height:'auto'
						}
					});
				});
			},
			"click;.new-issue":function(event){
				var name = $(event.target).closest("div").attr("data-name");
				var login = $(event.target).closest("div").attr("data-login");
				app.githubApi.showUserInfo().pipe(function(result){
					var userInfo = JSON.parse(result.result);
					brite.display("GithubNewIssue",$("body"),{
						name:name,
						login:login,
						avatarUrl:userInfo.avatar_url,
						layout:{
							left:'20%',
							top:"100px",
							width:'60%',
							height:'auto'
						}
					})
				});
			}
		}
	});
})();