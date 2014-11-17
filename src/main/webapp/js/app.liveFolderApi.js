var app = app || {};
(function() {
	app.liveFolderApi = {
		getUserFolders : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/liveFolder/getFolders", param);
		},
		getFolder : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/liveFolder/getFolder", param);
		},
		"saveFolder": function (folder) {
            return app.getJsonData(contextPath + "/liveFolder/saveFolder", folder);
        }
	}
})();