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
			"click;.link-img,.foldername":function(event){
				var folderitem = $(event.target).closest("div");
				if(!$(folderitem).hasClass("selected")){
					$(".folderitem").removeClass("selected");
					$(folderitem).addClass("selected");
				}
			},
			"click;.copy":function(event){
				var copyBtn = $(event.target);
				if($(copyBtn).hasClass("disabled"))
					return false;
				$(copyBtn).addClass("disabled");
				var view = this;
				var fromPath = $(event.target).closest(".dialogBody").attr("data-path");
				var toPath = $("div.selected:eq(0)").attr("data-path");
				app.dropboxApi.copy({fromPath:fromPath,toPath:toPath}).pipe(function(json){
					if(json.result.error){
						alert(json.result.error);
						$(copyBtn).removeClass("disabled");
						return false;
					}
					view.$el.remove();
				});
			},
			"click;.s_web_bullet_plus":function(event){
				var expandIco = $(event.target);
				$(expandIco).attr("src","https://www.dropbox.com/static/images/icons/ajax-loading-small.gif");
				var parentPath = $(expandIco).closest("div").attr("data-path");
				app.dropboxApi.getMetadata({path:parentPath}).pipe(function(metadata){
					metadata = metadata.result;
					brite.display("DropboxSubFolder",$(expandIco).closest("div.itemDiv"),{metadata:metadata});
					$(expandIco).attr("src","https://www.dropbox.com/static/images/icons/icon_spacer.gif");
					$(expandIco).toggleClass("s_web_bullet_plus").toggleClass("s_web_bullet_minus");
				});
			},
			"click;.s_web_bullet_minus":function(event){
				var expandIco = $(event.target);
				var subFolder = $(expandIco).closest("div").next();
				$(expandIco).toggleClass("s_web_bullet_plus").toggleClass("s_web_bullet_minus");
				$(subFolder).remove();
			},
			"click;.move":function(event){
				var moveBtn = $(event.target);
				if($(moveBtn).hasClass("disabled"))
					return false;
				$(moveBtn).addClass("disabled");
				var view = this;
				var fromPath = $(event.target).closest(".dialogBody").attr("data-path");
				var toPath = $("div.selected:eq(0)").attr("data-path");
				app.dropboxApi.move({fromPath:fromPath,toPath:toPath}).pipe(function(json){
					if(json.result.error){
						alert(json.result.error);
						$(moveBtn).removeClass("disabled");
						return false;
					}
					view.$el.remove();
				});
			}
		}
	});
})();