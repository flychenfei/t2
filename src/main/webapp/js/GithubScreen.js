(function(){
	brite.registerView("GithubScreen",{parent:".MainScreen-main",emptyParent:true},{
		create:function(data,config){
			return  app.render("tmpl-GithubScreen")
		},
		postDisplay:function(){
			/*app.githubApi.showUserInfo().pipe(function(result){
				var userInfo = JSON.parse(result.result);
				var emails = JSON.parse(result.emails);
				brite.display("GithubUserInfo",$(".tab-content"),{userInfo:userInfo,emails:emails});
			});*/
			app.githubApi.getRepositories().pipe(function(repositories){
				repositories = repositories.result;
				brite.display("GithubRepositories",$(".tab-content"),{repositories:repositories});
			});
		},
		events:{
			"click;.auth":function(event){
				app.oauth.authorize("Github");
			},
			"click;.show":function(event){
				app.githubApi.showUserInfo().pipe(function(result){
					var userInfo = JSON.parse(result.result);
					var emails = result.emails;
					brite.display("GithubUserInfo",$(".tab-content"),{userInfo:userInfo,emails:emails});
				});
			},
			"click;.nav-tabs li":function(event){
				this.$el.find("li").removeClass("active");
				var $li = $(event.currentTarget);
				$li.addClass("active");
				var menu = $li.attr("data-nav");
				$(".tab-content").html("<div class=\"alert alert-info\">Tring to load data,Please wait...</div>");
				if(menu=="UserInformation"){
					app.githubApi.showUserInfo().pipe(function(result){
						var userInfo = JSON.parse(result.result);
						var emails = result.emails.split(",");
						brite.display("GithubUserInfo",$(".tab-content"),{userInfo:userInfo,emails:emails});
					});
				}else if(menu=="Repositories"){
					app.githubApi.getRepositories().pipe(function(repositories){
						repositories = repositories.result;
						brite.display("GithubRepositories",$(".tab-content"),{repositories:repositories});
					});
				}
			}
		}
	})
})();