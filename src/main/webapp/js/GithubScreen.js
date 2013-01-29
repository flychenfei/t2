(function(){
	brite.registerView("GithubScreen",{parent:".MainScreen-main",emptyParent:true},{
		create:function(data,config){
			return  app.render("tmpl-GithubScreen")
		},
		events:{
			"click;.btn":function(event){
				app.oauth.authorize("Github");
			}
		}
	})
})();