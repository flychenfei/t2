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
				var userInfo = null;
				app.githubApi.showUserInfo().pipe(function(result){
					userInfo = JSON.parse(result.result);
					app.githubApi.getIssue({
						name:name,
						login:login,
						issueNumber:issueNumber
					}).pipe(function(json){
						brite.display("GithubIssue",$("body"),{
							issue:json.result.issue,
							comments:json.result.comment,
							avatarUrl:userInfo.avatar_url,
							layout:{
								left:'20%',
								top:"100px",
								width:'60%',
								height:'auto'
							},
							info:{
								name:name,
								login:login,
								issueNumber:issueNumber
							}
						});
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
			},
			"click;.btn-close": function (event) {
				var $btn = $(event.target);
				if($btn.hasClass("loading")) return;
				var name = $btn.closest("table").attr("data-name");
				var login =$btn.closest("table").attr("data-login");
				var number = $btn.closest("span").attr("data-issue-id");
				$btn.html("Closing...").addClass("loading");
				app.githubApi.editIssue({
					name:name,
					login:login,
					state:"closed",
					number:number
				}).pipe(function(result){
					if(result.success == true){
						var $tr = $btn.closest("tr");
						$btn.html("Closed")
						$tr.animate({"opacity":0}, 1000, function(){
							$tr.remove();
							refreshIssueCount("close");
						});
					}else{
						$btn.html("Close").removeClass("loading");
						alert("change state failed,please check network");
					}
				})
			},
			"click;.btn-open": function (event) {
				var $btn = $(event.target);
				if($btn.hasClass("loading")) return;
				var name = $btn.closest("table").attr("data-name");
				var login =$btn.closest("table").attr("data-login");
				var number = $btn.closest("span").attr("data-issue-id");
				$btn.html("Opening...").addClass("loading");
				app.githubApi.editIssue({
					name:name,
					login:login,
					state:"open",
					number:number
				}).pipe(function(result){
					if(result.success == true){
						var $tr = $btn.closest("tr");
						$btn.html("Opened")
						$tr.animate({"opacity":0}, 1000, function(){
							$tr.remove();
							refreshIssueCount("open");
						});
					}else{
						$btn.html("Open").removeClass("loading");
						alert("change state failed,please check network");
					}
				})
			}
		}
	});

	function refreshIssueCount(operation){
		var $openCount = $(".open-count");
		var $closeCount = $(".close-count");
		if(operation == "open"){
			var openCount = +$openCount.attr("data-count")+1;
			$openCount.html(openCount+" Open").attr("data-count",openCount);
			var closeCount = +$closeCount.attr("data-count")-1;
			$closeCount.html(closeCount+" Closed").attr("data-count",closeCount);
		}else{
			var openCount = +$openCount.attr("data-count")-1;
			$openCount.html(openCount+" Open").attr("data-count",openCount);
			var closeCount = +$closeCount.attr("data-count")+1;
			$closeCount.html(closeCount+" Closed").attr("data-count",closeCount);
		}
	}
})();