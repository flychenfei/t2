(function(){
	brite.registerView("GithubFiles",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-GithubFiles",{data:data});
		}
	});
})();