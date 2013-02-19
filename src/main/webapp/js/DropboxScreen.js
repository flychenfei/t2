(function(){
	brite.registerView("DropboxScreen",{parent:".MainScreen-main",emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-DropboxScreen");
		},
		events:{
			"click;.btn":function(event){
				app.oauth.authorize("Dropbox");
			},
			"click;.nav-tabs li":function(event){
				this.$el.find("li").removeClass("active");
				var $li = $(event.currentTarget);
				$li.addClass("active");
				var menu = $li.attr("data-nav");
				$(".tab-content").html("<div class=\"alert alert-info\">Tring to load data,Please wait...</div>");
				if(menu=="accountInfo"){
					app.dropboxApi.getAccountInfo().pipe(function(account){
						console.log(account);
						brite.display("DropboxAccountInfo",$(".tab-content"),{account:account.result});
					});
				}else if(menu=="Repositories"){
					app.githubApi.getRepositories().pipe(function(repositories){
						repositories = repositories.result;
						brite.display("GithubRepositories",$(".tab-content"),{repositories:repositories});
					});
				}
			}
		}
	});
})();