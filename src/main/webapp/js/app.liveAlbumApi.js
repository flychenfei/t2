var app = app || {};
(function() {
	app.liveAlbumApi = {
		getUserAlbums : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/liveAlbum/getUserAlbums", param);
		},
		getAlbum : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/liveAlbum/getAlbum", param);
		},
		"saveAlbum": function (album) {
            return app.getJsonData(contextPath + "/liveAlbum/saveAlbum", album);
        }
	}
})();