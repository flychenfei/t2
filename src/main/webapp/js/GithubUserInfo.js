(function(){
	brite.registerView("GithubUserInfo",{emptyParent:true},{
		create:function(data,config){
			return  app.render("tmpl-GithubUserInfo",{userInfo:data.userInfo,emails:data.emails});
		}
	})
})();