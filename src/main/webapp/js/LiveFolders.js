(function(){
	
	brite.registerView("LiveFolders",{emptyParent:true, parent:".LiveScreen-content"},{
		create: function(data,config){
			data = data || {};
			return render("tmpl-LiveFolders",data);
		},
		postDisplay: function (data) {
			var view = this;
			view.folderId = data ? data.id : "";
			view.isFolderContents = data ? data.isFolderContents : false;
			showFolders.call(view);
		},
		events:{
			"click;.btnFolderAdd":function(e){
				var view = this;
				brite.display("LiveCreateFolder", null, {parendId: view.folderId});
			},
			"click;.btnFiles":function(event){
				var view = this;
				var $e = view.$el;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
				brite.display("LiveFolders", null, {id:id, isFolderContents:true, isShowAddFolderBtn:true});
			}
		},
		docEvents: {
            "DO_REFRESH_FOLDER":function(){
                var view = this;
                showFolders.call(view);
            },
            "EDIT_FOLDER":function(){
                var view = this;
                var $e = view.$el;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
                brite.display("LiveCreateFolder", null, {id:id, parendId: view.folderId});
            },
            "DELETE_FOLDER":function(){
                var view = this;
                var $e = view.$el;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
                app.liveFolderApi.deleteFolder(id).done(function(result){
					setTimeout(function(){
                        showFolders.call(view);
                    }, 3000)
				});
            }
         }
	});

	function showFolders() {
		var view = this;
		var listFunction = function(){
			if(view.isFolderContents){
				return app.liveFolderApi.getFolderFilesList(view.folderId).pipe(function(result){
					return {result: result.result.data};
				});
			}else{
				return app.liveFolderApi.getRootFolder().pipe(function(result){
					var list = [];
					list.push(result.result);
					return {result: list};
				});
			}
		}
		var opts = {
			htmlIfEmpty: "Not folders found",
			withPaging: false,
			withCmdDelete: false,
			withCmdEdit: false
		}
		if(view.isFolderContents){
			opts = {
				htmlIfEmpty: "Not folders found",
				withPaging: false,
				cmdDelete: "DELETE_FOLDER",
                cmdEdit: "EDIT_FOLDER"
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
						return obj.count;
					},
					attrs: "style='width: 25px'"
				},
				{
					text: "Created Time",
					render: function (obj) {
						return obj.created_time;
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
						return "<span class='btn btn-default btn-sm btnFiles'>Show</button>";
					},
					attrs: "style='width: 100px'"
				}
			],
			opts: opts
		});
	}
})();