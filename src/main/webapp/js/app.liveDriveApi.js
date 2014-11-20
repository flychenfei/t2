var app = app || {};
(function() {
	app.liveDriveApi = {
		getRootFolder : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/liveDrive/getRootFolder", param);
		},
		getFolderFilesList : function(id) {
			var param = {id:id};
			param.method = "Get";
			return app.getJsonData(contextPath + "/liveDrive/getFolderFilesList", param);
		},
		get : function(id) {
			var param = {id:id};
			param.method = "Get";
			return app.getJsonData(contextPath + "/liveDrive/get", param);
		},
		delete : function(id) {
			var param = {id:id};
			param.method = "Post";
			return app.getJsonData(contextPath + "/liveDrive/delete", param);
		},
		"save": function (obj) {
			return app.getJsonData(contextPath + "/liveDrive/save", obj);
		},
		"showPhotos" : function(id) {
			var param = {id:id};
			param.method = "Get";
			return app.getJsonData(contextPath + "/liveDrive/showPhotos", param);
		}
	}
})();