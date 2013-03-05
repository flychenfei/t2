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
				app.githubApi.getContents({repo:repo,path:path}).pipe(function(files){
					console.log(files);
				});
			}
		}
	})
})();