(function(){
	brite.registerView("GithubUserInfo",{emptyParent:true},{
		create:function(data,config){
			return  app.render("tmpl-GithubUserInfo",{userInfo:data.userInfo,emails:data.emails});
		},
		events:{
			"click; .btn":function(event){
				app.githubApi.addEmail({email:$(":input[name='email']").val()}).pipe(function(email){
					alert(email+" has been added.")
				});
			}
		}
	})
})();