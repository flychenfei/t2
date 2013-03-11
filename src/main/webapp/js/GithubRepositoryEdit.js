(function(){
	brite.registerView("GithubRepositoryEdit",{emptyParent:false},{
		create:function(data,config){
			return app.render("tmpl-GithubRepositoryEdit",{
				id:data.id,
				name:data.name,
				description:data.description,
				login:data.login,
				layout:data.layout
				});
		},
		events:{
			"click;.dialogCloseBtn":function(event){
				this.$el.remove();
			},
			"click;.btn.cancel":function(event){
				this.$el.remove();
			},
			"click;.btn.save":function(event){
				var dialogContent = $(event.target).closest(".dialogContent");
				var name = $(":input[name='repositoryName']",dialogContent);
				var description = $(":input[name='description']",dialogContent);
				var view = this;
				var saveBtn = $(event.target);
				var loading = $(".githubloading.save");
				$(loading).toggleClass("hide");
				$(saveBtn).toggleClass("hide");
				app.githubApi.editRepository({
					name:$(name).val(),
					description:$(description).val(),
					repositoryId:$(name).closest("div").attr("data-repository-id"),
					login:$(name).closest("div").attr("data-login")
				}).pipe(function(json){
					if(json.success){
						alert("Alter repository successfully");
						view.$el.remove();
					}else{
						alert(json.errorMessage);
						$(loading).toggleClass("hide");
						$(saveBtn).toggleClass("hide");
					}
				});
			}
		}
	});
})();