(function(){
	brite.registerView("GithubCommit",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-GithubCommit",{data:data.commit});
		}
	});
})();