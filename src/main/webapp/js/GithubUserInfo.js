(function(){
	brite.registerView("GithubUserInfo",{emptyParent:true},{
		create:function(data,config){
			return  app.render("tmpl-GithubUserInfo",{userInfo:data.userInfo,emails:data.emails});
		},
		events:{
			"click;.btn.add":function(event){
				var emailInput = $(":input[name='email']");
				if($(emailInput).val().length==0){
					alert("please type the email u want add");
					return false;
				}
				$(".githubloading.adding").toggleClass("hide");
				var button = $(event.target);
				if($(button).hasClass("disabled"))
					return false;
				$(button).toggleClass("disabled");
				app.githubApi.addEmail({email:$(emailInput).val()}).pipe(function(result){
					alert(result.result);
					if(result.addSuccess){
						$("#emailPlace").html(
							$("#emailPlace").html()
							+"<div data-email=\""+result.email+"\">"
							+"<span class=\"emailName\">"+result.email+"</span>"
							+"<button class=\"btn btn-mini delete\">Delete</button>"
							+"<img class=\"githubloading hide deleting\" "
							+"src=\"https://a248.e.akamai.net/assets.github.com/images/spinners/octocat-spinner-32.gif\"/>"
							+"</div>"
						);
					}
					$(button).toggleClass("disabled");
					$(emailInput).val("");
					$(".githubloading.adding").toggleClass("hide");
				});
			},
			"click; .btn.delete":function(event){
				var button = $(event.target);
				$(button).next().toggleClass("hide");
				var email = $(event.target).closest("div").attr("data-email");
				app.githubApi.deleteEmail({email:email}).pipe(function(result){
					alert(result.result);
					if(result.deleteSuccess)
						$(event.target).closest("div").remove();
					$(button).next().toggleClass("hide");
				});
			}
		}
	})
})();