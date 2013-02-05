(function(){
	brite.registerView("GithubUserInfo",{emptyParent:true},{
		create:function(data,config){
			return  app.render("tmpl-GithubUserInfo",{userInfo:data.userInfo,emails:data.emails});
		},
		events:{
			"click; .btn":function(event){
				$(".githubloading").toggleClass("hide");
				var emailInput = $(":input[name='email']");
				if($(emailInput).val().length==0){
					alert("please type the email u want add");
					return false;
				}
				var button = $(event.target);
				if($(button).hasClass("disabled"))
					return false;
				$(button).toggleClass("disabled");
				app.githubApi.addEmail({email:$(emailInput).val()}).pipe(function(result){
					alert(result.result);
					if(result.addSuccess){
						$("#emailPlace").html($("#emailPlace").html()+result.email);
					}
					$(button).toggleClass("disabled");
					$(emailInput).val("");
					$(".githubloading").toggleClass("hide");
				});
			}
		}
	})
})();