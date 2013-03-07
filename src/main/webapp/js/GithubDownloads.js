;(function(){
	brite.registerView("GithubDownloads",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-GithubDownloads",{data:data});
		}
	});
})();