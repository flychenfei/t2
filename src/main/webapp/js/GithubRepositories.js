(function(){
	brite.registerView("GithubRepositories",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-GithubRepositories",{repositories:data.repositories});
		}
	})
})();