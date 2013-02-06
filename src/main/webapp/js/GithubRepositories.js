(function(){
	brite.registerView("GithubRepositories",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-GithubRepositories",{repositories:data.repositories});
		},
		events:{
			"click;.createRepository":function(event){
				var btn = $(event.target);
				if($(btn).hasClass("disabled"))
					return false;
				var name=$(":input[name='newRepository']");
				var description = $(":input[name='description']");
				if(name.val().length==0){
					alert("The repository name is required");
					return false;
				}
				$(btn).toggleClass("disabled");
				$(".githubloading.create").toggleClass("hide");
				app.githubApi.createRepository({
					name:$(name).val(),
					description:$(description).val()
					}).pipe(function(json){
					if(json.success){
						alert("adding "+json.result.name+" successfully.");
					}else{
						alert(json.errorMessage);
					}
					$(btn).toggleClass("disabled");
					$(".githubloading.create").toggleClass("hide");
					$(name).val("");
					$(description).val("");
				});
			}
		}
	});
	
	Handlebars.registerHelper('date', function(dateObj) {
		 var year = parseInt(dateObj.year)+1900;
		 var month = parseInt(dateObj.month)+1;
		 var date = dateObj.date;
		 return new Handlebars.SafeString(
			year+"-"+month+"-"+date
		 );
		});
})();