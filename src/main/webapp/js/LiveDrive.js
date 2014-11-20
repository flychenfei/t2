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
			"click;.btnShowPhotos":function(event){
				var view = this;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
				brite.display("LiveDrive", null, {id:id, isFolderContents:true, isShowAddFolderBtn:false, isShowPhotos:true});
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
			columnDef: [
				{
					text: "Name",
					render: function (obj) {
						return obj.name;
					},
					attrs: "style='width: 15%'"
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
					text: "Files",
					render: function (obj) {
						return ((obj.type == "folder" || obj.type == "album") && obj.count > 0) ? "<span class='btn btn-default btn-sm btnFiles'>Show</button>" : "";
					},
					attrs: "style='width: 100px'"
				},
				{
					text: "Photos",
					render: function (obj) {
						return ((obj.type == "folder" || obj.type == "album") && obj.count > 0) ? "<span class='btn btn-default btn-sm btnShowPhotos'>ShowPhotos</button>" : "";
					},
					attrs: "style='width: 100px'"
				}
			],
			opts: opts
		});
	}
})();