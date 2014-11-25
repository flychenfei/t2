(function(){
	
	brite.registerView("LiveDrive",{emptyParent:true, parent:".LiveScreen-content"},{
		create: function(data,config){
			data = data || {};
			return render("tmpl-LiveDrive",data);
		},
		postDisplay: function (data) {
			var view = this;
			view.targetId = data ? data.id : "";
			view.isFolderContents = data ? data.isFolderContents : false;
			view.isShowPhotos = data ? data.isShowPhotos : false;
			showView.call(view);
		},
		events:{
			"click;.btnFolderAdd":function(e){
				var view = this;
				brite.display("LiveCreateFolder", null, {parendId: view.targetId});
			},
			"click;.btnFiles":function(event){
				var view = this;
				var $e = view.$el;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
				brite.display("LiveDrive", null, {id:id, isFolderContents:true, isShowAddFolderBtn:true});
			},
			"click;.btnDownloadFiles":function(event){
				var view = this;
				var $e = view.$el;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
				var fileName = $(event.currentTarget).closest("tr").attr("data-obj_name");
				if(id){
					window.location.href=contextPath+"/liveDrive/download?id="+id+"&fileName="+fileName;
				}else{
					alert("This file is not support download!");
				}
			},
			"click;.btnShowPhotos":function(event){
				var view = this;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
				brite.display("LiveDrive", null, {id:id, isFolderContents:true, isShowAddFolderBtn:false, isShowPhotos:true});
			},
			"click;.albumSelf":function(event){
				var view = this;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
				if(id.indexOf("folder") > -1){
					brite.display("LiveDrive", null, {id:id, isFolderContents:true, isShowAddFolderBtn:true});
				}else{
					view.$el.find(".pictureContent").removeClass("hide");
					view.$el.find(".pictureContent .showPicture").append("<img src='"+contextPath+"/liveDrive/showPicture?id="+id+"' />");
					view.$screen = $("<div class='notTransparentScreen'></div>").insertBefore(".MainScreen");
				}
			},
			"click;.glyphicon-remove":function () {
				var view = this;
				view.$el.find(".pictureContent").addClass("hide");
				view.$el.find(".pictureContent .showPicture img").remove();
				$("#bodyPage .notTransparentScreen").remove();
				
			}
		},
		docEvents: {
			"DO_REFRESH_DRIVE":function(){
				var view = this;
				showView.call(view);
			},
			"EDIT_DRIVE_OBJECT":function(){
				var view = this;
				var $e = view.$el;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
				brite.display("LiveCreateFolder", null, {id:id, parendId: view.targetId});
			},
			"DELETE_DRIVE_OBJECT":function(){
				var view = this;
				var $e = view.$el;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
				app.liveDriveApi.delete(id).done(function(result){
					setTimeout(function(){
						showView.call(view);
					}, 3000)
				});
			}
		 }
	});

	function showView() {
		var view = this;
		var listFunction = function(){
			if(view.isFolderContents){
				if(view.isShowPhotos){
					return app.liveDriveApi.showPhotos(view.targetId).pipe(function(result){
						return {result: result.result.data};
					});
				}else{
					return app.liveDriveApi.getFolderFilesList(view.targetId).pipe(function(result){
						return {result: result.result.data};
					});
				}
			}else{
				return app.liveDriveApi.getRootFolder().pipe(function(result){
					var list = [];
					list.push(result.result);
					return {result: list};
				});
			}
		}
		var opts = {
			htmlIfEmpty: "Not folders or files found",
			withPaging: false,
			withCmdDelete: false,
			withCmdEdit: false
		}
		if(view.isFolderContents){
			opts = {
				htmlIfEmpty: "Not folders or files found",
				withPaging: false,
				cmdDelete: "DELETE_DRIVE_OBJECT",
				cmdEdit: "EDIT_DRIVE_OBJECT"
			}
		}
		brite.display("DataTable", ".foldersLists", {
			dataProvider: {list: listFunction},
			rowAttrs: function (obj) {
				return "data-obj_id='{0}' data-obj_name='{1}'".format(obj.id,obj.name)
			},
			columnDef: [
				{
					text: "Name",
					render: function (obj) {
						return "<a src=\"#\" class=\"albumSelf\">"+obj.name+"</a>";
					},
					attrs: "style='width: 15%; word-break: break-word; cursor:pointer;'"
				},
				{
					text: "Type",
					render: function (obj) {
						return obj.type;
					},
					attrs: "style='width: 15%'"
				},
				{
					text: "Description",
					render: function (obj) {
						return obj.description;
					},
					attrs: "style='width: 30%'"

				},
				{
					text: "Count",
					render: function (obj) {
						return typeof obj.count != "undefined" ? obj.count : "-";
					},
					attrs: "style='width: 25px'"
				},
				{
					text: "Created Time",
					render: function (obj) {
						return obj.created_time ? obj.created_time : "";
					},
					attrs: "style='width: 20%'"
				},
				{
					text: "Updated Time",
					render: function (obj) {
						return obj.updated_time;
					},
					attrs: "style='width: 20%'"
				},
				{
					text: "",
					render: function (obj) {
						return ((obj.type == "folder" || obj.type == "album") && obj.count > 0) ? "<div class='glyphicon glyphicon-folder-open btnFiles'/>" : "";
					},
					attrs: "style='width: 40px' title='Show Files'"
				},
				{
					text: "",
					render: function (obj) {
						return (obj.type == "folder" || obj.type == "album") ?  "" : "<div class='glyphicon glyphicon-download btnDownloadFiles'/>";
					},
					attrs: "style='width: 40px'  title='Download'"
				},
				{
					text: "",
					render: function (obj) {
						return ((obj.type == "folder" || obj.type == "album") && obj.count > 0) ? "<div class='glyphicon glyphicon-picture btnShowPhotos'/>" : "";
					},
					attrs: "style='width: 40px' title='Show Photos'"
				}
			],
			opts: opts
		});
	}
})();