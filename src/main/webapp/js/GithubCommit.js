(function(){
	brite.registerView("GithubCommit",{emptyParent:true},{
		create:function(data,config){
			console.log(data.commit);
			return app.render("tmpl-GithubCommit",{data:data.commit});
		}
	});
})();