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
            },
            "EDIT_ALBUM":function(){
                var view = this;
                var $e = view.$el;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
                brite.display("LiveCreateAlbum", null, {id:id});
            },
            "DELETE_ALBUM":function(){
                var view = this;
                var $e = view.$el;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
                app.liveAlbumApi.deleteAlbum(id).done(function(result){
					setTimeout(function(){
                        showAlbums.call(view);
                    }, 3000)
				});
            }
         }
	});

	function showAlbums() {
		var view = this;
		var listFunction = function(){
			return app.liveAlbumApi.getUserAlbums().pipe(function(result){
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
				cmdDelete: "DELETE_ALBUM",
                cmdEdit: "EDIT_ALBUM"
			}
		});
	}
})();