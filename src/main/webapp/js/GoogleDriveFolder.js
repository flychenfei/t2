;
(function () {
	brite.registerView("GoogleDriveFolder", {emptyParent:false}, {
		create:function (data, config) {
			var view = this;
			view.filedId = data.fileId;
			this.model = {};
			if(data||data.callback) {
				this.model.callback = data.callback;
			}
			return app.render("tmpl-GoogleDriveFolder", {data:data});
		},
		postDisplay:function(){
			var view = this;
			var $e = view.$el;
			refresh.call(view);
		},
		events:{
			"click;.dialogCloseBtn":function(event){
				this.$el.remove();
			},
			"click;.btn.cancel":function(event){
				this.$el.remove();
			},
			"click;.driveFolderPlus":function(event){
				var expandIco = $(event.target);
				if(expandIco.attr("class")==="glyphicon glyphicon-plus"){
					var param = {};
					param.parentId = $(event.target).closest(".folderitem").attr("data-selfId");
					app.googleDriveApi.foldersInfo(param).done(function (result) {
						brite.display("GoogleDriveSubFolder",$(expandIco).closest("div.itemDiv"),{result:result.result});
					});
					expandIco.removeClass("glyphicon glyphicon-plus").addClass("glyphicon glyphicon-minus");
					expandIco.closest(".folderitem").find(".glyphicon glyphicon-folder-close").removeClass("glyphicon glyphicon-folder-close").addClass("glyphicon glyphicon-folder-open");
				}else if(expandIco.attr("class")==="glyphicon glyphicon-minus"){
					var subFolder = $(expandIco).closest("div").next();
					$(subFolder).remove();
					expandIco.removeClass("glyphicon glyphicon-minus").addClass("glyphicon glyphicon-plus");
					expandIco.closest(".folderitem").find(".glyphicon glyphicon-folder-open").removeClass("glyphicon glyphicon-folder-open").addClass("glyphicon glyphicon-folder-close");
				}
			},
			"click;.foldername":function(event){
				$(".foldername.select").toggleClass("select");
				$(event.target).toggleClass("select");
				if($(".move").hasClass("disabled"))
					$(".move").removeClass("disabled");
				if($(".copy").hasClass("disabled"))
					$(".copy").removeClass("disabled");
			},
			"click;.newFolder":function(e){
				var view = this;
				var $e = view.$el;
				var parentId = $(e.target).closest(".dialogBody").attr("data-parentid");
				var selfId = $e.find(".dialogBody .select").attr("data-selfid");

				brite.display("InputValue", ".dialogContent", {
					title: 'Create Folder',
					fields: [
						{label:"Folder Name", name:'folderName', mandatory:false}
					],
					callback: function (params) {
						var params = params || {};
						if(selfId.length > 0){
							params.parentId = selfId;
						}else{
							params.parentId = parentId;
						}
						
						app.googleDriveApi.createFolder(params).done(function (result) {
							if(result.success === true){
								alert("CreateFolder success");
								refresh.call(view);
							}else{
								alert("CreateFolder fail");
							}
							var params = {};
							params.selfId = parentId;

							brite.display("GoogleDriveFiles",".GoogleScreen-content",{
								results: function(){
									return app.googleDriveApi.childList(params);
								}
							});
						});
					}
				});
			},
			"click;.move":function(event){
				var moveBtn = $(event.target);
				var view = this;
				var param = {};
				param.fileId = $(".dialogBody").attr("data-fileId");
				param.parentId = $(".dialogBody").attr("data-parentId");
				param.moveId = $(".foldername.select").attr("data-selfId");
				if(param.parentId === param.moveId){
					alert("Can't move file to itself!");
					return false;
				}
				if($(moveBtn).hasClass("disabled"))
					return false;
				$(moveBtn).addClass("disabled");
				app.googleDriveApi.moveFile(param).done(function(result){
					if(result.success === true){
							alert("move success");
						}else{
							alert("move fail");
						}
					view.$el.remove();
					var params = {};
					params.selfId = param.parentId;
					brite.display("GoogleDriveFiles",".GoogleScreen-content",{
						results: function(){
							return app.googleDriveApi.childList(params);
						}
					});
				});
			},
			"click;.copy":function(event){
				var copyBtn = $(event.target);
				var view = this;
				var param = {};
				var parentId = $(".dialogBody").attr("data-parentId");
				param.fileId = $(".dialogBody").attr("data-fileId");
				param.parentId = $(".dialogBody").attr("data-parentId");
				param.copyTitle= $(".copyTitle").val();
				param.targetId = $(".foldername.select").attr("data-selfId");
				if($(copyBtn).hasClass("disabled"))
					return false;
				$(copyBtn).addClass("disabled");
				app.googleDriveApi.copyFile(param).done(function(result){
					if(result.success === true){
							alert("copy success");
						}else{
							alert("copy fail");
						}
					view.$el.remove();
					var params = {};
					params.selfId = parentId;
					brite.display("GoogleDriveFiles",".GoogleScreen-content",{
						results: function(){
							return app.googleDriveApi.childList(params);
						}
					});
				});
			}
		}
		});
	// --------- Private Methods--------- //
	function refresh(data){
		var view = this;
		var $e = view.$el;
			$(".move").addClass("disabled");
			$(".copy").addClass("disabled");
			param = {};
			app.googleDriveApi.foldersInfo(param).done(function (result) {
				$("div.itemDiv").empty();
				brite.display("GoogleDriveSubFolder",$("div.itemDiv"),{result:result.result,root:true}).done(function(){
					for (var i = 0; i < result.result.length; i++){
						var selfId = result.result[i].selfId;
						var filedId = view.filedId;
						if(selfId == filedId){
							$e.find(".rootfolder .folderitem[data-selfid='"+selfId+"']").addClass("hide");
						}
					}
				});
			});		
	}
	})();