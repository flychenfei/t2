(function(){
	
	brite.registerView("LiveAlbums",{emptyParent:true, parent:".LiveScreen-content"},{
		create: function(data,config){
			return render("tmpl-LiveAlbums");
		},
		postDisplay: function () {
			var view = this;
			showAlbums.call(view);
		},
		events:{
			"click;.btnAdd":function(e){
				brite.display("LiveCreateAlbum", null, {id: null});
			},
		},
		docEvents: {
            "DO_REFRESH_ALBUM":function(){
                 var view = this;
                 showAlbums.call(view);
            }
         }
	});

	function showAlbums() {
		var view = this;
		var listFunction = function(){
			return app.liveAlbumApi.getUserAlbums().pipe(function(result){
				console.log(result.result.data)
				return {result: result.result.data};
			});
		}
		brite.display("DataTable", ".albumsLists", {
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
				htmlIfEmpty: "Not albums found",
				withPaging: false,
				withCmdDelete: false,
				withCmdEdit: false
			}
		});
	}
})();