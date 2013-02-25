(function(){
	brite.registerView("DropboxDialog",{emptyParent:false},{
		create:function(data,config){
			return app.render("tmpl-DropboxDialog",{path:data.path,type:data.type});
		},
		events:{
			"click;.dialogCloseBtn":function(event){
				this.$el.remove();
			},
			"click;.btn.cancel":function(event){
				this.$el.remove();
			},
			"click;.save":function(event){
				var view = this;
				if($(".save").hasClass("disabled"))
					return false;
				$(".save").toggleClass("disabled");
				var path = $(event.target).closest(".dialogBody").attr("data-path");
				app.dropboxApi.createFolder({path:path+"/"+$(":input[name='folder']").val()}).pipe(function(json){
					console.log(json);
					alert("adding folder successfully.");
					view.$el.remove();
					app.dropboxApi.getMetadata({path:path}).pipe(function(metadata){
						metadata = metadata.result;
						brite.display("DropboxFiles",$(".tab-content"),{metadata:metadata});
						$(".loading").toggleClass("hide");
					});
				});
			},
			"click;.upload":function(){var path = $(event.target).closest(".dialogBody").attr("data-path");
				var uploadBtn = $(event.target);
				var view = this;
				if($(uploadBtn).hasClass("disabled"))
					return false;
				$(uploadBtn).toggleClass("disabled");
				console.log($(":input[type='file']")[0].files[0]);
				app.ajaxPost(contextPath+"/dropbox/upload",{path:path},$(":input[type='file']")[0].files[0]).done(function(){
					$(".loading").toggleClass("hide");
					view.$el.remove();
					app.dropboxApi.getMetadata({path:path}).pipe(function(metadata){
						metadata = metadata.result;
						brite.display("DropboxFiles",$(".tab-content"),{metadata:metadata});
					});
				});
			}
		}
	});
})();