var app = app || {};
(function(){
	app.dropboxApi={
		getAccountInfo:function(opts){
			var params = opts||{};
        	params.method="Get";
        	return app.getJsonData(contextPath+"/dropbox/getAccountInfo",params);
		},
		getMetadata:function(opts){
			var params = opts||{};
        	params.method="Get";
        	return app.getJsonData(contextPath+"/dropbox/getMetadata",params);
		},
		getFile:function(opts){
			var params = opts||{};
        	params.method="Get";
        	return app.getJsonData(contextPath+"/dropbox/getFile",params);
		},
		createFolder:function(opts){
			var params = opts||{};
			params.method = "Post";
			return app.getJsonData(contextPath+"/dropbox/createFolder",params);
		},
		delete:function(opts){
			var params = opts||{};
			params.method = "Post";
			return app.getJsonData(contextPath+"/dropbox/delete",params);
		},
		share:function(opts){
			var params = opts||{};
			params.method = "Get";
			return app.getJsonData(contextPath+"/dropbox/share",params);
		},
		restore:function(opts){
			var params = opts||{};
			params.method = "Post";
			return app.getJsonData(contextPath+"/dropbox/restore",params);
		},
		getRevisions:function(opts){
			var params = opts||{};
			params.method = "Get";
			return app.getJsonData(contextPath+"/dropbox/revisions",params);
		},
		copy:function(opts){
			var params = opts||{};
			params.method = "Post";
			return app.getJsonData(contextPath+"/dropbox/copy",params);
		},
		move:function(opts){
			var params = opts||{};
			params.method = "Post";
			return app.getJsonData(contextPath+"/dropbox/move",params);
		},
		search:function(opts){
			var params = opts||{};
			params.method = "Get";
			return app.getJsonData(contextPath+"/dropbox/search",params);
		},
		getMedia:function(opts){
			var params = opts||{};
			params.method = "Get";
			return app.getJsonData(contextPath+"/dropbox/getMedia",params);
		}
    };
})();