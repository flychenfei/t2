(function(){
	brite.registerView("GithubDialog",{emptyParent:false},{
		create:function(data,config){
			return app.render("tmpl-GithubDialog",{data:data});
		},
		events:{
			"click;.dialogCloseBtn":function(event){
				this.$el.remove();
			},
			"click;.btn.cancel":function(event){
				this.$el.remove();
			},
			"click;.filename":function(event){
				var current = $(event.target);
				var path=$(current).closest("span.filename").attr("data-path");
				var repo = $(current).closest("div.files").attr("data-repo");
				var readme = {content:""};
				app.githubApi.getContents({repo:repo,path:path}).pipe(function(files){
					files = JSON.parse(files.result);
					brite.display("GithubFiles",$("#filecontent"),{
						files:files,
						readme:readme,
						repo:repo
					});
				});
			}
		}
	})
})();