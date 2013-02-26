(function(){
	brite.registerView("DropboxDialog",{emptyParent:false},{
		create:function(data,config){
			return app.render("tmpl-DropboxDialog",{data:data});
		},
		postDisplay:function(){
			//when restore file,default select the recent version 
			var currentVersion = $(":radio[name='revision']").get(0);
			var recentVersion = $(":radio[name='revision']").get(1);
			if(currentVersion){
				$(currentVersion).attr("disabled","disabled");
			}
			if(recentVersion){
				$(recentVersion).attr("checked","checked");
			}
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
			"click;.upload":function(){
				var path = $(event.target).closest(".dialogBody").attr("data-path");
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
			},
			"click;.restore":function(event){
				var restoreBtn = $(event.target);
				if($(restoreBtn).hasClass("disabled"))
					return false;
				$(restoreBtn).toggleClass("disabled");
				var version = $(":input[name='revision']:checked");
				var view = this;
				var rev = $(version).val();
				var path=$(version).attr("data-path");
				var parentPath = path.substring(0,path.lastIndexOf("/"));
				app.dropboxApi.restore({path:path,rev:rev}).pipe(function(metadata){
					view.$el.remove();
					var parentPath = $("span.commonoperation").attr("data-path");
					app.dropboxApi.getMetadata({path:parentPath,include_deleted:true}).pipe(function(metadata){
						metadata = metadata.result;
						brite.display("DropboxFiles",$(".tab-content"),{metadata:metadata,showDeleted:true});
					});
				})
			},
			"click;.folderitem":function(event){
				var folderitem = $(event.target).closest("div");
				if(!$(folderitem).hasClass("selected"))
					$(folderitem).addClass("selected").siblings("div").removeClass("selected");
			},
			"click;.copy":function(event){
				var srcPath = $(event.target).closest(".dialogBody").attr("data-path");
				var distPath = $("div.selected:eq(0)").attr("data-path");
				alert(srcPath+"\n"+distPath);
			}
		}
	});
})();