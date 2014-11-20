var app = app || {};
(function() {
	app.liveAlbumApi = {
		getUserAlbums : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/liveAlbum/getUserAlbums", param);
		},
		getAlbum : function(id) {
			var param = {id:id};
			param.method = "Get";
			return app.getJsonData(contextPath + "/liveAlbum/getAlbum", param);
		},
		deleteAlbum : function(id) {
			var param = {id:id};
			param.method = "Post";
			return app.getJsonData(contextPath + "/liveAlbum/deleteAlbum", param);
		},
		"saveAlbum": function (album) {
            return app.getJsonData(contextPath + "/liveAlbum/saveAlbum", album);
       },
		"showPhotos" : function(id) {
			var param = {id:id};
			param.method = "Get";
			return app.getJsonData(contextPath + "/liveAlbum/showPhotos", param);
		},
	}
})();