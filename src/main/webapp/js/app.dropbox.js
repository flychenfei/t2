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
		}
    };
})();