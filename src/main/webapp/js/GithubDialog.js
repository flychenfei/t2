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
				var isDir = $(current).closest("span.filename").attr("data-type")=="dir"?true:false;
				app.githubApi.getContents({repo:repo,path:path}).pipe(function(json){
					var result = json.result;
					if(isDir){
						brite.display("GithubFiles",$("#filecontent"),{
							files:JSON.parse(result),
							readme:readme,
							repo:repo,
							isDir:true
						});
					}else{
						brite.display("GithubFiles",$("#filecontent"),{
							file:result,
							readme:readme,
							repo:repo,
							isDir:false,
							filename:result.name
						});
					}
				});
			}
		}
	})
})();