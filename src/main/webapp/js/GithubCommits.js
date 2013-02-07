(function(){
	brite.registerView("GithubCommits",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-GithubCommits",{commits:data.commits});
		}
	})
})();