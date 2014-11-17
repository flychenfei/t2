(function(){
	
	brite.registerView("LiveFolders",{emptyParent:true, parent:".LiveScreen-content"},{
		create: function(data,config){
			return render("tmpl-LiveFolders");
		},
		postDisplay: function () {
			var view = this;
			showFolders.call(view);
		},
		events:{
			"click;.btnAdd":function(e){
				brite.display("LiveCreateFolder", null, {id: null});
			},
		},
		docEvents: {
            "DO_REFRESH_FOLDER":function(){
                 var view = this;
                 showFolders.call(view);
            }
         }
	});

	function showFolders() {
		var view = this;
		var listFunction = function(){
			return app.liveFolderApi.getUserFolders().pipe(function(result){
				return {result: result.result.data};
			});
		}
		brite.display("DataTable", ".foldersLists", {
			dataProvider: {list: listFunction},
			columnDef: [
				{
					text: "#",
					render: function (obj, idx) {
						return idx + 1;
					},
					attrs: "style='width: 25px'"
				},
				{
					text: "Name",
					render: function (obj) {
						return obj.name;
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
					text: "From Name",
					render: function (obj) {
						return obj.from.name;
					},
					attrs: "style='width: 15%'"
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
				}
			],
			opts: {
				htmlIfEmpty: "Not folders found",
				withPaging: false,
				withCmdDelete: false,
				withCmdEdit: false
			}
		});
	}
})();