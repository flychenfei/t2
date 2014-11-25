(function(){
	
	brite.registerView("LiveAlbums",{emptyParent:true, parent:".LiveScreen-content"},{
		create: function(data,config){
			view = this;
			data = data || {};
			var $html = app.render("tmpl-LiveAlbums",data);
			return $html;
		},
		postDisplay: function (data) {
			var view = this;
			view.albumId = data ? data.id : "";
			view.isShowPhotos = data ? data.isShowPhotos : false;
			showAlbums.call(view);
		},
		events:{
			"click;.btnAdd":function(e){
				brite.display("LiveCreateAlbum", null, {id: null});
			},
			"click;.albumSelf":function(event){
				var view = this;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
				if(id.indexOf("folder") > -1){
					brite.display("LiveAlbums", null, {id:id, isShowPhotos:true, isShowAllPicturesBtn:true});
				}else{
					view.$el.find(".pictureContent").removeClass("hide");
					view.$el.find(".pictureContent .showPicture").append("<img src='"+contextPath+"/liveAlbum/showPicture?id="+id+"' />");
					view.$screen = $("<div class='notTransparentScreen'></div>").insertBefore(".MainScreen"); 
				}
			},
			"click;.glyphicon-remove":function () {
				var view = this;
				view.$el.find(".pictureContent").addClass("hide");
				view.$el.find(".pictureContent .showPicture img").remove();
				$("#bodyPage .notTransparentScreen").remove();
				
			},
			"click; .btnAllPicture": function(event){
				var view = this;
				view.$el.find(".albumsLists .listItem tr").each(function(idx,items){
					$items = $(items);
					var name = $items.find(".albumSelf").text();
					var photoId = $items.attr("data-obj_id");
					view.$el.find(".pictureContent").removeClass("hide");
					view.$el.find(".pictureContent .showPicture").append("<div class='pictureItem'><img src='"+contextPath+"/liveAlbum/showPicture?id="+photoId+"' /><span class='photoName'>Name:"+name+"</span></div>");
				});
			}
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
			if(view.isShowPhotos){
				return app.liveAlbumApi.showPhotos(view.albumId).pipe(function(result){
					return {result: result.result.data};
				});
			}else{
				return app.liveAlbumApi.getUserAlbums().pipe(function(result){
					return {result: result.result.data};
				});
				
			}
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
						return "<a src=\"#\" class=\"albumSelf\">"+obj.name+"</a>";
					},
					attrs: "style='width: 15%; word-break: break-word; cursor:pointer;'"
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
				},
				
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