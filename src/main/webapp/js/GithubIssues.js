(function(){
	brite.registerView("GithubIssues",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-GithubIssues",{issues:data.issues,name:data.name,login:data.login,issueState:data.issueState,openCount: data.openCount,closedCount:data.closedCount,pageNum:data.pageNum,pageSum:data.pageSum});
		},
		events:{
			"click;.openIssues":function(event){
				refreshIssues(event,"open");
			},
			"click;.closedIssues":function(event){
				refreshIssues(event,"closed");
			},
			"click;.message":function(event){
				var name = $(event.target).closest("table").attr("data-name");
				var login = $(event.target).closest("table").attr("data-login");
				var issueNumber = $(event.target).closest("td").attr("data-issue-id");
				var userInfo = null;
				app.githubApi.showUserInfo().pipe(function(result){
					userInfo = JSON.parse(result.result);
					app.githubApi.getIssueEvents({
						name:name,
						login:login,
						issueId:issueNumber
					}).pipe(function(reData){
						var issueEvents = reData.result;
						$.when(addCommit(name,login,issueEvents)).pipe(function(){
							app.githubApi.getIssue({
								name:name,
								login:login,
								issueNumber:issueNumber
							}).pipe(function(json){
								var commentsEvents = refreshGroup(json.result.comment,issueEvents);
								brite.display("GithubIssue",$("body"),{
									issue:json.result.issue,
									commentsEvents:commentsEvents,
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
				changeIssueState(event,"closed","Closing...","Closed");
			},
			"click;.btn-open": function (event) {
				changeIssueState(event,"open","Opening...","Opened");
			},
			"click;.page-previous": function (event) {
				var name = $(event.target).closest("div").attr("data-name");
				var login = $(event.target).closest("div").attr("data-login");
				var state = $(event.target).closest("div").attr("data-state");
				var pageNum = parseInt($(event.target).closest("div").attr("data-pageNum"))-1;
				var pageSum = parseInt($(event.target).closest("div").attr("data-pageSum"));
				if(pageNum <= 1)pageNum = 1;
				else if(pageNum >= pageSum)pageNum = pageSum;
				app.githubApi.getIssues({
					name:name,
					login:login,
					state:state,
					pageNum:pageNum
				}).pipe(function(json){
					brite.display("GithubIssues",$(".tab-content"),{issues:json.result.issues,name:name,login:login,issueState:state,openCount: json.result.openCount,closedCount:json.result.closedCount,pageNum:pageNum,pageSum:json.result.pageSum});
				});
			},
			"click;.page-next": function (event) {
				var name = $(event.target).closest("div").attr("data-name");
				var login = $(event.target).closest("div").attr("data-login");
				var state = $(event.target).closest("div").attr("data-state");
				var pageNum = parseInt($(event.target).closest("div").attr("data-pageNum"))+1;
				var pageSum = parseInt($(event.target).closest("div").attr("data-pageSum"));
				if(pageNum <= 1)pageNum = 1;
				else if(pageNum >= pageSum)pageNum = pageSum;
				app.githubApi.getIssues({
					name:name,
					login:login,
					state:state,
					pageNum:pageNum
				}).pipe(function(json){
					brite.display("GithubIssues",$(".tab-content"),{issues:json.result.issues,name:name,login:login,issueState:state,openCount: json.result.openCount,closedCount:json.result.closedCount,pageNum:pageNum,pageSum:json.result.pageSum});
				});
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
	};

	function refreshGroup(comments,events){
		comments = comments.concat(events);
		comments.sort(function(a,b){
			var date1 = typeof(a.createdAt) == "undefined" ? new Date(a.created_at).getTime() : a.createdAt.time;
			var date2 = typeof(b.createdAt) == "undefined" ? new Date(b.created_at).getTime() : b.createdAt.time;
			return date1 - date2;
		});
		return comments;
	};

	function addCommit(name,login,events){
		var dfds = new Array();
		var x = 0;
		for(x = 0;x <events.length;x++){
			if(events[x].event == "referenced" && typeof(events[x].commit_id) != "undefined"){
				(function(p){
					var dfd = $.Deferred();
					dfds.push(dfd);
					app.githubApi.getCommit({
						name:name,
						login:login,
						sha:events[p].commit_id
					}).pipe(function(result){
						events[p].commit = result.result.commit.message;
						dfd.resolve();
					})
				})(x);
			}
		}
		var dfd = $.Deferred();
		dfds.push(dfd);
		dfd.resolve();
		return dfds;
	}

	function changeIssueState(event,state,stating,stateBtn){
		var $btn = $(event.target);
		if($btn.hasClass("loading")) return;
		var name = $btn.closest("table").attr("data-name");
		var login =$btn.closest("table").attr("data-login");
		var number = $btn.closest("span").attr("data-issue-id");
		$btn.html(stating).addClass("loading");
		app.githubApi.editIssue({
			name:name,
			login:login,
			state:state,
			number:number
		}).pipe(function(result){
			if(result.success == true){
				var $tr = $btn.closest("tr");
				$btn.html(stateBtn)
				$tr.animate({"opacity":0}, 1000, function(){
					$tr.remove();
					refreshIssueCount(state);
				});
			}else{
				$btn.html(stateBtn).removeClass("loading");
				alert("change state failed,please check network");
			}
		})
	}

	function refreshIssues(event,state){
		var name = $(event.target).closest("div").attr("data-name");
		var login = $(event.target).closest("div").attr("data-login");
		app.githubApi.getIssues({
			name:name,
			login:login,
			state:state,
			pageNum:1
		}).pipe(function(json){
			brite.display("GithubIssues",$(".tab-content"),{issues:json.result.issues,name:name,login:login,issueState:state,openCount: json.result.openCount,closedCount:json.result.closedCount,pageNum:1,pageSum:json.result.pageSum});
		});
	}
})();