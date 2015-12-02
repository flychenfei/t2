(function(){
	brite.registerView("GithubIssues",{emptyParent:true},{
		create:function(data,config){
			var view = this;
			view.data =data;
			return app.render("tmpl-GithubIssues",{name:data.name,login:data.login,issueState:data.issueState,openCount: data.openCount,closedCount:data.closedCount});
		},
		postDisplay:function(data,config){
			var view = this;
			showListIssues.call(view,"open");
		},
		events:{
			"click;.openIssues":function(event){
				var view = this;
				showListIssues.call(view,"open");
			},
			"click;.closedIssues":function(event){
				var view = this;
				showListIssues.call(view,"closed");
			},
			"click;.message":function(event){
				var view = this;
				var name = view.data.name;
				var login = view.data.login;
				var issueNumber = $(event.target).closest("div").attr("data-issue-id");
				var userInfo = null;
				console.info(name+","+login+","+issueNumber);
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
			"click;.btn-closed": function (event) {
				changeIssueState(event,"closed","Closing...","Closed");
			},
			"click;.btn-open": function (event) {
				changeIssueState(event,"open","Opening...","Opened");
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
		var view = this;
		var $btn = $(event.target);
		if($btn.hasClass("loading")) return;
		var name = view.data.name;
		var login = view.data.login;
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

	function showListIssues(state){
		var view = this;
		data = view.data;
		var $e = view.$el;
		if(state.issueState != null){
			state="open";
		}
		if(state == "open"){
			$e.find(".openIssues").addClass("choosed");
			$e.find(".closedIssues").removeClass("choosed");
		}else{
			$e.find(".openIssues").removeClass("choosed");
			$e.find(".closedIssues").addClass("choosed");
		}
		return brite.display("DataTable",".issues-container",{
			dataProvider:{list:app.githubApi.getIssues},
			columnDef: [
				{
					text:"",
					render:function(obj){
						return app.render("issueList",{number:obj.number,title:obj.title,createdAt:obj.createdAt,login:obj.user.login});
					}
				},
				{
					text:"",
					render:function(obj){
						if(obj.state == "open"){
							return "<div class='sha'><span class='btn btn-closed' data-issue-id="+obj.number+">Closed</span></div>";
						}else{
							return "<div class='sha'><span class='btn btn-open' data-issue-id="+obj.number+">Open</span></div>"
						}
					},
					attrs:"style='padding-right:10px;width:100px;'"
				}
			],
			opts: {
				htmlIfEmpty: "Not issues found",
				withPaging:true,
				withDataListening:true,
				withCmdDelete:false,
				dataOpts:{
					login:data.login,
					name:data.name,
					state:state
				}
			}
		})
	}
})();